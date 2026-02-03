import { Directive, HostListener, Input, booleanAttribute, Output, EventEmitter, AfterViewInit } from '@angular/core';

@Directive({
  selector: '[appCollapse]',
  standalone: true,
})
export class CollapseDirective implements AfterViewInit {
  @Input({ required: true }) appCollapse!: HTMLElement;
  @Input({ transform: booleanAttribute }) collapsed = false;
  @Input() animationSpeed = 300;
  @Output() collapsedChange = new EventEmitter<boolean>();

  private collapseAnimation: Animation | null = null;
  private clientHeight = 0;

  @HostListener('click')
  onClick(): void {
    this.collapsed = !this.collapsed;
    this.collapsedChange.emit(this.collapsed);
    this.updateState();
  }

  ngAfterViewInit(): void {
    this.setInitialState();
  }

  updateState(): void {
    if (this.collapseAnimation) {
      this.collapseAnimation.cancel();
      return;
    }

    if (this.collapsed) {
      this.clientHeight = this.appCollapse.clientHeight;
      this.collapseElement();
    } else {
      this.expandElement();
    }
  }

  private setInitialState(): void {
    if (this.collapsed) {
      this.clientHeight = this.appCollapse.clientHeight;
      this.appCollapse.classList.add('collapsed');
    }
  }

  private collapseElement(): void {
    this.appCollapse.classList.add('transforming');
    this.collapseAnimation = this.appCollapse.animate(
      { height: [`${this.clientHeight}px`, '0px'] },
      { duration: this.animationSpeed, easing: 'ease-in-out' },
    );
    this.collapseAnimation.finished
      .then(() => {
        this.appCollapse.classList.add('collapsed');
      })
      .finally(() => {
        this.appCollapse.classList.remove('transforming');
        this.collapseAnimation = null;
      });
  }

  private expandElement(): void {
    this.collapseAnimation = this.appCollapse.animate(
      { height: ['0px', `${this.clientHeight}px`] },
      { duration: this.animationSpeed, easing: 'ease-in-out' },
    );
    this.collapseAnimation.finished
      .then(() => {
        this.appCollapse.classList.remove('collapsed');
      })
      .finally(() => {
        this.collapseAnimation = null;
      });
  }
}
