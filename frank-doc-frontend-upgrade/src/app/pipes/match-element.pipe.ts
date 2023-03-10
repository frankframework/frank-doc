import { Pipe, PipeTransform } from '@angular/core';
import { AppService } from '../app.service';
import { Elements } from '../app.types';
import { Group } from '../frankdoc.types';

@Pipe({
  name: 'matchElement'
})
export class MatchElementPipe implements PipeTransform {

  constructor(private appService: AppService) { }

  transform(elements: Elements, searchText?: string, group?: Group) {
    if (!elements || !group)
      return {}; //Cannot filter elements if no group has been selected

    const r: Elements = {},
      matchedParents: { [index: string]: boolean } = {}, // cache matched parents
      noMatchParents: { [index: string]: boolean } = {}, // cache no match parents
      groupMembers = this.appService.getGroupElements( group.types);
    let searchTextLC = searchText && searchText != "" ? searchText.toLowerCase() : null;
    for (const element of groupMembers) {
      const obj = elements[element];
      const parentStack = [];
      if (searchTextLC) {
        if (JSON.stringify(obj).replace(/"/g, '').toLowerCase().indexOf(searchTextLC) > -1) {
          r[element] = obj;
        } else { // search in parent (accessing children is an expensive operation)
          let elementParent = elements[element].parent;
          while (elementParent) {
            parentStack.push(elementParent); // keep list of unmatched parents
            if (matchedParents[elementParent]) { // if parent matched already leave the loop
              r[element] = obj;
              break;
            } else if (noMatchParents[elementParent]) { // if parent has no match leave the loop
              break;
            }
            const parentObj = elements[elementParent];
            if (JSON.stringify(parentObj).replace(/"/g, '').toLowerCase().indexOf(searchTextLC) > -1) {
              r[element] = obj;
              matchedParents[elementParent] = true;
              break;
            }
            if (!elements[elementParent].parent) {
              for (const t of parentStack) {
                noMatchParents[t] = true;
              }
              break;
            }
            elementParent = elements[elementParent].parent;
          }
        }
      } else {
        r[element] = obj;
      }
    }
    return r;
  }

}
