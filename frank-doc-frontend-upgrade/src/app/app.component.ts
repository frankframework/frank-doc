import { Component, OnInit } from '@angular/core';
import { AppService } from './app.service';
import { Group } from './frankdoc.types';
import { Elements } from './app.types';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  groups: Group[] = [];
  elements: Elements = {};
  loadError?: string;

  constructor(private frankdocService: AppService) { }

  ngOnInit() {
    this.frankdocService.init();
    this.frankdocService.appState$.subscribe(state => {
      this.groups = state.groups;
      this.elements = state.elements;
      this.loadError = state.loadError;
    });
  }
}
