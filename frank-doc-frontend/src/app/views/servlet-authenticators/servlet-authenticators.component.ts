import { Component, computed, inject, Signal } from '@angular/core';
import { JavadocTransformDirective } from '../../components/javadoc-transform.directive';
import { CredentialProvider, Element, ServletAuthenticator } from '../../frankdoc.types';
import { AppService } from '../../app.service';
import { DEFAULT_RETURN_CHARACTER } from '../../app.constants';
import { NameWbrPipe } from '../../components/name-wbr.pipe';
import { ActivatedRoute, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-servlet-authenticators',
  imports: [JavadocTransformDirective, NameWbrPipe, NgClass],
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
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;

  private readonly router: Router = inject(Router);
  private readonly route: ActivatedRoute = inject(ActivatedRoute);

  protected readonly selectedAuthenticatorName = toSignal(
    this.route.paramMap.pipe(map((params) => params.get('name'))),
  );

  protected readonly selectedAuthenticator = computed(() => {
    const authenticatorName = this.selectedAuthenticatorName();
    const authenticators = this.servletAuthenticators();

    if (!authenticatorName || authenticators.length === 0) {
      return null;
    }

    return authenticators.find((authenticator) => authenticator.name === authenticatorName) || null;
  });

  protected handleSelectedAuthenticator(provider: CredentialProvider): void {
    this.router.navigate(['servlet-authenticators', provider.name]);
  }
}
