import { computed, Injectable, Signal, signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
// eslint-disable-next-line unicorn/prevent-abbreviations
import { FrankDoc } from './frankdoc.types';

export type ReferenceOptions = {
  darkmode: boolean;
};

export type Filter = {
  name: string;
  labels: FilterLabels[];
};

export type FilterLabels = {
  name: string;
  elements: string[];
};

@Injectable({
  providedIn: 'root',
})
export class AppService {
  readonly frankDoc: WritableSignal<FrankDoc | null> = signal(null);
  readonly darkmode: WritableSignal<boolean> = signal(false);
  readonly filters: Signal<Filter[]> = computed(
    // eslint-disable-next-line unicorn/consistent-function-scoping
    () =>
      this.frankDoc()?.labels.map((category) => ({
        name: category.label,
        labels: category.values.map((labelName) => ({
          name: labelName,
          elements: [],
        })),
      })) ?? [],
  );

  constructor(private http: HttpClient) {}

  getFrankDoc(): Observable<FrankDoc> {
    return this.http.get<FrankDoc>(`${environment.frankDocUrl}?cache=${Date.now()}`);
  }
}
