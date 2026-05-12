import { Component, computed, inject, OnInit, Signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AppService } from '../../app.service';
import { ChipComponent } from '@frankframework/angular-components';
import { IconCaretComponent } from '../../icons/icon-caret-down/icon-caret.component';
import { CollapseDirective } from '../../components/collapse.directive';
import { KeyValuePipe, NgClass } from '@angular/common';
import { NameWbrPipe } from '../../components/name-wbr.pipe';
import { FilterLabels } from '@frankframework/doc-library-core';
import { NgFFDoc } from '@frankframework/doc-library-ng';

@Component({
  selector: 'app-index',
  imports: [RouterLink, ChipComponent, IconCaretComponent, CollapseDirective, NgClass, NameWbrPipe, KeyValuePipe],
  templateUrl: './index.component.html',
  styleUrl: './index.component.scss',
})
export class IndexComponent implements OnInit {
  protected collapsedFilters: Record<string, boolean> = {};
  protected readonly filters: Signal<{ name: string; filter: FilterLabels; filterCount: number }[]> = computed(() => {
    const filters = Object.entries(this.ffDoc.filters()).map(([name, filter]) => ({
      name,
      filter,
      filterCount: Object.keys(filter).length,
    }));
    if (this.scrollElementId) this.appService.scrollToElement(this.scrollElementId);
    return filters;
  });
  protected readonly deprecatedElements: Signal<string[]> = computed(() => {
    const elements = this.ffDoc.elements();
    if (!elements) return [];
    return Object.values(elements)
      .filter((element) => element.deprecated)
      .map((element) => element.name);
  });
  protected readonly appService: AppService = inject(AppService);

  private scrollElementId: string | null = null;
  private readonly router: Router = inject(Router);
  private readonly route: ActivatedRoute = inject(ActivatedRoute);
  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((parameters) => {
      const filterName = parameters.get('filterName');
      const labelName = parameters.get('labelName');
      if (filterName) {
        this.scrollElementId = labelName ? `#${filterName}-${labelName}` : `#${filterName}`;
        this.appService.scrollToElement(this.scrollElementId);
      }
    });
  }

  protected scrollToElement(filterName: string, labelName: string | null): void {
    this.router.navigate([], {
      relativeTo: this.route,
      scroll: 'manual',
      queryParams: {
        filterName,
        labelName,
      },
    });
  }
}
