import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeComponentListComponent } from './home-component-list.component';
import { AppService } from '../../../app.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('HomeComponentListComponent', () => {
  let component: HomeComponentListComponent;
  let fixture: ComponentFixture<HomeComponentListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponentListComponent],
      providers: [AppService, provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponentListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
