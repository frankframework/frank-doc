import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PropertiesComponent } from './properties.component';
import { AppService } from '../../app.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('PropertiesComponent', () => {
  let component: PropertiesComponent;
  let fixture: ComponentFixture<PropertiesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PropertiesComponent],
      providers: [AppService, provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(PropertiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
