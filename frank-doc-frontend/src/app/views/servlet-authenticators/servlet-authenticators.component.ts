import { Component, computed, inject, Signal } from '@angular/core';
import { AppService } from '../../app.service';
import { DEFAULT_RETURN_CHARACTER } from '../../app.constants';
import { NameWbrPipe } from '../../components/name-wbr.pipe';
import { ActivatedRoute, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { NgClass } from '@angular/common';
import { Elements, JavadocTransformDirective, NgFFDoc, ServletAuthenticator } from '@frankframework/ff-doc';
import { AlertComponent } from '@frankframework/angular-components';

@Component({
  selector: 'app-servlet-authenticators',
  imports: [NameWbrPipe, NgClass, JavadocTransformDirective, AlertComponent],
  templateUrl: './servlet-authenticators.component.html',
  styleUrl: './servlet-authenticators.component.scss',
})
export class ServletAuthenticatorsComponent {
  protected readonly servletAuthenticatorNames: Signal<string[]> = computed(
    () => Object.keys(this.ffDoc.servletAuthenticators()).sort((a, b) => a.localeCompare(b)) ?? [],
  );
  protected readonly elements: Signal<Elements | null> = computed(() => this.ffDoc.elements() ?? null);
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;

  private readonly appService: AppService = inject(AppService);
  private readonly router: Router = inject(Router);
  private readonly route: ActivatedRoute = inject(ActivatedRoute);
  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();

  protected readonly selectedAuthenticatorName = toSignal(
    this.route.paramMap.pipe(map((params) => params.get('name'))),
  );

  protected readonly selectedAuthenticator: Signal<{ name: string; authenticator: ServletAuthenticator } | null> =
    computed(() => {
      const authenticatorName = this.selectedAuthenticatorName();
      const authenticators = this.ffDoc.servletAuthenticators();

      if (!authenticatorName || Object.keys(authenticators).length === 0) {
        return null;
      }

      return { name: authenticatorName, authenticator: authenticators[authenticatorName] };
    });

  protected handleSelectedAuthenticator(authenticator: string): void {
    this.router.navigate(['servlet-authenticators', authenticator]);
  }
}
