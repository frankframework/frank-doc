import { ElementClass } from './frankdoc.types';

export type LinkData = { text: string; href?: string };

// eslint-disable-next-line sonarjs/slow-regex
export const markdownLinkRegex = /\[([^\]]+)]\(([^)]+)\)/g; // old regex: /\[(.*?)\]\((.+?)\)/g
export const tagsRegex = /<[^>]*>?/gm;
export const linkRegex = /(?:{@link\s(.*?)})/g;

export function transformAsHtml(
  javadoc: string,
  elements: Record<string, ElementClass>,
  hasCustomLinkTransform: boolean,
): string[] {
  let value = `${javadoc}`;
  value = value.replaceAll(markdownLinkRegex, '<a target="_blank" href="$2" alt="$1">$1</a>');

  if (hasCustomLinkTransform) {
    value = value.replaceAll(linkRegex, (_, captureGroup) => {
      const linkData = getLinkData(captureGroup, elements);
      if (linkData.href) return `\\"${JSON.stringify(linkData)}\\"`;
      return linkData.text;
    });
    return value.split(String.raw`\"`);
  }
  value = value.replaceAll(linkRegex, (_, captureGroup) => {
    const linkData = getLinkData(captureGroup, elements);
    if (linkData.href) return defaultLinkTransformation(linkData);
    return linkData.text;
  });

  value = value.replaceAll(String.raw`\"`, '"');
  return [value];
}

export function transformAsText(javadoc: string, elements: Record<string, ElementClass>): string[] {
  let value = `${javadoc}`;
  value = value.replaceAll(markdownLinkRegex, '$1($2)');
  value = value.replaceAll(tagsRegex, '');
  value = value.replaceAll(linkRegex, (_: string, captureGroup: string) => {
    const linkData = getLinkData(captureGroup, elements);
    if (linkData.href) return `${linkData.text}(${linkData.href})`;
    return linkData.text;
  });
  value = value.replaceAll(String.raw`\"`, '"');
  return [value];
}

export function defaultLinkTransformation(linkData: LinkData): string {
  return `<a href="#/${linkData.href}">${linkData.text}</a>`;
}

export function getLinkData(captureGroup: string, elements: Record<string, ElementClass>): LinkData {
  /* {@link PipeLineSession pipeLineSession} -> 'PipeLineSession pipeLineSession'
   * {@link IPipe#configure()} -> 'IPipe#configure()'
   * {@link #doPipe(Message, PipeLineSession) doPipe} -> '#doPipe(Message, PipeLineSession) doPipe'
   */

  const hashPosition = captureGroup.indexOf('#'),
    isMethod = hashPosition !== -1,
    elementString = isMethod ? captureGroup.split('#')[0] : captureGroup;

  if (elementString === '') {
    return { text: getInternalMethodReference(captureGroup, hashPosition) };
  }

  const elementParts = elementString.split(' '); //first part is the class name, 2nd part the written name
  const name = parseLinkName(elementParts, isMethod, captureGroup);

  const element = findElement(elements, elementParts[0]);
  if (!element) return { text: name };
  return { href: element.name, text: name };
}

export function getInternalMethodReference(captureGroup: string, hashPosition: number): string {
  const method = captureGroup.slice(hashPosition),
    methodParts = method.split(' ');
  return methodParts.length === 2
    ? methodParts[1] // 'methodName label' -> 'label'
    : method.slice(1, method.indexOf('('));
}

export function parseLinkName(elementParts: string[], isMethod: boolean, captureGroup: string): string {
  const elementName = elementParts.at(-1)!; // element name/label
  if (isMethod) {
    const method = captureGroup.split('#')[1],
      methodNameOrLabel = method.slice(method.indexOf(') ') + 1).trim();
    return methodNameOrLabel.includes(' ') ? method.split(' ')[1] : `${elementName}.${methodNameOrLabel}`;
  }
  return elementName;
}

export function findElement(elements: Record<string, ElementClass>, simpleName: string): ElementClass | null {
  if (Object.keys(elements).length === 0) return null;
  const element = elements[simpleName];
  if (element) return element;

  console.warn(`could not find element [${simpleName}]`);
  return null;
}
