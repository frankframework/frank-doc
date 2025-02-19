import { Directive, ElementRef, HostListener, inject, Input } from '@angular/core';
import { environment } from '../../../environments/environment';

@Directive({
  selector: '[appVersionFormat]',
})
export class VersionFormatDirective {
  private fullVersion: string = '';
  private shortVersion: string = '';
  private elementAnimation: Animation | null = null;
  private elementRef: ElementRef<HTMLElement> = inject(ElementRef);
  private element = this.elementRef.nativeElement;
  private readonly animationSpeed: number = 300;

  @Input()
  set version(version: string) {
    this.fullVersion = version;
    this.shortVersion = version.split('-')[0];
    this.element.textContent = this.shortVersion;
  }

  @HostListener('mouseenter')
  onMouseEnter(): void {
    this.cancelAnimation();
    this.transition(this.fullVersion);
  }

  @HostListener('mouseleave')
  onMouseLeave(): void {
    this.cancelAnimation();
    this.transition(this.shortVersion);
  }

  transition(newText: string): void {
    if (environment.hideSnapshotVersion) {
      const beforeWidth = this.getClientWidth();
      this.element.textContent = newText;
      const afterWidth = this.getClientWidth();

      this.elementAnimation = this.element.animate(
        { width: [`${beforeWidth}px`, `${afterWidth}px`] },
        { duration: this.animationSpeed, easing: 'ease-in-out' },
      );
      this.elementAnimation.finished
        .catch(() => {
          // NOOP
        })
        .finally(() => {
          this.elementAnimation = null;
        });
    }
  }

  private cancelAnimation(): void {
    if (this.elementAnimation) {
      this.elementAnimation.cancel();
    }
  }

  private getClientWidth(): number {
    return this.element.clientWidth;
  }
}
