import { Component, computed, inject, OnInit, Signal } from '@angular/core';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { RouterOutlet } from '@angular/router';
import { AlertComponent } from '@frankframework/angular-components';
import { AppService, Filter } from './app.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Element, Label } from './frankdoc.types';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HeaderComponent, FooterComponent, RouterOutlet, AlertComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  protected version: Signal<string> = computed(() => this.appService.frankDoc()?.metadata.version ?? 'unknown');
  protected error: string | null = null;

  private appService: AppService = inject(AppService);

  ngOnInit(): void {
    this.appService.getFrankDoc().subscribe({
      next: (data) => {
        this.appService.frankDoc.set(data);
        const filters = this.getFiltersFromLabels(data.labels);
        this.assignFrankDocElementsToFilters(filters, data.elements);
        this.appService.filters.set(filters);
      },
      error: (error: HttpErrorResponse) => {
        this.error = `Couldn't retrieve FrankDoc file: ${error.statusText}`;
      },
    });
  }

  private getFiltersFromLabels(labels: Label[]): Filter[] {
    return labels.map((filter) => ({
      name: filter.label,
      labels: filter.values.map((labelName) => ({
        name: labelName,
        elements: [],
      })),
    }));
  }

  private assignFrankDocElementsToFilters(filters: Filter[], elements: Record<string, Element>): void {
    for (const element of Object.values(elements)) {
      if (!element.labels) continue;

      for (const elementFilterName in element.labels) {
        const elementFilter = element.labels[elementFilterName];
        const filter = filters.find((filter) => filter.name === elementFilterName);

        if (!filter) continue;
        for (const elementLabel of elementFilter) {
          const label = filter.labels.find((label) => label.name === elementLabel);
          if (!label) continue;
          label.elements.push(element.name);
        }
      }
    }
  }
}
