import { LinkData, transformAsHtml, transformAsText } from '../javadoc';
import { ElementClass } from '../frankdoc.types';
import { useMemo } from 'react';

type InnerHTML = {
  __html: string;
};

function javadocTransform(
  javadoc: string | undefined,
  elements: Record<string, ElementClass> | null,
  asText = true,
  // eslint-disable-next-line no-unused-vars
  linkTemplate?: (link: LinkData) => string,
): string {
  if (javadoc === '') javadoc = '-';
  if (!javadoc || !elements) return '';

  const hasCustomLinkTransform = !!linkTemplate;
  const javadocParts = asText
    ? transformAsText(javadoc, elements)
    : transformAsHtml(javadoc, elements, hasCustomLinkTransform);

  let transformedResult = '';
  for (const partIndexString in javadocParts) {
    const partIndex = +partIndexString;
    const part = javadocParts[partIndex];
    if (hasCustomLinkTransform && partIndex % 2 !== 0 && part.startsWith('{')) {
      try {
        const linkData: LinkData = JSON.parse(part);
        transformedResult += linkTemplate(linkData);
      } catch (error) {
        console.error("Can't parse link data", error);
      }
      continue;
    }
    transformedResult += part;
  }
  return transformedResult;
}

export function useJavadocTransform(
  javadoc: string | undefined,
  elements: Record<string, ElementClass> | null,
  asText = true,
  // eslint-disable-next-line no-unused-vars
  linkTemplate?: (link: LinkData) => string,
): InnerHTML {
  return useMemo(
    (): InnerHTML => ({
      __html: javadocTransform(javadoc, elements, asText, linkTemplate),
    }),
    [javadoc, elements, asText, linkTemplate],
  );
}
