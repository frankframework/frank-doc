import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Element, ElementLabels, FrankDoc } from './frankdoc.types';
import { DEFAULT_RETURN_CHARACTER } from './app.constants';

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
  readonly filters: WritableSignal<Filter[]> = signal([]);
  readonly darkmode: WritableSignal<boolean> = signal(false);

  private readonly http: HttpClient = inject(HttpClient);

  getFrankDoc(): Observable<FrankDoc> {
    return this.http.get<FrankDoc>(`${environment.frankDocUrl}?cache=${Date.now()}`);
  }

  fullNameToSimpleName(fullName: string): string {
    return fullName.slice(fullName.lastIndexOf('.') + 1);
  }

  filterElementsBySelectedFilters(elements: Element[], selectedFilters: ElementLabels): Element[] {
    if (this.getSelectedFiltersLength(selectedFilters) === 0) return elements;
    return elements.filter((element) => {
      if (!element.labels) return false;
      for (const labelKey of Object.keys(element.labels)) {
        if (!selectedFilters[labelKey]) continue;
        for (const labelItem of element.labels[labelKey]) {
          if (selectedFilters[labelKey].includes(labelItem)) {
            return true;
          }
        }
      }
      return false;
    });
  }

  getFirstFilter(labels?: Record<string, string[]>): string {
    if (!labels) return DEFAULT_RETURN_CHARACTER;
    const labelGroups = Object.keys(labels);
    if (labelGroups.length === 0) return DEFAULT_RETURN_CHARACTER;
    return labelGroups[0];
  }

  getFirstLabel(labels?: Record<string, string[]>): string {
    if (!labels) return DEFAULT_RETURN_CHARACTER;
    const labelGroups = Object.keys(labels);
    if (labelGroups.length === 0) return DEFAULT_RETURN_CHARACTER;
    return labels[labelGroups[0]][0];
  }

  getLabelColor(colours: string[], index: number): string {
    return colours[index % colours.length];
  }

  scrollToElement(selectors: string): void {
    const element = document.querySelector(selectors);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  }

  private getSelectedFiltersLength(selectedFilters: ElementLabels): number {
    if (Object.values(selectedFilters).length === 0) return 0;
    return Object.values(selectedFilters).reduce((acc, val) => acc + val.length, 0);
  }
}
