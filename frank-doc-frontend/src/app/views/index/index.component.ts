import { Component, computed, inject, Signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AppService, Filter } from '../../app.service';
import { ChipComponent } from '@frankframework/angular-components';
import { IconCaretComponent } from '../../icons/icon-caret-down/icon-caret.component';
import { CollapseDirective } from '../../components/collapse.directive';
import { IconSmileComponent } from '../../icons/icon-smile/icon-smile.component';
import { environment } from '../../../environments/environment';
import { filterColours } from '../../app.constants';
import { NgClass } from '@angular/common';
import { NameWbrPipe } from '../../components/name-wbr.pipe';

@Component({
  selector: 'app-index',
  standalone: true,
  imports: [RouterLink, ChipComponent, IconCaretComponent, CollapseDirective, IconSmileComponent, NgClass, NameWbrPipe],
  templateUrl: './index.component.html',
  styleUrl: './index.component.scss',
})
export class IndexComponent {
  protected collapsedFilters: Record<string, boolean> = {};
  protected readonly showFeedback: boolean = environment.feedbackButtons;
  protected readonly filters: Signal<Filter[]> = computed(() => this.appService.filters());
  protected readonly deprecatedElements: Signal<string[]> = computed(() => {
    const elements = this.appService.frankDoc()?.elements;
    if (!elements) return [];
    return Object.values(elements)
      .filter((element) => element.deprecated)
      .map((element) => element.name);
  });

  private readonly appService: AppService = inject(AppService);
  protected readonly filterColours = filterColours;
  protected readonly getLabelColor = this.appService.getLabelColor;
  protected readonly scrollToElement = this.appService.scrollToElement;
}
