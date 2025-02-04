import { Component, computed, inject, isDevMode, OnInit, Signal } from '@angular/core';
import { SearchComponent } from '@frankframework/angular-components';
import { FormsModule } from '@angular/forms';
import { HomeComponentListComponent } from './home-component-list/home-component-list.component';
import { AppService } from '../../app.service';
import { FrankDoc, Element, ElementLabels } from '../../frankdoc.types';
import Fuse, { FuseResult } from 'fuse.js';
import { HomeFiltersComponent } from './home-filters/home-filters.component';
import { fuseOptions } from '../../app.constants';
import { SearchQueryParamsService } from '../../search-query-params.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [SearchComponent, FormsModule, HomeComponentListComponent, HomeFiltersComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  protected searchQuery = '';
  protected readonly elements: Signal<FrankDoc['elements']> = computed(
    () => this.appService.frankDoc()?.elements ?? {},
  );
  protected filteredElements: FuseResult<Element>[] = [];
  protected initialFilters: ElementLabels = {};
  protected loading: boolean = true;

  private readonly appService: AppService = inject(AppService);
  private readonly searchParamsService: SearchQueryParamsService = inject(SearchQueryParamsService);
  private readonly elementsList: Signal<Element[]> = computed(() =>
    Object.values(this.elements()).filter((element) => !element.deprecated),
  );
  private readonly fuse: Signal<Fuse<Element>> = computed(() => new Fuse(this.elementsList(), fuseOptions));
  private selectedFilters: ElementLabels = {};

  ngOnInit(): void {
    this.appService.applicationLoaded$.subscribe(() => {
      this.initializeSearchFilters();
      this.loading = false;
    });
    this.initializeSearchFilters();
    this.loading = !this.appService.hasLoaded;
    this.searchQuery = this.appService.previousSearchQuery;
  }

  protected search(query: string): void {
    const searchPattern = query.trim();
    this.searchParamsService.setInRoute({ search: searchPattern, query: this.selectedFilters });
    if (searchPattern !== '') {
      this.filteredElements = this.fuse().search(searchPattern);
      this.appService.previousSearchQuery = searchPattern;
      if (isDevMode()) console.log('Search', this.filteredElements);
    }
  }

  protected updateSelectedFilters(selectedFilters: ElementLabels): void {
    this.selectedFilters = selectedFilters;
    this.fuse().setCollection(this.appService.filterElementsBySelectedFilters(this.elementsList(), selectedFilters));
    this.search(this.searchQuery);
    if (isDevMode()) console.log('Selected Filters', selectedFilters);
  }

  private initializeSearchFilters(): void {
    const params = this.searchParamsService.getFromRoute();
    this.searchQuery = params.search;
    this.initialFilters = params.query;
    this.updateSelectedFilters(params.query);
  }
}
