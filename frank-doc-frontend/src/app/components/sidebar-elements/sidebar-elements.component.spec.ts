import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatchElementPipe } from 'src/app/pipes/match-element.pipe';

import { SidebarElementsComponent } from './sidebar-elements.component';

describe('SidebarElementsComponent', () => {
  let component: SidebarElementsComponent;
  let fixture: ComponentFixture<SidebarElementsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [SidebarElementsComponent, MatchElementPipe],
    }).compileComponents();

    fixture = TestBed.createComponent(SidebarElementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
