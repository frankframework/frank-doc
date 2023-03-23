import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { AppService } from '../app.service';
import { Elements } from '../app.types';

@Pipe({
  name: 'javadoc'
})
export class JavadocPipe implements PipeTransform {

  constructor(private appService: AppService, private domSanatizer: DomSanitizer) { }

  transform(value: string | undefined, elements: Elements) {
    if (!value || !elements) return "";
    value = value.replace(/\[(.*?)\]\((.+?)\)/g, '<a target="_blank" href="$2" alt="$1">$1</a>');
    value = value.replaceAll('\\"', '"');
    value = value.replace(/(?:{@link\s(.*?)})/g, (match, captureGroup: string) => {
      // {@link PipeLineSession pipeLineSession}
      // {@link IPipe#configure()}
      // {@link #doPipe(Message, PipeLineSession) doPipe}
      let referencedElement = captureGroup;
      const hash = captureGroup.indexOf("#");
      if (hash > -1) {
        referencedElement = captureGroup.split("#")[0];

        if (referencedElement == '') { //if there is no element ref then it's a method
          const method = captureGroup.substring(hash);
          const nameOrAlias = method.split(") ");
          if (nameOrAlias.length == 2) {
            return nameOrAlias[1]; //If it's an alias
          }
          return method.substring(1, method.indexOf("("));
        }
      }
      const captures = referencedElement.split(" "); //first part is the ClassName, 2nd part the written name
      let name = captures[captures.length - 1];
      if (hash > -1) {
        const method = captureGroup.split("#")[1];
        name = name + "." + (method.substring(method.indexOf(") ") + 1)).trim();
      }
      const element = this.findElement(elements, captures[0]);
      if (!element) {
        return name;
      }
      return `<a href="/#/All/${element.name}">${name}</a>`;
    });

    return this.domSanatizer.bypassSecurityTrustHtml(value);
  }

  findElement(allElements: Elements, simpleName: string) {
    if (!allElements || Object.keys(allElements).length < 1) return null; //Cannot find anything if we have nothing to search in
    for (const element in allElements) {
      if (this.appService.fullNameToSimpleName(element) == simpleName) {
        return allElements[element];
      }
    }

    console.warn("could not find element [" + simpleName + "]");
    return null;
  }

}
