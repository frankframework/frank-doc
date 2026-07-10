import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsElementForwardsComponent } from './details-element-forwards.component';

describe('DetailsElementForwardsComponent', () => {
  let component: DetailsElementForwardsComponent;
  let fixture: ComponentFixture<DetailsElementForwardsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsElementForwardsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailsElementForwardsComponent);
    fixture.componentRef.setInput('elements', {});
    fixture.componentRef.setInput('forwards', {});
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
