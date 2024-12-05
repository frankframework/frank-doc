import { Directive, HostListener, Input, booleanAttribute, Output, EventEmitter } from '@angular/core';

@Directive({
  selector: '[collapse]',
  standalone: true,
})
export class CollapseDirective {
  @Input({ required: true }) collapse!: HTMLElement;
  @Input({ transform: booleanAttribute }) collapsed: boolean = false;
  @Input() animationSpeed: number = 150;
  @Output() collapsedChange = new EventEmitter<boolean>();

  @HostListener('click')
  onClick(): void {
    this.collapsed = !this.collapsed;
    if (this.collapsed) {
      this.collapseElement();
    } else {
      this.expandElement();
    }
    this.collapsedChange.emit(this.collapsed);
  }

  collapseElement(): void {
    this.collapse.style.overflowY = 'hidden';
    this.collapse
      .animate({ height: [`${this.collapse.clientHeight}px`, '0px'] }, this.animationSpeed)
      .finished.then(() => {
        this.collapse.style.height = '0px';
        this.collapse.style.overflowY = '';
        this.collapse.style.overflow = 'hidden';
      });
  }

  expandElement(): void {
    this.collapse.style.height = '';
    this.collapse.style.overflowY = 'hidden';
    this.collapse
      .animate({ height: ['0px', `${this.collapse.clientHeight}px`] }, this.animationSpeed)
      .finished.then(() => {
        this.collapse.removeAttribute('style');
      });
  }
}
