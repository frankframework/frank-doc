import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsElementSyntaxComponent } from './details-element-syntax.component';

describe('DetailsElementSyntaxComponent', () => {
  let component: DetailsElementSyntaxComponent;
  let fixture: ComponentFixture<DetailsElementSyntaxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsElementSyntaxComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailsElementSyntaxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
