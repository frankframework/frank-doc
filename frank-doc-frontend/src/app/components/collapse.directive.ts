import { Directive, HostListener, Input, booleanAttribute, Output, EventEmitter } from '@angular/core';

@Directive({
  selector: '[collapse]',
  standalone: true,
})
export class CollapseDirective {
  @Input({ required: true }) collapse!: HTMLElement;
  @Input({ transform: booleanAttribute }) collapsed: boolean = false;
  @Input() animationSpeed: number = 300;
  @Output() collapsedChange = new EventEmitter<boolean>();

  private collapseAnimation: Animation | null = null;
  private clientHeight: number = 0;

  @HostListener('click')
  onClick(): void {
    this.collapsed = !this.collapsed;
    this.collapsedChange.emit(this.collapsed);

    if (this.collapseAnimation) {
      this.collapseAnimation.cancel();
      return;
    }

    if (this.collapsed) {
      this.clientHeight = this.collapse.clientHeight;
      this.collapseElement();
    } else {
      this.expandElement();
    }
  }

  collapseElement(): void {
    this.collapse.classList.add('transforming');
    this.collapseAnimation = this.collapse.animate(
      { height: [`${this.clientHeight}px`, '0px'] },
      { duration: this.animationSpeed, easing: 'ease-in-out' },
    );
    this.collapseAnimation.finished
      .then(() => {
        this.collapse.classList.add('collapsed');
      })
      .finally(() => {
        this.collapse.classList.remove('transforming');
        this.collapseAnimation = null;
      });
  }

  expandElement(): void {
    this.collapseAnimation = this.collapse.animate(
      { height: ['0px', `${this.clientHeight}px`] },
      { duration: this.animationSpeed, easing: 'ease-in-out' },
    );
    this.collapseAnimation.finished
      .then(() => {
        this.collapse.classList.remove('collapsed');
      })
      .finally(() => {
        this.collapseAnimation = null;
      });
  }
}
