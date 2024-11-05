import { Directive, ElementRef, inject, OnInit, QueryList, Renderer2, ViewChildren } from '@angular/core';

@Directive({
  selector: '[appTransformToRoutelink]',
  standalone: true,
})
export class TransformToRoutelinkDirective implements OnInit {
  @ViewChildren('span[data-link]') linkSpanElements!: QueryList<HTMLSpanElement>;

  private renderer: Renderer2 = inject(Renderer2);
  private elementRef: ElementRef<HTMLElement> = inject(ElementRef);
  private element = this.elementRef.nativeElement;

  ngOnInit(): void {
    for (const linkSpanElement of this.linkSpanElements) {
      const href = linkSpanElement.dataset['href'];
      const text = linkSpanElement.dataset['text'];

      const anchorElement: HTMLAnchorElement = this.renderer.createElement('a');
      this.renderer.setProperty(anchorElement, '[routerLink]', `['${href}']`);
      anchorElement.textContent = text ?? '';

      this.renderer.appendChild(this.element, anchorElement);
    }
  }
}
