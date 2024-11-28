import { Component, computed, isDevMode, Signal } from '@angular/core';
import { SearchComponent } from '@frankframework/angular-components';
import { FormsModule } from '@angular/forms';
import { HomeComponentListComponent } from './home-component-list/home-component-list.component';
import { AppService } from '../../app.service';
import { FrankDoc, Element } from '../../frankdoc.types';
import Fuse, { FuseResult, IFuseOptions } from 'fuse.js';
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
  private readonly fuse: Signal<Fuse<Element>> = computed(() => new Fuse(this.elementsList(), this.fuseOptions));

  private fuseOptions: IFuseOptions<Element> = {
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
  };

  constructor(private appService: AppService) {}

  protected search(query: string): void {
    const searchPattern = query.trim();
    if (searchPattern !== '') {
      this.filteredElements = this.fuse().search(searchPattern);
      if (isDevMode()) console.log(this.filteredElements);
    }
  }

  protected updateSelectedFilters(selectedFilters: SelectedFilters): void {
    this.fuse().setCollection(this.filterElementsBySelectedFilters(this.elementsList(), selectedFilters));
    this.search(this.searchQuery);
    if (isDevMode()) console.log(selectedFilters);
  }

  private getSelectedFiltersLength(selectedFilters: SelectedFilters): number {
    if (Object.values(selectedFilters).length === 0) return 0;
    return Object.values(selectedFilters).reduce((acc, val) => acc + val.length, 0);
  }

  private filterElementsBySelectedFilters(elements: Element[], selectedFilters: SelectedFilters): Element[] {
    if (this.getSelectedFiltersLength(selectedFilters) === 0) return elements;
    return elements.filter((element) => {
      if (!element.labels) return false;
      for (const labelKey of Object.keys(element.labels)) {
        if (!selectedFilters[labelKey]) continue;
        for (const labelItem of element.labels[labelKey]) {
          if (selectedFilters[labelKey].includes(labelItem)) {
            return true;
          }
        }
      }
      return false;
    });
  }
}
