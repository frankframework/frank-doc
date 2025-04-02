import { Component, computed, inject, Signal } from '@angular/core';
import { CredentialProvider, Element } from '../../frankdoc.types';
import { DEFAULT_RETURN_CHARACTER } from '../../app.constants';
import { AppService } from '../../app.service';
import { JavadocTransformDirective } from '../../components/javadoc-transform.directive';

@Component({
  selector: 'app-credential-providers',
  imports: [JavadocTransformDirective],
  templateUrl: './credential-providers.component.html',
  styleUrl: './credential-providers.component.scss',
})
export class CredentialProvidersComponent {
  protected readonly credentialProviders: Signal<CredentialProvider[]> = computed(
    () => this.appService.frankDoc()?.credentialProviders.sort((a, b) => a.name.localeCompare(b.name)) ?? [],
  );
  protected readonly frankDocElements: Signal<Record<string, Element> | null> = computed(
    () => this.appService.frankDoc()?.elements ?? null,
  );

  private readonly appService: AppService = inject(AppService);
  protected readonly scrollToElement = this.appService.scrollToElement;
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;
}
