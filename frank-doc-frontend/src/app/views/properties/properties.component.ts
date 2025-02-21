import { Component, computed, inject, Signal } from '@angular/core';
import { AppService } from '../../app.service';
import { Element, Property } from '../../frankdoc.types';
import { NameWbrPipe } from '../../components/name-wbr.pipe';
import { JavadocTransformDirective } from '../../components/javadoc-transform.directive';
import { DEFAULT_RETURN_CHARACTER } from '../../app.constants';
import { CollapseDirective } from '../../components/collapse.directive';
import { IconCaretComponent } from '../../icons/icon-caret-down/icon-caret.component';

@Component({
  selector: 'app-properties',
  imports: [NameWbrPipe, JavadocTransformDirective, CollapseDirective, IconCaretComponent],
  templateUrl: './properties.component.html',
  styleUrl: './properties.component.scss',
})
export class PropertiesComponent {
  protected readonly properties: Signal<Property[]> = computed(() => this.appService.frankDoc()?.properties ?? []);
  protected readonly frankDocElements: Signal<Record<string, Element> | null> = computed(
    () => this.appService.frankDoc()?.elements ?? null,
  );
  protected collapsedFilterGroups: Record<string, boolean> = {};

  private readonly appService: AppService = inject(AppService);
  protected readonly scrollToElement = this.appService.scrollToElement;
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;

  getPropertyNamesFromGroup(group: Property): string[] {
    return group.properties.map((prop) => prop.name);
  }

  getNameOrNull(name: string): string | null {
    return name === '' ? null : name;
  }

  convertToId(idString: string): string {
    return idString.replaceAll('.', '_').replaceAll(' ', '_');
  }
}
