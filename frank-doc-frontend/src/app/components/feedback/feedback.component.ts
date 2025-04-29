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
  private appService: AppService = inject(AppService);
  private router: Router = inject(Router);

  onFeedbackClick(): void {
    const currentPage = this.router.url;
    const frankDocVersion = this.appService.frankDoc()?.metadata.version ?? 'unknown';
    const selectedText = window.getSelection()?.toString().trim() || 'None';

    const markdown = `
**Current Page:** \`${currentPage}\`
**Frank!Doc Version:** \`${frankDocVersion}\`
**Selected Text:** \`${selectedText}\`
`;

    const encodedMarkdown = encodeURIComponent(markdown.trim());
    const githubUrl = `https://github.com/frankframework/frankframework/issues/new?template=4-documentation.yml&context=${encodedMarkdown}`;

    window.open(githubUrl, '_blank');
  }
}
