import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Element, ElementLabels, FrankDoc } from './frankdoc.types';
import { IFuseOptions } from 'fuse.js';

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
  readonly fuseOptions: IFuseOptions<Element> = {
    keys: [
      {
        name: 'name',
        weight: 2,
      },
      {
        name: 'fullName',
        weight: 2,
      },
      {
        name: 'elementNames', // default weight is 1.0
      },
      {
        name: 'labels.FrankDocGroup',
      },
      {
        name: 'attributes',
        weight: 0.7,
      },
      {
        name: 'description',
        weight: 0.5,
      },
    ],
    includeScore: true,
    includeMatches: true,
    threshold: 0.2,
    minMatchCharLength: 3,
    ignoreLocation: true,
  };
  readonly filterColours: string[] = ['#CD55EB', '#037CD4', '#00B31D'];

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
    if (!labels) return '-';
    const labelGroups = Object.keys(labels);
    if (labelGroups.length === 0) return '-';
    return labelGroups[0];
  }

  getFirstLabel(labels?: Record<string, string[]>): string {
    if (!labels) return '-';
    const labelGroups = Object.keys(labels);
    if (labelGroups.length === 0) return '-';
    return labels[labelGroups[0]][0];
  }

  getLabelColor(colours: string[], index: number): string {
    return colours[index % colours.length];
  }

  private getSelectedFiltersLength(selectedFilters: ElementLabels): number {
    if (Object.values(selectedFilters).length === 0) return 0;
    return Object.values(selectedFilters).reduce((acc, val) => acc + val.length, 0);
  }
}
