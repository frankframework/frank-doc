import { Component, computed, inject, Signal } from '@angular/core';
import { JavadocTransformDirective } from '../../components/javadoc-transform.directive';
import { Element, ServletAuthenticator } from '../../frankdoc.types';
import { AppService } from '../../app.service';
import { DEFAULT_RETURN_CHARACTER } from '../../app.constants';
import { NameWbrPipe } from '../../components/name-wbr.pipe';

@Component({
  selector: 'app-servlet-authenticators',
  imports: [JavadocTransformDirective, NameWbrPipe],
  templateUrl: './servlet-authenticators.component.html',
  styleUrl: './servlet-authenticators.component.scss',
})
export class ServletAuthenticatorsComponent {
  protected readonly servletAuthenticators: Signal<ServletAuthenticator[]> = computed(
    () => this.appService.frankDoc()?.servletAuthenticators.sort((a, b) => a.name.localeCompare(b.name)) ?? [],
  );
  protected readonly frankDocElements: Signal<Record<string, Element> | null> = computed(
    () => this.appService.frankDoc()?.elements ?? null,
  );

  private readonly appService: AppService = inject(AppService);
  protected readonly scrollToElement = this.appService.scrollToElement;
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;
}
