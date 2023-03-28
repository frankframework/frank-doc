import { Pipe, PipeTransform } from '@angular/core';
import { AppService } from '../app.service';
import { Elements } from '../app.types';
import { Group } from '../frankdoc.types';

@Pipe({
  name: 'matchElement'
})
export class MatchElementPipe implements PipeTransform {

  constructor(private appService: AppService) {}

  transform(elements: Elements, searchText?: string, group?: Group) {
    if (!elements || !group)
      return {}; //Cannot filter elements if no group has been selected

    const returnELements: Elements = {},
      matchedParents: { [index: string]: boolean } = {}, // cache matched parents
      noMatchParents: { [index: string]: boolean } = {}, // cache no match parents
      groupMembers = this.appService.getGroupElements( group.types);
    const searchTextLC = searchText && searchText != "" ? searchText.toLowerCase() : undefined;
    for (const elementName of groupMembers) {
      const element = elements[elementName];
      const parentStack = [];
      if (searchTextLC) {
        if (JSON.stringify(element).replace(/"/g, '').toLowerCase().includes(searchTextLC)) {
          returnELements[elementName] = element;
        } else { // search in parent (accessing children is an expensive operation)
          let elementParentName = elements[elementName].parent;
          while (elementParentName) {
            parentStack.push(elementParentName); // keep list of unmatched parents
            if (matchedParents[elementParentName]) { // if parent matched already leave the loop
              returnELements[elementName] = element;
              break;
            } else if (noMatchParents[elementParentName]) { // if parent has no match leave the loop
              break;
            }
            const parentElement = elements[elementParentName];
            if (JSON.stringify(parentElement).replace(/"/g, '').toLowerCase().includes(searchTextLC)) {
              returnELements[elementName] = element;
              matchedParents[elementParentName] = true;
              break;
            }
            if (!elements[elementParentName].parent) {
              for (const t of parentStack) {
                noMatchParents[t] = true;
              }
              break;
            }
            elementParentName = elements[elementParentName].parent;
          }
        }
      } else {
        returnELements[elementName] = element;
      }
    }
    return returnELements;
  }

}
