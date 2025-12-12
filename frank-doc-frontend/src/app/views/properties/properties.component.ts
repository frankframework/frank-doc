import { Component, computed, inject, Signal } from '@angular/core';
import { AppService } from '../../app.service';
import { NameWbrPipe } from '../../components/name-wbr.pipe';
import { DEFAULT_RETURN_CHARACTER, DEFAULT_UNKNOWN_PROPERTY_GROUP } from '../../app.constants';
import { CollapseDirective } from '../../components/collapse.directive';
import { IconCaretComponent } from '../../icons/icon-caret-down/icon-caret.component';
import { NgClass } from '@angular/common';
import { IconHelpComponent } from '../../icons/icon-help/icon-help.component';
import { Elements, FFDocJson, Property } from '@frankframework/doc-library-core';
import { JavadocTransformDirective, NgFFDoc } from '@frankframework/doc-library-ng';

@Component({
  selector: 'app-properties',
  imports: [NameWbrPipe, CollapseDirective, IconCaretComponent, NgClass, IconHelpComponent, JavadocTransformDirective],
  templateUrl: './properties.component.html',
  styleUrl: './properties.component.scss',
})
export class PropertiesComponent {
  protected readonly properties: Signal<FFDocJson['properties']> = computed(() => this.ffDoc.properties() ?? []);
  protected readonly elements: Signal<Elements> = computed(() => this.ffDoc.elements() ?? {});
  protected expandedFilterGroups: Signal<Record<string, boolean>> = computed(() =>
    this.properties().reduce<Record<string, boolean>>((acc, propertyGroup, index) => {
      if (!!this.getNameOrNull(propertyGroup.name)) return acc;
      acc[`${DEFAULT_UNKNOWN_PROPERTY_GROUP}-${index}`] = true;
      return acc;
    }, {}),
  );
  protected readonly DEFAULT_UNKNOWN_PROPERTY_GROUP = DEFAULT_UNKNOWN_PROPERTY_GROUP;
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;

  private readonly appService: AppService = inject(AppService);
  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();
  protected readonly scrollToElement = this.appService.scrollToElement;

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
