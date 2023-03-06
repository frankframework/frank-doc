import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttributeDescriptionComponent } from './attribute-description.component';

describe('AttributeDescriptionComponent', () => {
  let component: AttributeDescriptionComponent;
  let fixture: ComponentFixture<AttributeDescriptionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AttributeDescriptionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AttributeDescriptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
