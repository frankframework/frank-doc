import { KeyValue } from '@angular/common';
import { Component, Input } from '@angular/core';
import { AppService } from 'src/app/app.service';
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
  @Input() search!: string;
  @Input() group?: Group;
  @Input() element?: Element;

  constructor(private appService: AppService) {}

  orderBy = <K, V>(fieldName: keyof V): ((keyValueA: KeyValue<K, V>, keyValueB: KeyValue<K, V>) => number) => this.appService.orderBy<K, V>(fieldName);
}
