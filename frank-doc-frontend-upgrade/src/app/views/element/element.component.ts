import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, Subscription } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { Elements, Types } from 'src/app/app.types';
import { Group, Element } from 'src/app/frankdoc.types';

@Component({
  selector: 'app-element',
  templateUrl: './element.component.html',
  styleUrls: ['./element.component.scss']
})
export class ElementComponent implements OnInit, OnDestroy {
  version = "";
  groups: Group[] = [];
  elements: Elements = {};
  types: Types = {};
  showDeprecatedElements = false;
  showInheritance = false;
  element?: Element | null;

  private subs?: Subscription;

  constructor(private appService: AppService, private route: ActivatedRoute) { }

  javaDocUrlOf = (fullName: string) => this.appService.javaDocUrlOf(fullName);

  ngOnInit() {
    this.subs = combineLatest(
      [this.appService.frankDoc$, this.route.paramMap]
    ).subscribe(([state, paramMap]) => {
      this.version = state.version || "";
      const groups = state.groups,
        elements = state.elements,
        stateElement = state.element,
        groupParam = paramMap.get('group'),
        elementParam = paramMap.get('element');

      this.groups = groups;
      this.elements = elements;
      this.types = state.types;
      this.showDeprecatedElements = state.showDeprecatedElements;
      this.showInheritance = state.showInheritance;

      if(stateElement){
        this.element = stateElement;
      }

      if (groupParam && groups.length > 0) {
        const group = groups.find((group: Group) => group.name === groupParam);
        if (group && elementParam !== stateElement?.name) {
          const groupElementsNames = this.appService.getGroupElements(group.types),
            elementName = groupElementsNames.find(fullname => elements[fullname].name === elementParam),
            element = (elementName && elements[elementName]) || undefined;
          if(element){
            setTimeout(() => {
              // pushes the element to the state, which will trigger this subscription to update again
              this.appService.setGroupAndElement(group, element);
            })
            return;
          }
          this.element = null;
        }
      }
    });
  }

  ngOnDestroy() {
    this.subs?.unsubscribe();
  }

}
