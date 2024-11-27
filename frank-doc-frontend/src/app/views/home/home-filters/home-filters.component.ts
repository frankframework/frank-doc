import {
  Component,
  computed,
  ElementRef,
  EventEmitter,
  Output,
  signal,
  Signal,
  ViewChild,
  WritableSignal,
} from '@angular/core';
import { ButtonComponent, CheckboxComponent } from '@frankframework/angular-components';
import { AppService, Filter } from '../../../app.service';
import { SelectedFilters } from '../home.component';
import { NgClass } from '@angular/common';
import { InitFilterToggleDirective } from './init-filter-toggle.directive';

@Component({
  selector: 'app-home-filters',
  standalone: true,
  imports: [ButtonComponent, NgClass, CheckboxComponent, InitFilterToggleDirective],
  templateUrl: './home-filters.component.html',
  styleUrl: './home-filters.component.scss',
})
export class HomeFiltersComponent {
  @ViewChild('menu') menuElementRef!: ElementRef<HTMLDivElement>;
  @Output() selectedFilters: EventEmitter<SelectedFilters> = new EventEmitter<SelectedFilters>();

  protected filters: Signal<Filter[]> = computed(() => this.appService.filters());
  protected open: boolean = false;
  protected selectedFilter: Filter | null = null;
  protected selectedFilterLabels: WritableSignal<SelectedFilters> = signal({});

  constructor(private appService: AppService) {}

  toggleDropdown(): void {
    const menuElement = this.menuElementRef.nativeElement;
    if (this.open) {
      menuElement.classList.add('open');
      return;
    }
    menuElement.classList.remove('open');
    this.selectedFilter = null;
  }

  toggleFilterMenu(filter: Filter): void {
    if (this.selectedFilter === filter) {
      this.selectedFilter = null;
      return;
    }
    this.selectedFilter = filter;
  }

  onToggleLabel(filterName: string, labelName: string): void {
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

  clearFilters(): void {
    this.updateSelectedFilters({});
  }

  clearSelectedLabels(): void {
    if (!this.selectedFilter) return;
    const selectedFilterLabels = this.selectedFilterLabels();
    delete selectedFilterLabels[this.selectedFilter.name];
    this.updateSelectedFilters(selectedFilterLabels);
  }

  private updateSelectedFilters(selectedFilters: SelectedFilters): void {
    // using spread to have directive input's update, maybe input signals will improve this so no spreading is needed
    this.selectedFilterLabels.set({ ...selectedFilters });
    this.selectedFilters.emit(this.selectedFilterLabels());
  }
}
