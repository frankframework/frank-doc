import { Component, computed, inject, Signal } from '@angular/core';
import { RouterLink } from '@angular/router';
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
export class IndexComponent {
  protected collapsedFilters: Record<string, boolean> = {};
  protected readonly filters: Signal<{ name: string; filter: FilterLabels; filterCount: number }[]> = computed(() =>
    Object.entries(this.ffDoc.filters()).map(([name, filter]) => ({
      name,
      filter,
      filterCount: Object.keys(filter).length,
    })),
  );
  protected readonly deprecatedElements: Signal<string[]> = computed(() => {
    const elements = this.ffDoc.elements();
    if (!elements) return [];
    return Object.values(elements)
      .filter((element) => element.deprecated)
      .map((element) => element.name);
  });

  protected readonly appService: AppService = inject(AppService);

  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();
}
