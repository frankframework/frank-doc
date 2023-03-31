import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'asText'
})
export class AsTextPipe implements PipeTransform {

  transform(value?: string) {
    if (!value) return "";
    value = value.replaceAll('\\"', '"');
    value = value.replace(/<[^>]*>?/gm, '');
    value = value.replace(/(?:{@link\s(.*?)})/g, function (match, captureGroup: string) {
      // {@link PipeLineSession pipeLineSession} -> 'PipeLineSession pipeLineSession'
      // {@link IPipe#configure()} -> 'IPipe#configure()'
      // {@link #doPipe(Message, PipeLineSession) doPipe} -> '#doPipe(Message, PipeLineSession) doPipe'
      const hashPos = captureGroup.indexOf("#"),
        isMethod = hashPos > -1,
        element = isMethod ? captureGroup.split("#")[0] : captureGroup;

      if (element == '') { //if there is no element ref then it's a method
        const methodName = captureGroup.slice(hashPos),
          methodLabelSplit = methodName.split(") ");

        if (methodLabelSplit.length == 2) return methodLabelSplit[1]; //return method label
        return methodName.slice(1, methodName.indexOf("("));
      }

      const elementParts = element.split(" "), //first part is the class name, 2nd part the written name
        classNameOrType = elementParts[elementParts.length - 1];
      if (isMethod) {
        const method = captureGroup.split("#")[1],
          methodNameOrLabel = (method.slice(method.indexOf(") ") + 1)).trim();
        return `${classNameOrType}.${methodNameOrLabel}`;
      }
      return classNameOrType;
    });
    return value;
  }
}
