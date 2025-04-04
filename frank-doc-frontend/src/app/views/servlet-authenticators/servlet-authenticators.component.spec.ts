import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServletAuthenticatorsComponent } from './servlet-authenticators.component';
import { AppService } from '../../app.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('ServletAuthenticatorsComponent', () => {
  let component: ServletAuthenticatorsComponent;
  let fixture: ComponentFixture<ServletAuthenticatorsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServletAuthenticatorsComponent],
      providers: [AppService, provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(ServletAuthenticatorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
