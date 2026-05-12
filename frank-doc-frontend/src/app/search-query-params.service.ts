import { inject, Injectable } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { FilterGroups } from './app.service';

type SearchFilters = {
  query: FilterGroups;
  search: string;
};

@Injectable({
  providedIn: 'root',
})
export class SearchQueryParamsService {
  private router: Router = inject(Router);
  private currentRoute: ActivatedRoute = inject(ActivatedRoute);
  private searchParam = '';
  private filterParams = new Map<string, string>();

  constructor() {
    this.currentRoute.queryParamMap.subscribe((queryParameters) => {
      this.searchParam = queryParameters.get('search') ?? '';
      this.filterParams = this.getAllFilters(queryParameters);
    });
  }

  getFromRoute(): SearchFilters {
    return {
      query: this.convertParamsToFilters(this.filterParams),
      search: this.searchParam,
    };
  }

  setInRoute(parameters: Partial<SearchFilters>): void {
    let newParameters: Record<string, string> = {};
    if (parameters.search && parameters.search !== '') newParameters['search'] = parameters.search.trim();
    if (parameters.query) newParameters = { ...newParameters, ...this.convertFiltersToParams(parameters.query) };

    this.router.navigate([], {
      relativeTo: this.currentRoute,
      preserveFragment: true,
      queryParams: newParameters,
      replaceUrl: true,
    });
  }

  private getAllFilters(parameters: ParamMap): Map<string, string> {
    const filterMap = new Map<string, string>();

    for (const key of parameters.keys) {
      if (key.startsWith('filter[') && key.endsWith(']')) {
        const filterKey = key.slice(7, -1);
        const filterValue = parameters.get(key);
        if (filterValue) filterMap.set(filterKey, filterValue);
      }
    }
    return filterMap;
  }

  private convertFiltersToParams(filters: FilterGroups): Record<string, string> {
    const filterParameters: Record<string, string> = {};
    for (const key in filters) {
      if (filters[key].length > 0) filterParameters[`filter[${key}]`] = filters[key].join(',');
    }
    return filterParameters;
  }

  private convertParamsToFilters(parameters: Map<string, string>): FilterGroups {
    const filters: FilterGroups = {};
    for (const [key, value] of parameters.entries()) {
      for (const label of value.split(',')) {
        if (!filters[key]) filters[key] = [];
        filters[key].push(label);
      }
    }
    return filters;
  }
}
