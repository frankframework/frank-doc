import { Component, Input, OnInit } from '@angular/core';
import { AppService } from 'src/app/app.service';
import { Group } from 'src/app/frankdoc.types';
import { Elements } from 'src/app/app.types';

@Component({
  selector: 'sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  @Input() groups!: Group[];
  @Input() elements!: Elements;

  search = "";

  constructor() { }
}
