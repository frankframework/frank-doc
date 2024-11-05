import { inject, Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { AppService } from '../app.service';
import { FrankDoc } from '../frankdoc.types';
import { transformLink } from './javadoc';

@Pipe({
  name: 'javadoc',
  standalone: true,
})
export class JavadocPipe implements PipeTransform {
  private appService: AppService = inject(AppService);
  private domSanatizer: DomSanitizer = inject(DomSanitizer);

  transform(value: string, elements: FrankDoc['elements'] | null): SafeHtml | string {
    if (value === '' || !elements) return value;
    const markdownLinkRegex = /\[(.*?)]\((.+?)\)/g;
    const linkRegex = /(?:{@link\s(.*?)})/g;
    value = value.replace(markdownLinkRegex, '<a target="_blank" href="$2" alt="$1">$1</a>'); // old regex: /\[(.*?)\]\((.+?)\)/g
    value = value.replaceAll('\\"', '"');
    value = value.replace(linkRegex, (_, captureGroup) => transformLink(captureGroup, elements, this.appService));
    return this.domSanatizer.bypassSecurityTrustHtml(value);
  }
}
