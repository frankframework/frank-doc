import { Component, Input } from '@angular/core';
import { AppService } from 'src/app/app.service';
import { Elements, Types } from 'src/app/app.types';
import { Child, Group } from 'src/app/frankdoc.types';

@Component({
  selector: 'element-children',
  templateUrl: './element-children.component.html',
  styleUrls: ['./element-children.component.scss']
})
export class ElementChildrenComponent {

  @Input() children!: Child[];

  @Input() groups!: Group[];
  @Input() elements!: Elements;
  @Input() types!: Types;
  @Input() showDeprecatedElements!: boolean;

  constructor(private appService: AppService) { }

  getTitle(child: Child) {
    let title = 'No child elements, only text';
    if (child.type) {
      const groups = this.getGroupsOfType(child.type, this.groups);
      const childElements = this.getElementsOfType(child.type);
      title = 'From ' + groups +  ': ' + childElements.join(', ');
    }
    return title;
  }

  // Exclude group All.
  getGroupsOfType(type: string, groups: Group[]) {
    for (let i = 1; i < groups.length; ++i) {
      const group = groups[i];
      if (group.types.includes(type)) {
        return group.name;
      }
    }
    return null;
  }

  getElementsOfType(type: string) {
    const fullNames = this.types[type];
    if (this.showDeprecatedElements) {
      return fullNames.map(fullName => this.appService.fullNameToSimpleName(fullName));
    }
    if(!fullNames)
      debugger;
    return fullNames.reduce<string[]>((acc, fullName) => {
      if (this.elements && !this.elements[fullName].deprecated) {
        acc.push(this.appService.fullNameToSimpleName(fullName))
      }
      return acc;
    }, []);
  }
}