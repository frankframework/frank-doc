import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeComponentListComponent } from './home-component-list.component';

describe('HomeComponentListComponent', () => {
  let component: HomeComponentListComponent;
  let fixture: ComponentFixture<HomeComponentListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponentListComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponentListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
