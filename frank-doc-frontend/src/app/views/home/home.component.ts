import { Component, computed, inject, isDevMode, OnInit, Signal } from '@angular/core';
import { SearchComponent } from '@frankframework/angular-components';
import { FormsModule } from '@angular/forms';
import { HomeComponentListComponent } from './home-component-list/home-component-list.component';
import { AppService, FilterGroups } from '../../app.service';
import Fuse, { FuseResult } from 'fuse.js';
import { HomeFiltersComponent } from './home-filters/home-filters.component';
import { fuseOptions } from '../../app.constants';
import { SearchQueryParamsService } from '../../search-query-params.service';
import { ElementDetails, Elements, NgFFDoc } from '@frankframework/ff-doc';

@Component({
  selector: 'app-home',
  imports: [SearchComponent, FormsModule, HomeComponentListComponent, HomeFiltersComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  protected searchQuery = '';
  protected readonly elements: Signal<Elements> = computed(() => this.ffDoc.elements() ?? {});
  protected filteredElements: FuseResult<ElementDetails>[] = [];
  protected initialFilters: FilterGroups = {};
  protected loading: boolean = true;

  private readonly appService: AppService = inject(AppService);
  private readonly searchParamsService: SearchQueryParamsService = inject(SearchQueryParamsService);
  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();
  private readonly elementsList: Signal<ElementDetails[]> = computed(() => Object.values(this.elements()));
  private readonly fuse: Signal<Fuse<ElementDetails>> = computed(() => new Fuse(this.elementsList(), fuseOptions));
  private selectedFilters: FilterGroups = {};

  ngOnInit(): void {
    this.appService.applicationLoaded$.subscribe(() => {
      this.initializeSearchFilters();
      this.loading = false;
    });
    this.initializeSearchFilters();
    this.loading = !this.appService.hasLoaded;

    if (this.appService.previousSearchQuery !== '') {
      this.searchQuery = this.appService.previousSearchQuery;
      this.search(this.searchQuery);
    }
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

  protected updateSelectedFilters(selectedFilters: FilterGroups): void {
    this.selectedFilters = selectedFilters;
    this.fuse().setCollection(
      this.appService.filterElementsBySelectedFilters(Object.values(this.elements()), selectedFilters),
    );
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
