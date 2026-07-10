import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsElementAttributesComponent } from './details-element-attributes.component';

describe('DetailsElementAttributesComponent', () => {
  let component: DetailsElementAttributesComponent;
  let fixture: ComponentFixture<DetailsElementAttributesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsElementAttributesComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailsElementAttributesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
