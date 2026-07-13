import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsElementNestedComponent } from './details-element-nested.component';

describe('DetailsElementNestedComponent', () => {
  let component: DetailsElementNestedComponent;
  let fixture: ComponentFixture<DetailsElementNestedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsElementNestedComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailsElementNestedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
