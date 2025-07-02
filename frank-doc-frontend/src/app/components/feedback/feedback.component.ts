import { Component, computed, inject, Signal } from '@angular/core';
import { Router } from '@angular/router';
import { IconDocumentAddComponent } from '../../icons/icon-document-add/icon-document-add.component';
import { AppService } from '../../app.service';
import { NgFFDoc } from '@frankframework/ff-doc';

@Component({
  selector: 'app-feedback',
  imports: [IconDocumentAddComponent],
  templateUrl: './feedback.component.html',
  styleUrl: './feedback.component.scss',
})
export class FeedbackComponent {
  private readonly appService: AppService = inject(AppService);
  private readonly router: Router = inject(Router);
  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();
  private readonly ffDocVersion: Signal<string> = computed(() => this.ffDoc.ffDoc()?.metadata.version ?? 'unknown');

  onFeedbackClick(): void {
    const currentPage = this.router.url;
    const selectedText = window.getSelection()?.toString().trim() || 'None';

    const markdown = `
**Current Page:** \`${currentPage}\`
**Frank!Doc Version:** \`${this.ffDocVersion()}\`
**Selected Text:** \`${selectedText}\`
`;

    const encodedMarkdown = encodeURIComponent(markdown.trim());
    const githubUrl = `https://github.com/frankframework/frankframework/issues/new?template=4-documentation.yml&context=${encodedMarkdown}`;

    window.open(githubUrl, '_blank');
  }
}
