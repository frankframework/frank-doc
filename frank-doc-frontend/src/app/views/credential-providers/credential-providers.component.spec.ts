import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CredentialProvidersComponent } from './credential-providers.component';

describe('CredentialProvidersComponent', () => {
  let component: CredentialProvidersComponent;
  let fixture: ComponentFixture<CredentialProvidersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CredentialProvidersComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CredentialProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
