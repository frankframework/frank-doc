import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsElementComponent } from './details-element.component';
import { AppService } from '../../../app.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

describe('DetailsElementComponent', () => {
  let component: DetailsElementComponent;
  let fixture: ComponentFixture<DetailsElementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsElementComponent],
      providers: [AppService, provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailsElementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
