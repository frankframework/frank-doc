import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ElementParametersComponent } from './element-parameters.component';

describe('ElementParametersComponent', () => {
  let component: ElementParametersComponent;
  let fixture: ComponentFixture<ElementParametersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ElementParametersComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ElementParametersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
