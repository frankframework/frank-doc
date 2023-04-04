import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { AppService } from '../app.service';
import { Elements } from '../app.types';

@Pipe({
  name: 'javadoc'
})
export class JavadocPipe implements PipeTransform {

  constructor(private appService: AppService, private domSanatizer: DomSanitizer) {}

  transform(value: string | undefined, elements: Elements) {
    if (!value || !elements) return "";
    value = value.replace(/\[(.*?)]\((.+?)\)/g, '<a target="_blank" href="$2" alt="$1">$1</a>'); // old regex: /\[(.*?)\]\((.+?)\)/g
    value = value.replaceAll('\\"', '"');
    value = value.replace(/(?:{@link\s(.*?)})/g, (match, captureGroup: string) => {
      // {@link PipeLineSession pipeLineSession} -> 'PipeLineSession pipeLineSession'
      // {@link IPipe#configure()} -> 'IPipe#configure()'
      // {@link #doPipe(Message, PipeLineSession) doPipe} -> '#doPipe(Message, PipeLineSession) doPipe'
      const hashPos = captureGroup.indexOf("#"),
        isMethod = hashPos > -1,
        elementString = isMethod ? captureGroup.split("#")[0] : captureGroup;

      if (elementString == '') { //if there is no element ref then it's an internal method
        return this.transformInternalMethod(captureGroup, hashPos);
      }

      const elementParts = elementString.split(" "); //first part is the class name, 2nd part the written name
      const name = this.parseLinkName(elementParts, isMethod, captureGroup);

      const element = this.findElement(elements, elementParts[0]);
      if (!element) return name;
      return `<a href="/#/All/${element.name}">${name}</a>`;
    });

    return this.domSanatizer.bypassSecurityTrustHtml(value);
  }

  transformInternalMethod(captureGroup: string, hashPos: number){
    const methodName = captureGroup.slice(hashPos),
      methodLabelSplit = methodName.split(" ");

    if (methodLabelSplit.length == 2) return methodLabelSplit[1]; //return method label
    return methodName.slice(1, methodName.indexOf("("));
  }

  parseLinkName(elementParts: string[], isMethod: boolean, captureGroup: string){
    const elementName = elementParts[elementParts.length - 1]; // element name/label
    if (isMethod) {
      const method = captureGroup.split("#")[1],
        methodNameOrLabel = (method.slice(method.indexOf(") ") + 1)).trim();
      return methodNameOrLabel.includes(" ") ? method.split(" ")[1] : `${elementName}.${methodNameOrLabel}`;
    }
    return elementName;
  }

  findElement(allElements: Elements, simpleName: string) {
    if (!allElements || Object.keys(allElements).length === 0) return null; //Cannot find anything if we have nothing to search in
    for (const element in allElements) {
      if (this.appService.fullNameToSimpleName(element) == simpleName) {
        return allElements[element];
      }
    }

    console.warn("could not find element [" + simpleName + "]");
    return null;
  }

}
