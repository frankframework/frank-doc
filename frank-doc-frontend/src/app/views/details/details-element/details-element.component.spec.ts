import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsElementComponent } from './details-element.component';

describe('DetailsElementComponent', () => {
  let component: DetailsElementComponent;
  let fixture: ComponentFixture<DetailsElementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsElementComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailsElementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
