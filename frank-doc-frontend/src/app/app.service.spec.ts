import { TestBed } from '@angular/core/testing';

import { AppService } from './app.service';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

describe('AppService', () => {
  let service: AppService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(AppService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
