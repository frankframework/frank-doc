import { Component, computed, inject, Signal } from '@angular/core';
import { AppService } from '../../app.service';
import { Element, Property } from '../../frankdoc.types';
import { NameWbrPipe } from '../../components/name-wbr.pipe';
import { JavadocTransformDirective } from '../../components/javadoc-transform.directive';
import { DEFAULT_RETURN_CHARACTER } from '../../app.constants';

@Component({
  selector: 'app-properties',
  imports: [NameWbrPipe, JavadocTransformDirective],
  templateUrl: './properties.component.html',
  styleUrl: './properties.component.scss',
})
export class PropertiesComponent {
  protected readonly properties: Signal<Property[]> = computed(() => this.appService.frankDoc()?.properties ?? []);
  protected readonly frankDocElements: Signal<Record<string, Element> | null> = computed(
    () => this.appService.frankDoc()?.elements ?? null,
  );

  private readonly appService: AppService = inject(AppService);
  protected readonly scrollToElement = this.appService.scrollToElement;
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;
}
