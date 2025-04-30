import { Component, computed, HostListener, inject, OnInit, Signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Element, FrankDoc } from '../../frankdoc.types';
import { AppService } from '../../app.service';
import { DetailsElementComponent } from './details-element/details-element.component';
import { DetailsSearchComponent } from './details-search/details-search.component';
import { NgClass } from '@angular/common';

type ElementFilterProperties = {
  filterName?: string;
  labelName: string;
  elementName: string;
};

type TOCEntry = {
  name: string;
  anchor: string;
  active: boolean;
  children?: TOCEntry[];
};

export type HasInheritedProperties = {
  required: boolean;
  optional: boolean;
  forwards: boolean;
};

@Component({
  selector: 'app-details',
  imports: [DetailsElementComponent, DetailsSearchComponent, NgClass],
  templateUrl: './details.component.html',
  styleUrl: './details.component.scss',
})
export class DetailsComponent implements OnInit {
  protected readonly frankDocElements: Signal<FrankDoc['elements'] | null> = computed(
    () => this.appService.frankDoc()?.elements ?? null,
  );
  protected readonly frankDocEnums: Signal<FrankDoc['enums'] | null> = computed(
    () => this.appService.frankDoc()?.enums ?? null,
  );
  protected elementByRoute: Element | null = null;
  protected readonly elementBySignal: Signal<Element | null> = computed(() => {
    const elements = this.frankDocElements();
    if (elements) return this.findElement(elements);
    return null;
  });
  protected hasInheritedProperties: HasInheritedProperties = {
    required: false,
    optional: false,
    forwards: false,
  };
  protected tableOfContents: TOCEntry[] = [];
  protected showNavigation: boolean = false;

  private appService: AppService = inject(AppService);
  protected readonly scrollToElement = this.appService.scrollToElement;

  private route: ActivatedRoute = inject(ActivatedRoute);
  private elementFilterProperties: ElementFilterProperties | null = null;
  private elementIndexOrClassName: string | null = null;

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const indexOrClassName: string | undefined = params['name'];
      const filterName: string | undefined = params['filter'];
      const labelName: string | undefined = params['label'];
      const elementName: string | undefined = params['element'];

      if (indexOrClassName) {
        this.elementIndexOrClassName = indexOrClassName;
      } else if (labelName && elementName) {
        this.elementFilterProperties = { filterName, labelName, elementName };
      }

      this.elementByRoute = this.findElement(this.frankDocElements());
    });
  }

  @HostListener('window:scroll')
  updateActiveTOC(): void {
    let activeIndex = 0;
    for (const key in this.tableOfContents) {
      const index = Number(key);
      const entry = this.tableOfContents[index];
      const elementRef = document.querySelector(entry.anchor);

      entry.active = false;
      if (elementRef && elementRef.getBoundingClientRect().top < 30) {
        activeIndex = index;
      }
    }
    this.tableOfContents[activeIndex].active = true;
  }

  protected getFoundElement(): Element | null {
    return this.elementByRoute ?? this.elementBySignal();
  }

  protected updateElementDetailInfo(info: HasInheritedProperties): void {
    this.hasInheritedProperties = info;
    this.updateTableOfContents();
    this.showNavigation = true;
    this.updateActiveTOC();
  }

  protected hasAnyRequiredProperties(): boolean {
    return (
      this.hasInheritedProperties.required ||
      (this.getFoundElement()?.attributes?.filter((attribute) => attribute.mandatory).length ?? 0) > 0
    );
  }

  protected updateTableOfContents(): void {
    const tableOfContents: TOCEntry[] = [];
    const element = this.getFoundElement();
    if (!element) return;

    tableOfContents.push(
      { name: element.name, anchor: '#element', active: false },
      { name: 'See also', anchor: '#seeAlso', active: false },
      { name: 'Syntax', anchor: '#syntax', active: false },
    );
    if (element.attributes || this.hasInheritedProperties.required || this.hasInheritedProperties.optional) {
      const length = tableOfContents.push({ name: 'Attributes', anchor: '#attributes', active: false, children: [] });
      const index = length - 1;
      if (this.hasAnyRequiredProperties()) {
        tableOfContents[index].children!.push({ name: 'Required', anchor: '#attributes-required', active: false });
      }
      tableOfContents[index].children!.push({ name: 'Optional', anchor: '#attributes-optional', active: false });
    }
    if (element.parameters) tableOfContents.push({ name: 'Parameters', anchor: '#parameters', active: false });
    if (element.children) tableOfContents.push({ name: 'Nested Elements', anchor: '#nested-elements', active: false });
    if (element.forwards || this.hasInheritedProperties.forwards)
      tableOfContents.push({ name: 'Forwards', anchor: '#forwards', active: false });

    this.tableOfContents = tableOfContents;
  }

  private findElement(frankDocElements: FrankDoc['elements'] | null): FrankDoc['elements'][string] | null {
    if (!frankDocElements) return null;
    if (this.elementIndexOrClassName) return this.getElementByFullName(frankDocElements, this.elementIndexOrClassName);
    if (!this.elementFilterProperties) return null;
    return this.getElementByFilterLabelNames(frankDocElements, this.elementFilterProperties);
  }

  private getElementByFilterLabelNames(
    frankDocElements: FrankDoc['elements'],
    elementFilterProperties: ElementFilterProperties,
  ): FrankDoc['elements'][string] | null {
    const elements = Object.values(frankDocElements);
    const { filterName, labelName, elementName } = elementFilterProperties;

    if (!filterName) {
      return this.getElementByShortName(elements, elementName);
    }

    const filteredElement = elements.find((element) => {
      return element.name === elementName && element.labels?.[filterName]?.includes(labelName);
    });
    return filteredElement ?? null;
  }

  private getElementByShortName(frankDocElements: Element[], shortName: string): Element | null {
    return frankDocElements.find((element) => element.name === shortName) ?? null;
  }

  private getElementByFullName(frankDocElements: FrankDoc['elements'], fullName: string): Element | null {
    return (
      frankDocElements[fullName] ??
      Object.values(frankDocElements).find((element) => element.className === fullName) ??
      null
    );
  }
}
