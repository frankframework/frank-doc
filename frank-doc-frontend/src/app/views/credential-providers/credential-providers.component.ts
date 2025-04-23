import { Component, computed, inject, Signal } from '@angular/core';
import { CredentialProvider, Element } from '../../frankdoc.types';
import { DEFAULT_RETURN_CHARACTER } from '../../app.constants';
import { AppService } from '../../app.service';
import { JavadocTransformDirective } from '../../components/javadoc-transform.directive';
import { NgClass } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { map } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-credential-providers',
  imports: [JavadocTransformDirective, NgClass],
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
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;

  private readonly router: Router = inject(Router);
  private readonly route: ActivatedRoute = inject(ActivatedRoute);

  protected readonly selectedProviderName = toSignal(this.route.paramMap.pipe(map((params) => params.get('name'))));

  protected readonly selectedProvider = computed(() => {
    const providerName = this.selectedProviderName();
    const providers = this.credentialProviders();

    if (!providerName || providers.length === 0) {
      return null;
    }

    return providers.find((provider) => provider.name === providerName) || null;
  });

  protected handleSelectedProvider(provider: CredentialProvider): void {
    this.router.navigate(['credential-providers', provider.name]);
  }
}
