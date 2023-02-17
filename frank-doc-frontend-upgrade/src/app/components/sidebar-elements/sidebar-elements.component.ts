import { Component, Input } from '@angular/core';
import { Elements } from 'src/app/app.types';

@Component({
  selector: 'sidebar-elements',
  templateUrl: './sidebar-elements.component.html',
  styleUrls: ['./sidebar-elements.component.scss']
})
export class SidebarElementsComponent {

  @Input() elements!: Elements;

}
