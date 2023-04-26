import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ElementForwardsComponent } from './element-forwards.component';

describe('ElementForwardsComponent', () => {
  let component: ElementForwardsComponent;
  let fixture: ComponentFixture<ElementForwardsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ElementForwardsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ElementForwardsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
