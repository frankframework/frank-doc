import { Component, computed, inject, OnInit, Signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Element, FrankDoc } from '../../frankdoc.types';
import { AppService } from '../../app.service';
import { DetailsElementComponent } from './details-element/details-element.component';
import { DetailsSearchComponent } from './details-search/details-search.component';
import { IconLikeComponent } from '../../icons/icon-like/icon-like.component';
import { IconDislikeComponent } from '../../icons/icon-dislike/icon-dislike.component';
import { environment } from '../../../environments/environment';
import { IconSmileComponent } from '../../icons/icon-smile/icon-smile.component';

type ElementFilterProperties = {
  filterName?: string;
  labelName: string;
  elementName: string;
};

export type HasInheritedProperties = {
  required: boolean;
  optional: boolean;
};

@Component({
  selector: 'app-details',
  imports: [
    DetailsElementComponent,
    DetailsSearchComponent,
    IconLikeComponent,
    IconDislikeComponent,
    IconSmileComponent,
  ],
  templateUrl: './details.component.html',
  styleUrl: './details.component.scss',
})
export class DetailsComponent implements OnInit {
  protected readonly frankDocElements: Signal<FrankDoc['elements'] | null> = computed(
    () => this.appService.frankDoc()?.elements ?? null,
  );
  protected elementByRoute: Element | null = null;
  protected readonly elementBySignal: Signal<Element | null> = computed(() => {
    const elements = this.frankDocElements();
    if (elements) return this.findElement(elements);
    return null;
  });
  protected readonly showFeedback: boolean = environment.feedbackButtons;
  protected hasInheritedProperties: HasInheritedProperties = {
    required: false,
    optional: false,
  };

  private appService: AppService = inject(AppService);
  protected readonly scrollToElement = this.appService.scrollToElement;

  private route: ActivatedRoute = inject(ActivatedRoute);
  private elementFilterProperties: ElementFilterProperties | null = null;
  private fullElementName: string | null = null;

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const fullName: string | undefined = params['fullname'];
      const filterName: string | undefined = params['filter'];
      const labelName: string | undefined = params['label'];
      const elementName: string | undefined = params['element'];

      if (fullName) {
        this.fullElementName = fullName;
      } else if (labelName && elementName) {
        this.elementFilterProperties = { filterName, labelName, elementName };
      }

      this.elementByRoute = this.findElement(this.frankDocElements());
    });
  }

  protected getFoundElement(): Element | null {
    return this.elementByRoute ?? this.elementBySignal();
  }

  protected hasAnyInheritedProperties(): boolean {
    return this.hasInheritedProperties.required || this.hasInheritedProperties.optional;
  }

  protected hasAnyRequiredProperities(): boolean {
    return (
      this.hasInheritedProperties.required ||
      (this.getFoundElement()?.attributes?.filter((attribute) => attribute.mandatory).length ?? 0) > 0
    );
  }

  private findElement(frankDocElements: FrankDoc['elements'] | null): FrankDoc['elements'][string] | null {
    if (!frankDocElements) return null;
    if (this.fullElementName) return this.getElementByFullName(frankDocElements, this.fullElementName);
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
    return frankDocElements[fullName] ?? null;
  }
}
