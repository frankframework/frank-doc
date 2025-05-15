import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AngularImplementationComponent } from './angular-implementation.component';

describe('AngularImplementationComponent', () => {
  let component: AngularImplementationComponent;
  let fixture: ComponentFixture<AngularImplementationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AngularImplementationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AngularImplementationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
