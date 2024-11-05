import { Directive, inject, Input, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';
import { FrankDoc } from '../frankdoc.types';
import { AppService } from '../app.service';
import { transformLink } from './javadoc';

@Directive({
  selector: '[javadocText]',
  standalone: true,
})
export class JavadocTextDirective implements OnInit {
  @Input({ required: true }) javadocTextOf?: string;
  @Input({ required: true }) javadocTextElements!: FrankDoc['elements'] | null;

  private templateRef: TemplateRef<unknown> = inject(TemplateRef);
  private ViewContainerRef: ViewContainerRef = inject(ViewContainerRef);
  private appService: AppService = inject(AppService);

  ngOnInit(): void {
    let value = '';
    if (this.javadocTextOf && this.javadocTextElements) {
      value = `${this.javadocTextOf}`;
      if (value !== '' || this.javadocTextElements) {
        const markdownLinkRegex = /\[(.*?)]\((.+?)\)/g;
        const linkRegex = /(?:{@link\s(.*?)})/g;
        value = value.replace(markdownLinkRegex, '<a target="_blank" href="$2" alt="$1">$1</a>'); // old regex: /\[(.*?)\]\((.+?)\)/g
        value = value.replaceAll('\\"', '"');
        value = value.replace(linkRegex, (_, captureGroup) =>
          transformLink(captureGroup, this.javadocTextElements!, this.appService),
        );
      }
    }

    this.ViewContainerRef.createEmbeddedView(this.templateRef, {
      $implicit: value,
    });
  }
}
