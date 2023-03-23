import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { AppService } from '../app.service';
import { MatchElementPipe } from './match-element.pipe';

describe('MatchElementPipe', () => {
  let appService: AppService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AppService]
    });
    appService = TestBed.inject(AppService);
  });

  it('create an instance', () => {
    const pipe = new MatchElementPipe(appService);
    expect(pipe).toBeTruthy();
  });
});
