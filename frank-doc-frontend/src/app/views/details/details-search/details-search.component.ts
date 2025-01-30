import { Component, inject, Input, isDevMode, OnChanges, SimpleChanges } from '@angular/core';
import { SearchComponent } from '@frankframework/angular-components';
import { FormsModule } from '@angular/forms';
import Fuse, { FuseResult } from 'fuse.js';
import { Element, ElementLabels, FrankDoc } from '../../../frankdoc.types';
import { AppService } from '../../../app.service';
import { NgClass } from '@angular/common';
import { RouterLink } from '@angular/router';
import { environment } from '../../../../environments/environment';
import { fuseOptions } from '../../../app.constants';
import { NameWbrPipe } from '../../../components/name-wbr.pipe';

@Component({
  selector: 'app-details-search',
  standalone: true,
  imports: [SearchComponent, FormsModule, NgClass, RouterLink, NameWbrPipe],
  templateUrl: './details-search.component.html',
  styleUrl: './details-search.component.scss',
})
export class DetailsSearchComponent implements OnChanges {
  @Input({ required: true }) element!: Element | null;
  @Input({ required: true }) frankDocElements!: FrankDoc['elements'] | null;

  protected searchQuery = '';
  protected filteredElements: FuseResult<Element>[] = [];
  protected showRelated: boolean = environment.relatedSearchResults;

  private appService: AppService = inject(AppService);
  protected getFirstFilter = this.appService.getFirstFilter;
  protected getFirstLabel = this.appService.getFirstLabel;

  private elementsList: Element[] = [];
  private fuse: Fuse<Element> = new Fuse(this.elementsList, fuseOptions);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['frankDocElements']) {
      this.elementsList = Object.values(this.frankDocElements ?? {}).filter((element) => !element.deprecated);
      this.fuse.setCollection(this.elementsList);
    }
    if (changes['element']) {
      if (this.searchQuery === '') {
        const firstElementNamePart = this.element?.name.split(/(?=[A-Z])/)[0];
        this.searchQuery = firstElementNamePart ?? '';
      }
      this.updateSelectedFilters(this.element?.labels ?? {});
    }
  }

  protected resetAndSearch(query: string): void {
    this.fuse.setCollection(this.elementsList);
    this.search(query);
  }

  protected search(query: string): void {
    const searchPattern = query.trim();
    if (searchPattern !== '') {
      this.filteredElements = this.fuse.search(searchPattern, { limit: 20 });
      if (isDevMode()) console.log('Search', this.filteredElements);
    }
  }

  protected updateSelectedFilters(selectedFilters: ElementLabels): void {
    this.fuse.setCollection(this.appService.filterElementsBySelectedFilters(this.elementsList, selectedFilters));
    this.search(this.searchQuery);
    if (isDevMode()) console.log('Selected Filters', selectedFilters);
  }
}
