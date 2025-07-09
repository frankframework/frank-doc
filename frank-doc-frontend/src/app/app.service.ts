import { computed, Injectable, Signal, signal, WritableSignal } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { DEFAULT_RETURN_CHARACTER } from './app.constants';
import { ElementDetails, FilterLabels, NgFFDoc } from '@frankframework/ff-doc';

export type FilterGroups = Record<string, string[]>;

type HSL = {
  hue: number;
  saturation: number;
  lightness: number;
};

@Injectable({
  providedIn: 'root',
})
export class AppService {
  hasLoaded: boolean = false;
  previousSearchQuery: string = '';
  readonly darkmode: WritableSignal<boolean> = signal(false);
  readonly ffDocVersion: Signal<string> = computed(() => {
    const ffDoc = this.ffDoc.ffDoc();
    if (!this.hasLoaded && ffDoc) this.triggerApplicationLoaded();
    return ffDoc?.metadata.version ?? 'unknown';
  });

  private readonly applicationLoadedSubject: Subject<void> = new Subject();
  readonly applicationLoaded$: Observable<void> = this.applicationLoadedSubject.asObservable();

  private ffDoc: NgFFDoc = new NgFFDoc();

  getFFDoc(): NgFFDoc {
    return this.ffDoc;
  }

  getLabelEntries(filterLabels: FilterLabels): { name: string; labels: string[] }[] {
    return Object.entries(filterLabels).map(([name, labels]) => ({ name, labels }));
  }

  triggerApplicationLoaded(): void {
    this.hasLoaded = true;
    this.applicationLoadedSubject.next();
    this.applicationLoadedSubject.complete();
  }

  filterElementsBySelectedFilters(elements: ElementDetails[], selectedFilters: FilterGroups): ElementDetails[] {
    if (this.getSelectedFiltersLength(selectedFilters) === 0) return elements;
    return elements.filter((element) => {
      if (!element.labels) return false;
      for (const labelGroup of Object.keys(element.labels)) {
        if (!selectedFilters[labelGroup]) continue;
        const label = element.labels[labelGroup];
        if (selectedFilters[labelGroup].includes(label)) return true;
      }
      return false;
    });
  }

  getFirstLabelGroup(filters?: Record<string, string>): [string, string] {
    const defaultLabelGroup: [string, string] = [DEFAULT_RETURN_CHARACTER, DEFAULT_RETURN_CHARACTER];
    if (!filters) return defaultLabelGroup;
    const labelGroups = Object.entries(filters);
    if (labelGroups.length === 0) return defaultLabelGroup;
    return labelGroups[0];
  }

  getLabelColor(name: string): string {
    return this.HSLToHex(this.createHSLColorFromString(name, 78));
  }

  scrollToElement(selectors: string): void {
    const element = document.querySelector(selectors);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  }

  private getSelectedFiltersLength(selectedFilters: FilterGroups): number {
    const filterLabels = Object.values(selectedFilters);
    if (filterLabels.length === 0) return 0;
    return filterLabels.reduce((acc, labels) => acc + labels.length, 0);
  }

  private createHSLColorFromString(string: string, lightness: number): HSL {
    let hash = 0;
    for (let i = 0; i < string.length; i++) {
      hash += string.codePointAt(i) ?? 0;
    }

    const hue = hash % 360;

    return { hue, saturation: 90, lightness };
  }

  private HSLToHex(hsl: HSL): string {
    const { hue, saturation, lightness } = hsl;

    const hDecimal = lightness / 100;
    const a = (saturation * Math.min(hDecimal, 1 - hDecimal)) / 100;
    const f = (n: number): string => {
      const k = (n + hue / 30) % 12;
      const color = hDecimal - a * Math.max(Math.min(k - 3, 9 - k, 1), -1);

      return Math.round(255 * color)
        .toString(16)
        .padStart(2, '0');
    };
    return `#${f(0)}${f(8)}${f(4)}`;
  }
}
