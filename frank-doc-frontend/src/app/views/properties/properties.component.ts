import { Component, computed, inject, Signal } from '@angular/core';
import { AppService } from '../../app.service';
import { Property } from '../../frankdoc.types';
import { KeyValuePipe } from '@angular/common';
import { NameWbrPipe } from '../../components/name-wbr.pipe';
import { JavadocTransformDirective } from '../../components/javadoc-transform.directive';
import { DEFAULT_RETURN_CHARACTER } from '../../app.constants';

@Component({
  selector: 'app-properties',
  imports: [KeyValuePipe, NameWbrPipe, JavadocTransformDirective],
  templateUrl: './properties.component.html',
  styleUrl: './properties.component.scss',
})
export class PropertiesComponent {
  protected readonly properties: Signal<Property[]> = computed(() => this.appService.frankDoc()?.properties ?? []);

  private readonly appService: AppService = inject(AppService);
  protected readonly scrollToElement = this.appService.scrollToElement;
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;
}
