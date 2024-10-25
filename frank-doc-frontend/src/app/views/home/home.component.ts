import { Component, computed, isDevMode, Signal } from '@angular/core';
import { SearchComponent } from '@frankframework/angular-components';
import { FormsModule } from '@angular/forms';
import { HomeComponentListComponent } from './home-component-list/home-component-list.component';
import { AppService } from '../../app.service';
import { FrankDoc, Element } from '../../frankdoc.types';
import Fuse, { FuseResult } from 'fuse.js';
import { HomeFiltersComponent } from './home-filters/home-filters.component';

export type SelectedFilters = Record<string, string[]>;

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

  private readonly elementsList: Signal<Element[]> = computed(() => Object.values(this.elements()));
  private readonly fuse: Signal<Fuse<Element>> = computed(
    () =>
      new Fuse(this.elementsList(), {
        keys: [
          {
            name: 'name',
            weight: 2,
          },
          {
            name: 'fullName',
            weight: 2,
          },
          {
            name: 'elementNames', // default weight is 1.0
          },
          {
            name: 'labels.FrankDocGroup',
          },
          {
            name: 'attributes',
            weight: 0.7,
          },
          {
            name: 'description',
            weight: 0.5,
          },
        ],
        includeScore: true,
        includeMatches: true,
        threshold: 0.2,
        minMatchCharLength: 3,
        ignoreLocation: true,
      }),
  );

  constructor(private appService: AppService) {}

  protected onSearchQueryChange(query: string): void {
    const searchPattern = query.trim();
    if (searchPattern !== '') {
      this.filteredElements = this.fuse().search(searchPattern);
      if (isDevMode()) console.log(this.filteredElements);
    }
  }
}
