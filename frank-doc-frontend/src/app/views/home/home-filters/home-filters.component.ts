import {
  Component,
  computed,
  ElementRef,
  EventEmitter,
  inject,
  OnDestroy,
  Output,
  signal,
  Signal,
  ViewChild,
  WritableSignal,
} from '@angular/core';
import { ButtonComponent, CheckboxComponent } from '@frankframework/angular-components';
import { AppService, Filter } from '../../../app.service';
import { KeyValuePipe, NgClass } from '@angular/common';
import { InitFilterToggleDirective } from './init-filter-toggle.directive';
import { ElementLabels } from '../../../frankdoc.types';

@Component({
  selector: 'app-home-filters',
  standalone: true,
  imports: [ButtonComponent, NgClass, CheckboxComponent, InitFilterToggleDirective, KeyValuePipe],
  templateUrl: './home-filters.component.html',
  styleUrl: './home-filters.component.scss',
})
export class HomeFiltersComponent implements OnDestroy {
  @ViewChild('menu') menuElementRef!: ElementRef<HTMLDivElement>;
  @Output() selectedFilters: EventEmitter<ElementLabels> = new EventEmitter<ElementLabels>();

  private appService: AppService = inject(AppService);
  protected filters: Signal<Filter[]> = computed(() => this.appService.filters());
  protected open: boolean = false;
  protected selectedFilter: Filter | null = null;
  protected selectedFilterLabels: WritableSignal<ElementLabels> = signal({});

  private onOutsideClick: (event: MouseEvent) => void = (event: MouseEvent): void => this.outsideClickHandler(event);

  ngOnDestroy(): void {
    document.removeEventListener('click', this.onOutsideClick);
  }

  protected toggleDropdown(): void {
    const menuElement = this.menuElementRef.nativeElement;
    if (this.open) {
      menuElement.classList.add('open');
      setTimeout(() => document.addEventListener('click', this.onOutsideClick));
      return;
    }
    menuElement.classList.remove('open');
    document.removeEventListener('click', this.onOutsideClick);
    this.selectedFilter = null;
  }

  protected toggleFilterMenu(filter: Filter): void {
    if (this.selectedFilter === filter) {
      this.selectedFilter = null;
      return;
    }
    this.selectedFilter = filter;
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

  protected clearSelectedLabels(): void {
    if (!this.selectedFilter) return;
    const selectedFilterLabels = this.selectedFilterLabels();
    delete selectedFilterLabels[this.selectedFilter.name];
    this.updateSelectedFilters(selectedFilterLabels);
  }

  protected removeSelectedFilterLabel(filterGroup: string, index: number): void {
    const selectedFilterLabels = this.selectedFilterLabels();
    selectedFilterLabels[filterGroup].splice(index, 1);
    this.updateSelectedFilters(selectedFilterLabels);
  }

  private updateSelectedFilters(selectedFilters: ElementLabels): void {
    // using spread to have directive input's update, maybe input signals will improve this so no spreading is needed
    this.selectedFilterLabels.set({ ...selectedFilters });
    this.selectedFilters.emit(this.selectedFilterLabels());
  }

  private outsideClickHandler(event: MouseEvent): void {
    const clickedElement = event.target as HTMLElement;
    if (!clickedElement.closest('.menu.open')) {
      this.open = false;
      this.toggleDropdown();
    }
  }
}
