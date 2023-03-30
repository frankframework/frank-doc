import { Component, isDevMode, OnInit } from '@angular/core';
import { AppService } from './app.service';
import { Element, Group } from './frankdoc.types';
import { Elements } from './app.types';
import { NavigationStart, Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  groups: Group[] = [];
  elements: Elements = {};
  loadError?: string;
  showDeprecatedElements = false;
  showInheritance = false;
  group?: Group;
  element?: Element;

  constructor(private appService: AppService, private router: Router) {}

  ngOnInit() {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart && event.url && /^\/!/.test(event.url)){
        this.router.navigate([event.url.replace('/!', '')]);
      }
    });

    this.appService.init();
    this.appService.frankDoc$.subscribe(state => {
      if (isDevMode())
        console.log(state);

      this.groups = state.groups;
      this.elements = state.elements;
      this.loadError = state.loadError;
      this.showDeprecatedElements = state.showDeprecatedElements;
      this.showInheritance = state.showInheritance;
      this.group = state.group;
      this.element = state.element;
    });
  }
}
