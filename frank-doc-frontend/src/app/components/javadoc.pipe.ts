import { inject, Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { AppService } from '../app.service';
import { Element, FrankDoc } from '../frankdoc.types';

@Pipe({
  name: 'javadoc',
  standalone: true,
})
export class JavadocPipe implements PipeTransform {
  private appService: AppService = inject(AppService);
  private domSanatizer: DomSanitizer = inject(DomSanitizer);

  transform(value: string, elements: FrankDoc['elements'] | null): SafeHtml | string {
    if (value === '' || !elements) return value;
    const markdownLinkRegex = /\[(.*?)]\((.+?)\)/g;
    const linkRegex = /(?:{@link\s(.*?)})/g;
    value = value.replace(markdownLinkRegex, '<a target="_blank" href="$2" alt="$1">$1</a>'); // old regex: /\[(.*?)\]\((.+?)\)/g
    value = value.replaceAll('\\"', '"');
    value = value.replace(linkRegex, (_, captureGroup) => this.transformLink(captureGroup, elements));
    return this.domSanatizer.bypassSecurityTrustHtml(value);
  }

  transformLink(captureGroup: string, elements: FrankDoc['elements']): string {
    // {@link PipeLineSession pipeLineSession} -> 'PipeLineSession pipeLineSession'
    // {@link IPipe#configure()} -> 'IPipe#configure()'
    // {@link #doPipe(Message, PipeLineSession) doPipe} -> '#doPipe(Message, PipeLineSession) doPipe'
    const hashPosition = captureGroup.indexOf('#'),
      isMethod = hashPosition > -1,
      elementString = isMethod ? captureGroup.split('#')[0] : captureGroup;

    if (elementString === '') {
      //if there is no element ref then it's an internal method
      return this.getInternalMethodReference(captureGroup, hashPosition);
    }

    const elementParts = elementString.split(' '); //first part is the class name, 2nd part the written name
    const name = this.parseLinkName(elementParts, isMethod, captureGroup);

    const element = this.findElement(elements, elementParts[0]);
    if (!element) return name;
    return `<a href="#/${element.fullName}">${name}</a>`;
  }

  getInternalMethodReference(captureGroup: string, hashPosition: number): string {
    const method = captureGroup.slice(hashPosition),
      methodParts = method.split(' ');
    return methodParts.length === 2
      ? methodParts[1] // 'methodName label' -> 'label'
      : method.slice(1, method.indexOf('('));
  }

  parseLinkName(elementParts: string[], isMethod: boolean, captureGroup: string): string {
    const elementName = elementParts[elementParts.length - 1]; // element name/label
    if (isMethod) {
      const method = captureGroup.split('#')[1],
        methodNameOrLabel = method.slice(method.indexOf(') ') + 1).trim();
      return methodNameOrLabel.includes(' ') ? method.split(' ')[1] : `${elementName}.${methodNameOrLabel}`;
    }
    return elementName;
  }

  findElement(allElements: FrankDoc['elements'], simpleName: string): Element | null {
    if (!allElements || Object.keys(allElements).length === 0) return null; //Cannot find anything if we have nothing to search in
    for (const element in allElements) {
      if (this.appService.fullNameToSimpleName(element) === simpleName) {
        return allElements[element];
      }
    }

    console.warn(`could not find element [${simpleName}]`);
    return null;
  }
}
