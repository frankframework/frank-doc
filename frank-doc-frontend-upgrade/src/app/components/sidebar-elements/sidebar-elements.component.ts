import { Component, Input } from '@angular/core';
import { Elements } from 'src/app/app.types';
import { Group, Element } from 'src/app/frankdoc.types';

@Component({
  selector: 'sidebar-elements',
  templateUrl: './sidebar-elements.component.html',
  styleUrls: ['./sidebar-elements.component.scss']
})
export class SidebarElementsComponent {

  @Input() elements!: Elements;
  @Input() showDeprecatedElements!: boolean;
  @Input() showInheritance!: boolean;
  @Input()
  set group(group: Group | undefined){
    this.groupName = group?.name || "All";
  }
  @Input()
  set element(element: Element | undefined){
    this.elementName = element?.name || "";
  }

  groupName = "All";
  elementName = "";
}