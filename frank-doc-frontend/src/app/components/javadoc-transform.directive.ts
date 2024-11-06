import { Directive, inject, Input, OnInit, Renderer2, TemplateRef, ViewContainerRef } from '@angular/core';
import { FrankDoc } from '../frankdoc.types';
import { AppService } from '../app.service';
import { transformRouterLink } from './javadoc';

/**
 * Transforms javadoc text to html text, handles links as a different template.
 * */
@Directive({
  selector: '[javadocTransform]',
  standalone: true,
})
export class JavadocTransformDirective implements OnInit {
  @Input({ required: true }) javadocTransformOf?: string;
  @Input({ required: true }) javadocTransformLink!: TemplateRef<unknown>;
  @Input({ required: true }) javadocTransformElements!: FrankDoc['elements'] | null;

  private templateRef: TemplateRef<unknown> = inject(TemplateRef);
  private viewContainerRef: ViewContainerRef = inject(ViewContainerRef);
  private appService: AppService = inject(AppService);
  private renderer: Renderer2 = inject(Renderer2);

  ngOnInit(): void {
    if (!this.javadocTransformOf || !this.javadocTransformElements) return;
    let value = `${this.javadocTransformOf}`;
    const markdownLinkRegex = /\[(.*?)]\((.+?)\)/g;
    const linkRegex = /(?:{@link\s(.*?)})/g;
    value = value.replace(markdownLinkRegex, '<a target="_blank" href="$2" alt="$1">$1</a>'); // old regex: /\[(.*?)\]\((.+?)\)/g
    value = value.replaceAll('\\"', '"');
    value = value.replace(linkRegex, (_, captureGroup) =>
      transformRouterLink(captureGroup, this.javadocTransformElements!, this.appService),
    );

    const javadocParts = value.split('\\"');

    for (const partIndexString in javadocParts) {
      const partIndex = +partIndexString;
      const part = javadocParts[partIndex];
      if (partIndex % 2 !== 0 && part.startsWith('{')) {
        try {
          const linkData = JSON.parse(part);
          this.viewContainerRef.createEmbeddedView(this.javadocTransformLink, {
            $implicit: linkData,
          });
        } catch (error) {
          console.error("Can't parse link data", error);
        }
        continue;
      }
      this.viewContainerRef.createEmbeddedView(this.templateRef, {
        $implicit: part,
      });
    }
  }
}
