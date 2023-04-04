import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'asText'
})
export class AsTextPipe implements PipeTransform {

  transform(value?: string) {
    if (!value) return "";
    value = value.replaceAll('\\"', '"');
    value = value.replace(/<[^>]*>?/gm, '');
    value = value.replace(/(?:{@link\s(.*?)})/g, (match, captureGroup: string) => {
      // {@link PipeLineSession pipeLineSession} -> 'PipeLineSession pipeLineSession'
      // {@link IPipe#configure()} -> 'IPipe#configure()'
      // {@link #doPipe(Message, PipeLineSession) doPipe} -> '#doPipe(Message, PipeLineSession) doPipe'
      const hashPos = captureGroup.indexOf("#"),
        isMethod = hashPos > -1,
        element = isMethod ? captureGroup.split("#")[0] : captureGroup;

      if (element == '') { //if there is no element ref then it's a method
        return this.transformInternalMethod(captureGroup, hashPos);
      }

      const elementParts = element.split(" "); //first part is the class name, 2nd part the written name
      return this.name(elementParts, isMethod, captureGroup);
    });
    return value;
  }

  transformInternalMethod(captureGroup: string, hashPos: number) {
    const methodName = captureGroup.slice(hashPos),
      methodLabelSplit = methodName.split(" ");

    if (methodLabelSplit.length == 2) return methodLabelSplit[1]; //return method label
    return methodName.slice(1, methodName.indexOf("("));
  }

  name(elementParts: string[], isMethod: boolean, captureGroup: string) {
    const elementName = elementParts[elementParts.length - 1]; // element name/label
    if (isMethod) {
      const method = captureGroup.split("#")[1],
        methodNameOrLabel = (method.slice(method.indexOf(") ") + 1)).trim();
      return methodNameOrLabel.includes(" ") ? method.split(" ")[1] : `${elementName}.${methodNameOrLabel}`;
    }
    return elementName;
  }
}
