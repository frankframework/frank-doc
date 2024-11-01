import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'asText',
  standalone: true,
})
export class AsTextPipe implements PipeTransform {
  transform(value: string): string {
    if (value === '') return value;
    const tagsRegex = /<[^>]*>?/gm;
    const linkRegex = /(?:{@link\s(.*?)})/g;
    value = value.replaceAll('\\"', '"');
    value = value.replace(tagsRegex, '');
    value = value.replace(linkRegex, (_: string, captureGroup: string) => this.transformLink(captureGroup));
    return value;
  }

  transformLink(captureGroup: string): string {
    // {@link PipeLineSession pipeLineSession} -> 'PipeLineSession pipeLineSession'
    // {@link IPipe#configure()} -> 'IPipe#configure()'
    // {@link #doPipe(Message, PipeLineSession) doPipe} -> '#doPipe(Message, PipeLineSession) doPipe'
    const hashPosition = captureGroup.indexOf('#'),
      isMethod = hashPosition > -1,
      element = isMethod ? captureGroup.split('#')[0] : captureGroup;

    if (element === '') {
      //if there is no element ref then it's a method
      return this.getInternalMethodReference(captureGroup, hashPosition);
    }

    const elementParts = element.split(' '); //first part is the class name, 2nd part the written name
    return this.getDisplayName(elementParts, isMethod, captureGroup);
  }

  getInternalMethodReference(captureGroup: string, hashPosition: number): string {
    const method = captureGroup.slice(hashPosition),
      methodParts = method.split(' ');
    return methodParts.length === 2
      ? methodParts[1] // 'methodName label' -> 'label'
      : method.slice(1, method.indexOf('('));
  }

  getDisplayName(elementParts: string[], isMethod: boolean, captureGroup: string): string {
    const elementName = elementParts[elementParts.length - 1]; // element name/label
    if (isMethod) {
      const method = captureGroup.split('#')[1],
        labelOrMethodName = method.slice(method.indexOf(') ') + 1).trim();
      return labelOrMethodName.includes(' ') ? method.split(' ')[1] : `${elementName}.${labelOrMethodName}`;
    }
    return elementName;
  }
}
