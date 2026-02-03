import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { IconDocumentAddComponent } from '../../icons/icon-document-add/icon-document-add.component';
import { AppService } from '../../app.service';

@Component({
  selector: 'app-feedback',
  imports: [IconDocumentAddComponent],
  templateUrl: './feedback.component.html',
  styleUrl: './feedback.component.scss',
})
export class FeedbackComponent {
  private readonly appService: AppService = inject(AppService);
  private readonly router: Router = inject(Router);

  onFeedbackClick(): void {
    const currentPage = this.router.url;
    const selectedText = globalThis.getSelection()?.toString().trim() || 'None';

    const markdown = `
**Current Page:** \`${currentPage}\`
**Frank!Doc Version:** \`${this.appService.ffDocVersion()}\`
**Selected Text:** \`${selectedText}\`
`;

    const encodedMarkdown = encodeURIComponent(markdown.trim());
    const githubUrl = `https://github.com/frankframework/frankframework/issues/new?template=4-documentation.yml&context=${encodedMarkdown}`;

    window.open(githubUrl, '_blank');
  }
}
