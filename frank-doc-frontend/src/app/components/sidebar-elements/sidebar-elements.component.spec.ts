import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatchElementPipe } from 'src/app/pipes/match-element.pipe';

import { SidebarElementsComponent } from './sidebar-elements.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('SidebarElementsComponent', () => {
  let component: SidebarElementsComponent;
  let fixture: ComponentFixture<SidebarElementsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    declarations: [SidebarElementsComponent, MatchElementPipe],
    imports: [],
    providers: [provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
}).compileComponents();

    fixture = TestBed.createComponent(SidebarElementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
