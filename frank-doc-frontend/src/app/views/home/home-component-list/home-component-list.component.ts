import { Component, inject, Input } from '@angular/core';
import { ButtonComponent } from '@frankframework/angular-components';
import { FuseResult } from 'fuse.js';
import { Element } from '../../../frankdoc.types';
import { Router } from '@angular/router';
import { AppService } from '../../../app.service';
import { TruncatePipe } from '../../../components/truncate.pipe';
import { environment } from '../../../../environments/environment';
import { JavadocTransformDirective } from '../../../components/javadoc-transform.directive';
import { CardDirective } from './card.directive';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-home-component-list',
  imports: [ButtonComponent, TruncatePipe, JavadocTransformDirective, CardDirective, NgClass],
  templateUrl: './home-component-list.component.html',
  styleUrl: './home-component-list.component.scss',
})
export class HomeComponentListComponent {
  @Input() components: FuseResult<Element>[] = [];
  @Input() elements: Record<string, Element> = {};

  protected relatedComponents: FuseResult<Element>[] = [];
  protected showRelated: boolean = environment.relatedSearchResults;

  private router: Router = inject(Router);
  private appService: AppService = inject(AppService);
  protected getFirstFilter = this.appService.getFirstFilter;
  protected getFirstLabel = this.appService.getFirstLabel;

  protected navigateToElement(element: Element): void {
    const route = element.labels
      ? ['/', this.getFirstFilter(element.labels), this.getFirstLabel(element.labels), element.name]
      : ['/', element.className];
    this.router.navigate(route);
  }

  protected setActiveHover(card: HTMLElement, active: boolean): void {
    try {
      const cardElement = card as unknown as CardDirective;
      cardElement.activeHover = active;
    } catch (error) {
      console.warn(error);
    }
  }

  protected checkActiveHover(card: HTMLElement): boolean {
    try {
      const cardElement = card as unknown as CardDirective;
      return cardElement.activeHover;
    } catch (error) {
      console.warn(error);
      return false;
    }
  }
}
