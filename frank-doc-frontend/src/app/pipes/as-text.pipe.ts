import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'asText'
})
export class AsTextPipe implements PipeTransform {

  transform(value?: string) {
    if (!value) return "";
    value = value.replaceAll('\\"', '"');
    value = value.replace(/<[^>]*>?/gm, '');
    value = value.replace(/(?:{@link\s(.*?)})/g, function (match, captureGroup) {
      // {@link PipeLineSession pipeLineSession}
      // {@link IPipe#configure()}
      // {@link #doPipe(Message, PipeLineSession) doPipe}
      let referencedElement = captureGroup;
      const hash = captureGroup.indexOf("#");
      if (hash > -1) {
        referencedElement = captureGroup.split("#")[0];

        if (referencedElement == '') { //if there is no element ref then it's a method
          const method = captureGroup.slice(hash);
          const nameOrAlias = method.split(") ");
          if (nameOrAlias.length == 2) {
            return nameOrAlias[1]; //If it's an alias
          }
          return method.slice(1, method.indexOf("("));
        }
      }
      const captures = referencedElement.split(" "); //first part is the ClassName, 2nd part the written name
      let name = captures[captures.length - 1];
      if (hash > -1) {
        const method = captureGroup.split("#")[1];
        name = name + "." + (method.slice(method.indexOf(") ") + 1)).trim();
      }
      return name;
    });
    return value;
  }

}
