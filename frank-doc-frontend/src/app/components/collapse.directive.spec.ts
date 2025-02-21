import { CollapseDirective } from './collapse.directive';
import { Component, DebugElement } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

@Component({
  template: ``,
  imports: [
    /*CollapseDirective*/
  ],
})
class TestComponent {}

describe('CollapseDirective', () => {
  let fixture: ComponentFixture<TestComponent>;
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  let des: DebugElement[];
  let component: TestComponent;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      imports: [CollapseDirective, TestComponent],
    }).createComponent(TestComponent);
    fixture.detectChanges(); // initial binding
    // all elements with an attached HighlightDirective
    des = fixture.debugElement.queryAll(By.directive(CollapseDirective));
    component = fixture.componentInstance;
  });

  it('should create an instance', () => {
    expect(component).toBeTruthy();
  });
});
