import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ElementAttributesComponent } from './element-attributes.component';

describe('ElementAttributesComponent', () => {
  let component: ElementAttributesComponent;
  let fixture: ComponentFixture<ElementAttributesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ElementAttributesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ElementAttributesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
