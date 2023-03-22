import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';

import { SidebarComponent } from './sidebar.component';

@Component({ selector: 'sidebar-elements', template: '' })
class SidebarElementsStubComponent {
  @Input() elements: any;
  @Input() showDeprecatedElements: any;
  @Input() showInheritance: any;
  @Input() search: any;
  @Input() group?: any;
  @Input() element?: any;
}

describe('SidebarComponent', () => {
  let component: SidebarComponent;
  let fixture: ComponentFixture<SidebarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, FormsModule],
      declarations: [SidebarComponent, SidebarElementsStubComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SidebarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start with empty searchbar', () => {
    const searchBar: HTMLElement = fixture.nativeElement.querySelector('.sidebar__search > input');
    expect(searchBar.textContent).toBe('');
  });

  it('should update search property when search bar is updated', () => {
    const searchBar: HTMLInputElement = fixture.nativeElement.querySelector('.sidebar__search > input');
    searchBar.value = 'test';
    searchBar.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    expect(component.search).toBe('test');
  });

  it('should display groups', () => {
    component.groups = [
      { name: 'group1', types: [] },
      { name: 'group2', types: [] }
    ];
    fixture.detectChanges();
    const groupElements = fixture.nativeElement.querySelectorAll('ul > li > span');
    const groupNames = [...groupElements].map((e: HTMLElement) => e.textContent);
    expect(groupNames).toEqual(['group1', 'group2']);
  });
});
