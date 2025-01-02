import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsSearchComponent } from './details-search.component';
import { AppService } from '../../../app.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('DetailsSearchComponent', () => {
  let component: DetailsSearchComponent;
  let fixture: ComponentFixture<DetailsSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsSearchComponent],
      providers: [AppService, provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailsSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
