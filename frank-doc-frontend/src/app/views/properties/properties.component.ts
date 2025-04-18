import { Component, computed, inject, Signal } from '@angular/core';
import { AppService } from '../../app.service';
import { Element, Property } from '../../frankdoc.types';
import { NameWbrPipe } from '../../components/name-wbr.pipe';
import { JavadocTransformDirective } from '../../components/javadoc-transform.directive';
import { DEFAULT_RETURN_CHARACTER, DEFAULT_UNKNOWN_PROPERTY_GROUP } from '../../app.constants';
import { CollapseDirective } from '../../components/collapse.directive';
import { IconCaretComponent } from '../../icons/icon-caret-down/icon-caret.component';
import { NgClass } from '@angular/common';
import { IconHelpComponent } from '../../icons/icon-help/icon-help.component';

@Component({
  selector: 'app-properties',
  imports: [NameWbrPipe, JavadocTransformDirective, CollapseDirective, IconCaretComponent, NgClass, IconHelpComponent],
  templateUrl: './properties.component.html',
  styleUrl: './properties.component.scss',
})
export class PropertiesComponent {
  protected readonly properties: Signal<Property[]> = computed(() => this.appService.frankDoc()?.properties ?? []);
  protected readonly frankDocElements: Signal<Record<string, Element> | null> = computed(
    () => this.appService.frankDoc()?.elements ?? null,
  );
  protected expandedFilterGroups: Signal<Record<string, boolean>> = computed(() =>
    this.properties().reduce<Record<string, boolean>>((acc, propertyGroup, index) => {
      if (!!this.getNameOrNull(propertyGroup.name)) return acc;
      acc[`${DEFAULT_UNKNOWN_PROPERTY_GROUP}-${index}`] = true;
      return acc;
    }, {}),
  );
  protected readonly DEFAULT_UNKNOWN_PROPERTY_GROUP = DEFAULT_UNKNOWN_PROPERTY_GROUP;

  private readonly appService: AppService = inject(AppService);
  protected readonly scrollToElement = this.appService.scrollToElement;
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;

  getPropertyNamesFromGroup(group: Property): string[] {
    return group.properties.map((prop) => prop.name);
  }

  getPropertyGroupKey(name: string, index: number): string {
    return this.getNameOrNull(name) ? name : `${DEFAULT_UNKNOWN_PROPERTY_GROUP}-${index}`;
  }

  getNameOrNull(name: string): string | null {
    return name === '' ? null : name;
  }

  convertToId(idString: string): string {
    return idString.replaceAll('.', '_').replaceAll(' ', '_');
  }
}
