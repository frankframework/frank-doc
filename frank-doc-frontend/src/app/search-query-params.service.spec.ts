import { TestBed } from '@angular/core/testing';

import { SearchQueryParamsService } from './search-query-params.service';

describe('SearchQueryParamsService', () => {
  let service: SearchQueryParamsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SearchQueryParamsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
