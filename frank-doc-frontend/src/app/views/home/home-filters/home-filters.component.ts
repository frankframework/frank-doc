import { Component, computed, ElementRef, EventEmitter, Output, Signal, ViewChild } from '@angular/core';
import { ButtonComponent } from '@frankframework/angular-components';
import { AppService, Filter } from '../../../app.service';
import { SelectedFilters } from '../home.component';

@Component({
  selector: 'app-home-filters',
  standalone: true,
  imports: [ButtonComponent],
  templateUrl: './home-filters.component.html',
  styleUrl: './home-filters.component.scss',
})
export class HomeFiltersComponent {
  @ViewChild('menu') menuElementRef!: ElementRef<HTMLDivElement>;
  @Output() selectedFilters: EventEmitter<SelectedFilters> = new EventEmitter<SelectedFilters>();

  protected filters: Signal<Filter[]> = computed(() => this.appService.filters());
  protected open: boolean = false;
  protected selectedFilter: Filter | null = null;
  protected selectedFilterLabels: SelectedFilters = {};

  constructor(private appService: AppService) {}

  toggleDropdown(): void {
    const menuElement = this.menuElementRef.nativeElement;
    if (this.open) {
      menuElement.classList.add('open');
      return;
    }
    menuElement.classList.remove('open');
  }

  toggleFilterMenu(filter: Filter): void {
    if (this.selectedFilter === filter) {
      this.selectedFilter = null;
      return;
    }
    this.selectedFilter = filter;
  }

  onToggleLabel(filterName: string, labelName: string): void {
    const filterLabelIndex = this.selectedFilterLabels[filterName]?.indexOf(labelName);
    if (filterLabelIndex !== -1) {
      this.selectedFilterLabels[filterName].splice(filterLabelIndex, 1);
      return;
    }
    this.selectedFilterLabels[filterName].push(labelName);
    this.selectedFilters.emit({ ...this.selectedFilterLabels });
  }

  clearFilters(): void {
    // TODO clear all selected labels
  }

  clearSelectedLabels(): void {
    // TODO for selected filter remove all labels
  }
}
