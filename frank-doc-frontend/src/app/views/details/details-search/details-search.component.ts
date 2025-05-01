import { Component, inject, Input, isDevMode, OnChanges, SimpleChanges } from '@angular/core';
import { SearchComponent } from '@frankframework/angular-components';
import { FormsModule } from '@angular/forms';
import Fuse, { FuseResult } from 'fuse.js';
import { Element, ElementLabels, FrankDoc } from '../../../frankdoc.types';
import { AppService } from '../../../app.service';
import { NgClass } from '@angular/common';
import { RouterLink } from '@angular/router';
import { environment } from '../../../../environments/environment';
import { fuseOptions, splitOnPascalCaseRegex } from '../../../app.constants';
import { NameWbrPipe } from '../../../components/name-wbr.pipe';

@Component({
  selector: 'app-details-search',
  imports: [SearchComponent, FormsModule, NgClass, RouterLink, NameWbrPipe],
  templateUrl: './details-search.component.html',
  styleUrl: './details-search.component.scss',
})
export class DetailsSearchComponent implements OnChanges {
  @Input({ required: true }) element!: Element | null;
  @Input({ required: true }) frankDocElements!: FrankDoc['elements'] | null;

  protected searchQuery = '';
  protected relatedSearchQuery = '';
  protected filteredElements: FuseResult<Element>[] = [];
  protected relatedElements: FuseResult<Element>[] = [];
  protected showRelated: boolean = environment.relatedSearchResults;

  private appService: AppService = inject(AppService);
  protected getFirstFilter = this.appService.getFirstFilter;
  protected getFirstLabel = this.appService.getFirstLabel;

  private elementsList: Element[] = [];
  private exclusiveElementsList: Element[] = [];
  private fuse: Fuse<Element> = new Fuse(this.elementsList, fuseOptions);
  private fuseRelated: Fuse<Element> = new Fuse(this.elementsList, { ...fuseOptions, shouldSort: false });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['frankDocElements']) {
      this.elementsList = Object.values(this.frankDocElements ?? {});
      this.fuse.setCollection(this.elementsList);
    }
    if (changes['element']) {
      if (this.element) {
        const firstElementNamePart = this.element.name.split(splitOnPascalCaseRegex)[0];
        this.relatedSearchQuery = firstElementNamePart ?? this.element.name;
      } else {
        this.relatedSearchQuery = '';
      }
      this.fuse.setCollection(this.elementsList);
      this.filterExclusiveElements();
      this.searchQuery = this.appService.previousSearchQuery;
      this.searchFilteredElements();
    }
  }

  protected search(): void {
    this.searchFilteredElements();
  }

  protected updateSelectedFiltersInSearch(selectedFilters: ElementLabels): void {
    this.fuse.setCollection(this.appService.filterElementsBySelectedFilters(this.elementsList, selectedFilters));
    if (isDevMode()) console.log('Selected Filters for search', selectedFilters);
    this.searchFilteredElements();
  }

  protected updateSelectedFiltersInRelated(selectedFilters: ElementLabels): void {
    this.fuseRelated.setCollection(
      this.appService.filterElementsBySelectedFilters(this.exclusiveElementsList, selectedFilters),
    );
    if (isDevMode()) console.log('Selected Filters for related', selectedFilters);
    this.searchRelatedElements();
  }

  private searchFilteredElements(): void {
    const searchPattern = this.searchQuery.trim();
    if (searchPattern === '') return;
    this.filteredElements = this.fuse.search(searchPattern, { limit: 20 });
    this.appService.previousSearchQuery = searchPattern;
    if (isDevMode()) console.log('Search', this.filteredElements);
  }

  private searchRelatedElements(): void {
    const searchPattern = this.relatedSearchQuery.trim();
    if (searchPattern === '') return;
    this.relatedElements = this.fuseRelated.search(searchPattern, { limit: 10 });
    if (isDevMode()) console.log('Related search', this.relatedElements);
  }

  private filterExclusiveElements(): void {
    this.exclusiveElementsList = this.elementsList.filter((element) => element.name !== this.element?.name);
    this.updateSelectedFiltersInRelated(this.element?.labels ?? {});
  }
}
