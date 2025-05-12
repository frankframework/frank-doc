import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { computed, inject, Signal, signal, WritableSignal } from '@angular/core';
import { Elements, FFDocBase, Filters } from '../ff-doc-base';
import { FFDocJson } from '../frankdoc.types';

export class NgFFDoc extends FFDocBase {
  public readonly enums: Signal<FFDocJson['enums']> = computed(() => this.ffDoc()?.enums ?? {});
  public readonly properties: Signal<FFDocJson['properties']> = computed(() => this.ffDoc()?.properties ?? []);
  public readonly credentialProviders: Signal<FFDocJson['credentialProviders']> = computed(
    () => this.ffDoc()?.credentialProviders ?? {},
  );
  public readonly servletAuthenticators: Signal<FFDocJson['servletAuthenticators']> = computed(
    () => this.ffDoc()?.servletAuthenticators ?? {},
  );

  private readonly _ffDoc: WritableSignal<FFDocJson | null> = signal(null);
  private readonly _elements: WritableSignal<Elements | null> = signal(null);
  private readonly _filters: WritableSignal<Filters> = signal({});

  private http: HttpClient = inject(HttpClient);

  get ffDoc(): Signal<FFDocJson | null> {
    return this._ffDoc.asReadonly();
  }

  get elements(): Signal<Elements | null> {
    return this._elements.asReadonly();
  }

  get filters(): Signal<Filters> {
    return this._filters.asReadonly();
  }

  initialize(jsonUrl: string): void {
    this.fetchJson(jsonUrl).subscribe((ffDocJson) => {
      this._ffDoc.set(ffDocJson);
      this._filters.set(
        this.assignFrankDocElementsToFilters(this.getFiltersFromLabels(ffDocJson.labels), ffDocJson.elementNames),
      );
      this._elements.set(this.getXMLElements(ffDocJson));
    });
  }

  private fetchJson(url: string): Observable<FFDocJson> {
    return this.http.get<FFDocJson>(url);
  }
}
