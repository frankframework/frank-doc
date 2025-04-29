import { Directive, HostListener, Input, booleanAttribute, Output, EventEmitter, AfterViewInit } from '@angular/core';

@Directive({
  selector: '[collapse]',
  standalone: true,
})
export class CollapseDirective implements AfterViewInit {
  @Input({ required: true }) collapse!: HTMLElement;
  @Input({ transform: booleanAttribute }) collapsed: boolean = false;
  @Input() animationSpeed: number = 300;
  @Output() collapsedChange = new EventEmitter<boolean>();

  private collapseAnimation: Animation | null = null;
  private clientHeight: number = 0;

  ngAfterViewInit(): void {
    this.setInitialState();
  }

  @HostListener('click')
  onClick(): void {
    this.collapsed = !this.collapsed;
    this.collapsedChange.emit(this.collapsed);
    this.updateState();
  }

  updateState(): void {
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

  private setInitialState(): void {
    if (this.collapsed) {
      this.clientHeight = this.collapse.clientHeight;
      this.collapse.classList.add('collapsed');
    }
  }

  private collapseElement(): void {
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

  private expandElement(): void {
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
