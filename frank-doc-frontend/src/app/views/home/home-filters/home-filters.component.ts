import {
  Component,
  computed,
  ElementRef,
  EventEmitter,
  inject,
  Input,
  OnDestroy,
  Output,
  signal,
  Signal,
  ViewChild,
  WritableSignal,
  ChangeDetectionStrategy,
} from '@angular/core';
import { ButtonComponent, CheckboxComponent } from '@frankframework/angular-components';
import { AppService, FilterGroups } from '../../../app.service';
import { KeyValuePipe, NgClass } from '@angular/common';
import { InitFilterToggleDirective } from './init-filter-toggle.directive';
import { FilterLabels } from '@frankframework/doc-library-core';
import { NgFFDoc } from '@frankframework/doc-library-ng';

type FilterEntry = {
  name: string;
  filter: FilterLabels;
};

@Component({
  selector: 'app-home-filters',
  imports: [ButtonComponent, NgClass, CheckboxComponent, InitFilterToggleDirective, KeyValuePipe],
  templateUrl: './home-filters.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './home-filters.component.scss',
})
export class HomeFiltersComponent {
  @ViewChild('menu') menuElementRef!: ElementRef<HTMLDivElement>;
  @Output() selectedFilters: EventEmitter<FilterGroups> = new EventEmitter<FilterGroups>();

  protected filters: Signal<FilterEntry[]> = computed(() =>
    Object.entries(this.ffDoc.filters()).map(([name, filter]) => ({ name, filter })),
  );
  protected open = false;
  protected selectedFilter: FilterEntry | null = null;
  protected selectedFilterLabels: WritableSignal<FilterGroups> = signal({});
  protected selectedFilterLocked = false;
  protected readonly appService: AppService = inject(AppService);

  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();

  @Input()
  set initialFilters(initialFilters: FilterGroups) {
    this.selectedFilterLabels.set(initialFilters);
  }

  protected toggleDropdown(): void {
    const menuElement = this.menuElementRef.nativeElement;
    if (this.open) {
      menuElement.classList.add('open');
      return;
    }
    menuElement.classList.remove('open');
    this.selectedFilter = null;
    this.selectedFilterLocked = false;
  }

  protected toggleFilterMenu(filter: FilterEntry, clicked = false): void {
    if (clicked && this.selectedFilter?.name === filter.name) {
      this.selectedFilterLocked = !this.selectedFilterLocked;
    }
    if (clicked || !this.selectedFilterLocked) {
      this.selectedFilter = filter;
    }
  }

  protected toggleMenuLabel(filterName: string, labelName: string): void {
    const selectedFilterLabels = this.selectedFilterLabels();
    selectedFilterLabels[filterName] ??= [];
    const filterLabelIndex = selectedFilterLabels[filterName].indexOf(labelName);
    if (filterLabelIndex === -1) {
      selectedFilterLabels[filterName].push(labelName);
    } else {
      selectedFilterLabels[filterName].splice(filterLabelIndex, 1);
    }
    this.updateSelectedFilters(selectedFilterLabels);
  }

  protected clearFilters(): void {
    this.updateSelectedFilters({});
  }

  protected isFilterGroupEmpty(filterGroup: string): boolean {
    const selectedFilters = this.selectedFilterLabels();
    return !Object.hasOwn(selectedFilters, filterGroup) || selectedFilters[filterGroup].length === 0;
  }

  protected areAllFilterGroupsEmpty(): boolean {
    const selectedFilters = this.selectedFilterLabels();
    if (Object.keys(selectedFilters).length === 0) {
      return true;
    }
    return Object.values(selectedFilters).every((group) => group.length === 0);
  }

  protected clearSelectedLabels(): void {
    if (!this.selectedFilter) return;
    const selectedFilterLabels = this.selectedFilterLabels();
    delete selectedFilterLabels[this.selectedFilter['name']];
    this.updateSelectedFilters(selectedFilterLabels);
  }

  protected removeSelectedFilterLabel(filterGroup: string, index: number): void {
    const selectedFilterLabels = this.selectedFilterLabels();
    selectedFilterLabels[filterGroup].splice(index, 1);
    this.updateSelectedFilters(selectedFilterLabels);
  }

  private updateSelectedFilters(selectedFilters: FilterGroups): void {
    this.selectedFilterLabels.set({ ...selectedFilters });
    this.selectedFilters.emit(this.selectedFilterLabels());
  }
}
