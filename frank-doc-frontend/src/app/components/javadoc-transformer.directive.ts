import { Directive, inject, Input, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';
import { transformLink } from './javadoc';
import { FrankDoc } from '../frankdoc.types';
import { AppService } from '../app.service';

@Directive({
  selector: '[appJavadocTransformer]',
  standalone: true,
})
export class JavadocTransformerDirective implements OnInit {
  @Input({ required: true }) javadocText!: string;
  @Input({ required: true }) elements!: FrankDoc['elements'];

  private templateRef: TemplateRef<unknown> = inject(TemplateRef);
  private ViewContainerRef: ViewContainerRef = inject(ViewContainerRef);
  private appService: AppService = inject(AppService);

  ngOnInit(): void {
    let value = `${this.javadocText}`;
    if (value !== '' || this.elements) {
      const markdownLinkRegex = /\[(.*?)]\((.+?)\)/g;
      const linkRegex = /(?:{@link\s(.*?)})/g;
      value = value.replace(markdownLinkRegex, '<a target="_blank" href="$2" alt="$1">$1</a>'); // old regex: /\[(.*?)\]\((.+?)\)/g
      value = value.replaceAll('\\"', '"');
      value = value.replace(linkRegex, (_, captureGroup) =>
        transformLink(captureGroup, this.elements, this.appService),
      );
    }

    this.ViewContainerRef.createEmbeddedView(this.templateRef, {
      $implicit: value,
    });
  }
}
