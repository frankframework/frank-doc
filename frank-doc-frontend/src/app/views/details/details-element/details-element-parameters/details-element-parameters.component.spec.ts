import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsElementParametersComponent } from './details-element-parameters.component';

describe('DetailsElementParametersComponent', () => {
  let component: DetailsElementParametersComponent;
  let fixture: ComponentFixture<DetailsElementParametersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsElementParametersComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailsElementParametersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
