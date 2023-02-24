import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, Subscription } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { Group, Element } from 'src/app/frankdoc.types';

@Component({
  selector: 'app-element',
  templateUrl: './element.component.html',
  styleUrls: ['./element.component.scss']
})
export class ElementComponent implements OnInit, OnDestroy {
  version = "";
  element!: Element;

  private subs?: Subscription;

  constructor(private appService: AppService, private route: ActivatedRoute) { }

  javaDocUrlOf = (fullName: string) => this.appService.javaDocUrlOf(fullName);

  ngOnInit() {
    this.subs = combineLatest(
      [this.appService.frankDoc$, this.route.paramMap]
    ).subscribe(([state, paramMap]) => {
      this.version = state.version || "";
      const groups = state.groups,
        // stateGroup = state.group,
        elements = state.elements,
        stateElement = state.element,
        groupParam = paramMap.get('group'),
        elementParam = paramMap.get('element');

      if (groupParam && groups.length > 0) {
        const group = groups.find((group: Group) => group.name === groupParam);
        if (group && elementParam !== stateElement?.name) {
          const groupElementsNames = this.appService.getGroupElements(group.types),
            elementName = groupElementsNames.find(fullname => elements[fullname].name === elementParam),
            element = (elementName && elements[elementName]) || undefined;
          if(element){
            this.element = element;
            this.appService.setGroupAndElement(group, element);
          }
        }
      }
    });
  }

  ngOnDestroy() {
    this.subs?.unsubscribe();
  }

}
