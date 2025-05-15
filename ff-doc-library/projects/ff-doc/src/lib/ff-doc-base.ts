import { ElementInfo, FFDocJson } from './frankdoc.types';
import { addInheritedPropertiesToElement, ElementClassWithInheritedProperties } from './frankdoc.utilities';

export type Filters = Record<string, FilterLabels>;
export type FilterLabels = Record<string, string[]>;
export type Elements = Record<string, ElementDetails>;
export type ElementDetails = ElementClassWithInheritedProperties & {
  labels: ElementInfo['labels'];
};

export abstract class FFDocBase {
  protected getFiltersFromLabels(labels: FFDocJson['labels']): Filters {
    const filters: Filters = {};
    for (const [filterGroup, filterLabels] of Object.entries(labels)) {
      filters[filterGroup] = {};
      for (const label of filterLabels) {
        filters[filterGroup][label] = [];
      }
    }
    return filters;
  }

  protected assignFrankDocElementsToFilters(filters: Filters, elements: FFDocJson['elementNames']): Filters {
    for (const [elementName, element] of Object.entries(elements)) {
      for (const elementFilterGroup in element.labels) {
        const elementFilterLabel = element.labels[elementFilterGroup];
        if (!filters[elementFilterGroup] || !filters[elementFilterGroup][elementFilterLabel]) continue;
        filters[elementFilterGroup][elementFilterLabel].push(elementName);
      }
    }
    return filters;
  }

  protected getXMLElements(json: FFDocJson): Elements {
    const elements: Elements = {};

    /* instead of looping over every xml element,
     * maybe parent elements already processed should be used as cache in order to reduce the number of loops
     */
    for (const [elementName, elementValue] of Object.entries(json.elementNames)) {
      const elementClassData = json.elements[elementValue.className];
      elements[elementName] = {
        ...addInheritedPropertiesToElement(elementClassData, json.elements, json.enums),
        labels: elementValue.labels,
      };
    }
    return elements;
  }
}
