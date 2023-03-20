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
});
