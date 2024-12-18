import { Component, computed, inject, isDevMode, Signal } from '@angular/core';
import { SearchComponent } from '@frankframework/angular-components';
import { FormsModule } from '@angular/forms';
import { HomeComponentListComponent } from './home-component-list/home-component-list.component';
import { AppService } from '../../app.service';
import { FrankDoc, Element, ElementLabels } from '../../frankdoc.types';
import Fuse, { FuseResult } from 'fuse.js';
import { HomeFiltersComponent } from './home-filters/home-filters.component';
import { fuseOptions } from '../../app.constants';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [SearchComponent, FormsModule, HomeComponentListComponent, HomeFiltersComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  protected searchQuery = '';
  protected readonly elements: Signal<FrankDoc['elements']> = computed(
    () => this.appService.frankDoc()?.elements ?? {},
  );
  protected filteredElements: FuseResult<Element>[] = [];

  private readonly appService: AppService = inject(AppService);
  private readonly elementsList: Signal<Element[]> = computed(() => Object.values(this.elements()));
  private readonly fuse: Signal<Fuse<Element>> = computed(() => new Fuse(this.elementsList(), fuseOptions));

  protected search(query: string): void {
    const searchPattern = query.trim();
    if (searchPattern !== '') {
      this.filteredElements = this.fuse().search(searchPattern);
      if (isDevMode()) console.log('Search', this.filteredElements);
    }
  }

  protected updateSelectedFilters(selectedFilters: ElementLabels): void {
    this.fuse().setCollection(this.appService.filterElementsBySelectedFilters(this.elementsList(), selectedFilters));
    this.search(this.searchQuery);
    if (isDevMode()) console.log('Selected Filters', selectedFilters);
  }
}
