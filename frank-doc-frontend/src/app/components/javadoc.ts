import { Element, FrankDoc } from '../frankdoc.types';
import { AppService } from '../app.service';

export type LinkData = { href: string; text: string };

export const getLinkData = function (
  captureGroup: string,
  elements: FrankDoc['elements'],
  appService: AppService,
): LinkData | string {
  /* {@link PipeLineSession pipeLineSession} -> 'PipeLineSession pipeLineSession'
   * {@link IPipe#configure()} -> 'IPipe#configure()'
   * {@link #doPipe(Message, PipeLineSession) doPipe} -> '#doPipe(Message, PipeLineSession) doPipe'
   */

  const hashPosition = captureGroup.indexOf('#'),
    isMethod = hashPosition > -1,
    elementString = isMethod ? captureGroup.split('#')[0] : captureGroup;

  if (elementString === '') {
    return getInternalMethodReference(captureGroup, hashPosition);
  }

  const elementParts = elementString.split(' '); //first part is the class name, 2nd part the written name
  const name = parseLinkName(elementParts, isMethod, captureGroup);

  const element = findElement(elements, appService, elementParts[0]);
  if (!element) return name;
  return { href: `${element.fullName}`, text: name };
};

export const getInternalMethodReference = function (captureGroup: string, hashPosition: number): string {
  const method = captureGroup.slice(hashPosition),
    methodParts = method.split(' ');
  return methodParts.length === 2
    ? methodParts[1] // 'methodName label' -> 'label'
    : method.slice(1, method.indexOf('('));
};

export const parseLinkName = function (elementParts: string[], isMethod: boolean, captureGroup: string): string {
  const elementName = elementParts[elementParts.length - 1]; // element name/label
  if (isMethod) {
    const method = captureGroup.split('#')[1],
      methodNameOrLabel = method.slice(method.indexOf(') ') + 1).trim();
    return methodNameOrLabel.includes(' ') ? method.split(' ')[1] : `${elementName}.${methodNameOrLabel}`;
  }
  return elementName;
};

export const findElement = function (
  allElements: FrankDoc['elements'],
  appService: AppService,
  simpleName: string,
): Element | null {
  if (!allElements || Object.keys(allElements).length === 0) return null; //Cannot find anything if we have nothing to search in
  for (const element in allElements) {
    if (appService.fullNameToSimpleName(element) === simpleName) {
      return allElements[element];
    }
  }

  console.warn(`could not find element [${simpleName}]`);
  return null;
};
