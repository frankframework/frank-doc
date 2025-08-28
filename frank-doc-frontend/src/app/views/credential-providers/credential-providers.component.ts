import { Component, computed, inject, Signal } from '@angular/core';
import { DEFAULT_RETURN_CHARACTER } from '../../app.constants';
import { AppService } from '../../app.service';
import { NgClass } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { map } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { CredentialProvider, Elements, JavadocTransformDirective, NgFFDoc } from '@frankframework/ff-doc';

@Component({
  selector: 'app-credential-providers',
  imports: [NgClass, JavadocTransformDirective],
  templateUrl: './credential-providers.component.html',
  styleUrl: './credential-providers.component.scss',
})
export class CredentialProvidersComponent {
  protected readonly credentialProviderNames: Signal<string[]> = computed(
    () => Object.keys(this.ffDoc.credentialProviders()).sort((a, b) => a.localeCompare(b)) ?? [],
  );
  protected readonly elements: Signal<Elements | null> = computed(() => this.ffDoc.elements() ?? null);
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;
  protected readonly selectedProvider: Signal<{ name: string; provider: CredentialProvider } | null> = computed(() => {
    const providerName = this.selectedProviderName();
    const providers = this.ffDoc.credentialProviders();

    if (!providerName || Object.keys(providers).length === 0) {
      return null;
    }

    return { name: providerName, provider: providers[providerName] };
  });

  private readonly route: ActivatedRoute = inject(ActivatedRoute);
  protected readonly selectedProviderName = toSignal(this.route.paramMap.pipe(map((params) => params.get('name'))));

  private readonly router: Router = inject(Router);
  private readonly appService: AppService = inject(AppService);
  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();

  protected handleSelectedProvider(providerName: string): void {
    this.router.navigate(['credential-providers', providerName]);
  }
}
