import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReactImplementationComponent } from './react-implementation.component';

describe('ReactImplementationComponent', () => {
  let component: ReactImplementationComponent;
  let fixture: ComponentFixture<ReactImplementationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactImplementationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ReactImplementationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
