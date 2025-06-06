import { Component, inject, Input } from '@angular/core';
import { ButtonComponent } from '@frankframework/angular-components';
import { FuseResult } from 'fuse.js';
import { Router } from '@angular/router';
import { AppService, ElementEntry } from '../../../app.service';
import { TruncatePipe } from '../../../components/truncate.pipe';
import { environment } from '../../../../environments/environment';
import { CardDirective } from './card.directive';
import { NgClass } from '@angular/common';
import { Elements, JavadocTransformDirective } from '@frankframework/ff-doc';

@Component({
  selector: 'app-home-component-list',
  imports: [ButtonComponent, TruncatePipe, CardDirective, NgClass, JavadocTransformDirective],
  templateUrl: './home-component-list.component.html',
  styleUrl: './home-component-list.component.scss',
})
export class HomeComponentListComponent {
  @Input() components: FuseResult<ElementEntry>[] = [];
  @Input() elements: Elements = {};

  protected relatedComponents: FuseResult<ElementEntry>[] = [];
  protected showRelated: boolean = environment.relatedSearchResults;

  private readonly router: Router = inject(Router);
  private readonly appService: AppService = inject(AppService);
  protected getFirstLabelGroup = this.appService.getFirstLabelGroup;

  protected navigateToElement({ name, element }: ElementEntry): void {
    const [labelGroup, label] = this.getFirstLabelGroup(element.labels);
    const route = element.labels ? ['/', labelGroup, label, element.name] : ['/', name];
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
