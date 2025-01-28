import { TestBed } from '@angular/core/testing';

import { SearchQueryParamsService } from './search-query-params.service';
import { provideRouter } from '@angular/router';

describe('SearchQueryParamsService', () => {
  let service: SearchQueryParamsService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [SearchQueryParamsService, provideRouter([])] });
    service = TestBed.inject(SearchQueryParamsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
