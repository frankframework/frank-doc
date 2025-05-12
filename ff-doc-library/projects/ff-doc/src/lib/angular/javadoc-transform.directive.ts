import { Directive, inject, Input, OnChanges, TemplateRef, ViewContainerRef } from '@angular/core';
import { LinkData, transformAsHtml, transformAsText } from '../javadoc';
import { ElementClass } from '../frankdoc.types';

export type TemplateContext = { $implicit: string };
export type LinkTemplateContext = { $implicit: LinkData };

/**
 * Transforms javadoc text to html text, handles links as a different template.
 * */
@Directive({
  selector: '[fdJavadocTransform]',
  standalone: true,
})
export class JavadocTransformDirective implements OnChanges {
  @Input({ required: true }) fdJavadocTransformOf?: string;
  @Input({ required: true }) fdJavadocTransformElements!: Record<string, ElementClass> | null;
  @Input() fdJavadocTransformLink?: TemplateRef<LinkTemplateContext>;
  @Input() fdJavadocTransformAsText = false;

  private readonly templateRef: TemplateRef<TemplateContext> = inject(TemplateRef);
  private readonly viewContainerRef: ViewContainerRef = inject(ViewContainerRef);

  ngOnChanges(): void {
    if (this.fdJavadocTransformOf === '') this.fdJavadocTransformOf = '-';
    if (!this.fdJavadocTransformOf || !this.fdJavadocTransformElements) return;
    const javadocParts = this.fdJavadocTransformAsText
      ? transformAsText(this.fdJavadocTransformOf, this.fdJavadocTransformElements)
      : transformAsHtml(this.fdJavadocTransformOf, this.fdJavadocTransformElements, !!this.fdJavadocTransformLink);
    this.viewContainerRef.clear();

    for (const partIndexString in javadocParts) {
      const partIndex = +partIndexString;
      const part = javadocParts[partIndex];
      if (this.fdJavadocTransformLink && partIndex % 2 !== 0 && part.startsWith('{')) {
        try {
          const linkData: LinkData = JSON.parse(part);
          this.viewContainerRef.createEmbeddedView<LinkTemplateContext>(this.fdJavadocTransformLink, {
            $implicit: linkData,
          });
        } catch (error) {
          console.error("Can't parse link data", error);
        }
        continue;
      }
      this.viewContainerRef.createEmbeddedView<TemplateContext>(this.templateRef, {
        $implicit: part,
      });
    }
  }
}
