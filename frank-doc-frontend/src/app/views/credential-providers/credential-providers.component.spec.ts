import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CredentialProvidersComponent } from './credential-providers.component';
import { AppService } from '../../app.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

describe('CredentialProvidersComponent', () => {
  let component: CredentialProvidersComponent;
  let fixture: ComponentFixture<CredentialProvidersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CredentialProvidersComponent],
      providers: [AppService, provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(CredentialProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
