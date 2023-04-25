import { Pipe, PipeTransform } from '@angular/core';
import { AppService } from '../app.service';
import { Elements, Element } from '../app.types';
import { Group } from '../frankdoc.types';

@Pipe({
  name: 'matchElement'
})
export class MatchElementPipe implements PipeTransform {

  constructor(private appService: AppService) {}

  transform(elements: Elements, searchText?: string, group?: Group): Elements {
    if (!group) return {}; //Cannot filter elements if no group has been selected

    const matchedParentsCache: Record<string, boolean> = {}, // cache matched parents
      unmatchedParentsCache: Record<string, boolean> = {}, // cache no match parents
      groupMembers = this.appService.getGroupElements(group.types),
      searchTerm = searchText && searchText != "" ? searchText.toLowerCase() : undefined;
    let matchedElements: Elements = {}

    for (const elementName of groupMembers){
      const element = elements[elementName];

      if (!searchTerm) {
        matchedElements[elementName] = element;
        continue;
      }
      if (this.elementToJSON(element).includes(searchTerm)) {
        matchedElements[elementName] = element;
        continue;
      }

      // caches will be updated inside function since they use the same reference
      const matchedParents = this.processParents(elements, unmatchedParentsCache, matchedParentsCache, searchTerm, elementName, element);
      matchedElements = {...matchedElements, ...matchedParents};
    }

    return matchedElements;
  }

  elementToJSON(element: Element): string {
    return JSON.stringify(element).replace(/"/g, '').toLowerCase();
  }

  processParents(elements: Elements, unmatchedParentsCache: Record<string, boolean>, matchedParentsCache: Record<string, boolean>, searchTerm: string, elementName: string, element: Element): Elements {
    const processedParents = [],
      matchedElements: Elements = {};
    let elementParentName = elements[elementName].parent;
    while (elementParentName) {
      processedParents.push(elementParentName);

      if (unmatchedParentsCache[elementParentName]) break;
      if (matchedParentsCache[elementParentName]) {
        matchedElements[elementName] = element;
        break;
      }

      const parentElement = elements[elementParentName];

      if (this.elementToJSON(parentElement).includes(searchTerm)) {
        matchedElements[elementName] = element;
        matchedParentsCache[elementParentName] = true;
        break;
      }

      if (!parentElement.parent) {
        for (const parentName of processedParents) {
          unmatchedParentsCache[parentName] = true;
        }
        break;
      }
      elementParentName = parentElement.parent;
    }
    return matchedElements;
  }

}
