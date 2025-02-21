import { Component, inject, Input } from '@angular/core';
import { ButtonComponent } from '@frankframework/angular-components';
import { FuseResult } from 'fuse.js';
import { Element } from '../../../frankdoc.types';
import { Router } from '@angular/router';
import { AppService } from '../../../app.service';
import { TruncatePipe } from '../../../components/truncate.pipe';
import { environment } from '../../../../environments/environment';
import { JavadocTransformDirective } from '../../../components/javadoc-transform.directive';

@Component({
  selector: 'app-home-component-list',
  imports: [ButtonComponent, TruncatePipe],
  templateUrl: './home-component-list.component.html',
  styleUrl: './home-component-list.component.scss',
})
export class HomeComponentListComponent {
  @Input() components: FuseResult<Element>[] = [];
  @Input() elements: Record<string, Element> = {};

  protected relatedComponents: FuseResult<Element>[] = [];
  protected showRelated: boolean = environment.relatedSearchResults;

  private router: Router = inject(Router);
  private appService: AppService = inject(AppService);
  protected getFirstFilter = this.appService.getFirstFilter;
  protected getFirstLabel = this.appService.getFirstLabel;

  protected navigateToElement(element: Element): void {
    const route = element.labels
      ? ['/', this.getFirstFilter(element.labels), this.getFirstLabel(element.labels), element.name]
      : ['/', element.fullName];
    this.router.navigate(route);
  }
}
