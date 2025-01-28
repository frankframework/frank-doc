import { inject, Injectable } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';

export type SearchFilters = {
  query: Record<string, string[]>;
  search: string;
};

@Injectable({
  providedIn: 'root',
})
export class SearchQueryParamsService {
  private router: Router = inject(Router);
  private currentRoute: ActivatedRoute = inject(ActivatedRoute);
  private searchParam: string = '';
  private filterParams: Map<string, string> = new Map();

  constructor() {
    this.currentRoute.queryParamMap.subscribe((queryParams) => {
      this.searchParam = queryParams.get('search') ?? '';
      this.filterParams = this.getAllFilters(queryParams);
    });
  }

  getFromRoute(): SearchFilters {
    return {
      query: this.convertParamsToFilters(this.filterParams),
      search: this.searchParam,
    };
  }

  setInRoute(params: Partial<SearchFilters>): void {
    let newParams: Record<string, string> = {};
    if (params.search && params.search !== '') newParams['search'] = params.search.trim();
    if (params.query) newParams = { ...newParams, ...this.convertFiltersToParams(params.query) };

    this.router.navigate([], {
      relativeTo: this.currentRoute,
      preserveFragment: true,
      queryParams: newParams,
    });
  }

  private getAllFilters(params: ParamMap): Map<string, string> {
    const filterMap = new Map<string, string>();

    for (const key of params.keys) {
      if (key.startsWith('filter[') && key.endsWith(']')) {
        const filterKey = key.slice(7, -1);
        const filterValue = params.get(key);
        if (filterValue) filterMap.set(filterKey, filterValue);
      }
    }
    return filterMap;
  }

  private convertFiltersToParams(filters: Record<string, string[]>): Record<string, string> {
    const filterParams: Record<string, string> = {};
    for (const key in filters) {
      if (filters[key].length > 0) filterParams[`filter[${key}]`] = filters[key].join(',');
    }
    return filterParams;
  }

  private convertParamsToFilters(params: Map<string, string>): Record<string, string[]> {
    const filters: Record<string, string[]> = {};
    for (const [key, value] of params.entries()) {
      filters[key] = value.split(',');
    }
    return filters;
  }
}
