import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { AppService } from '../app.service';
import { MatchElementPipe } from './match-element.pipe';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('MatchElementPipe', () => {
  let appService: AppService;

  beforeEach(() => {
    TestBed.configureTestingModule({
    imports: [],
    providers: [AppService, provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
});
    appService = TestBed.inject(AppService);
  });

  it('create an instance', () => {
    const pipe = new MatchElementPipe(appService);
    expect(pipe).toBeTruthy();
  });
});
