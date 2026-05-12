import { NgClass } from '@angular/common';
import { Component, computed, inject, OnInit, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Elements, FFDocJson, Property } from '@frankframework/doc-library-core';
import { JavadocTransformDirective, NgFFDoc } from '@frankframework/doc-library-ng';
import { DEFAULT_RETURN_CHARACTER, DEFAULT_UNKNOWN_PROPERTY_GROUP } from '../../app.constants';
import { AppService } from '../../app.service';
import { CollapseDirective } from '../../components/collapse.directive';
import { NameWbrPipe } from '../../components/name-wbr.pipe';
import { IconCaretComponent } from '../../icons/icon-caret-down/icon-caret.component';
import { IconHelpComponent } from '../../icons/icon-help/icon-help.component';

@Component({
  selector: 'app-properties',
  imports: [NameWbrPipe, CollapseDirective, IconCaretComponent, NgClass, IconHelpComponent, JavadocTransformDirective],
  templateUrl: './properties.component.html',
  styleUrl: './properties.component.scss',
})
export class PropertiesComponent implements OnInit {
  protected readonly properties: Signal<FFDocJson['properties']> = computed(() => this.ffDoc.properties() ?? []);
  protected readonly elements: Signal<Elements> = computed(() => this.ffDoc.elements() ?? {});
  protected expandedFilterGroups: Signal<Record<string, boolean>> = computed(() =>
    this.properties().reduce<Record<string, boolean>>((expendedGroup, propertyGroup, index) => {
      if (this.getNameOrNull(propertyGroup.name)) return expendedGroup;
      expendedGroup[`${DEFAULT_UNKNOWN_PROPERTY_GROUP}-${index}`] = true;
      return expendedGroup;
    }, {}),
  );
  protected readonly DEFAULT_UNKNOWN_PROPERTY_GROUP = DEFAULT_UNKNOWN_PROPERTY_GROUP;
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;

  private scrollElementId: string | null = null;
  private readonly appService: AppService = inject(AppService);
  private readonly router: Router = inject(Router);
  private readonly route: ActivatedRoute = inject(ActivatedRoute);
  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((parameters) => {
      const property = parameters.get('property');
      if (property) {
        this.scrollElementId = `#${this.convertToId(property)}`;
        this.appService.scrollToElement(this.scrollElementId);
      }
    });
    if (this.scrollElementId) setTimeout(() => this.appService.scrollToElement(this.scrollElementId!), 500);
  }

  protected getPropertyNamesFromGroup(group: Property): string[] {
    return group.properties.map((property) => property.name);
  }

  protected getPropertyGroupKey(name: string, index: number): string {
    return this.getNameOrNull(name) ? name : `${DEFAULT_UNKNOWN_PROPERTY_GROUP}-${index}`;
  }

  protected getNameOrNull(name: string): string | null {
    return name === '' ? null : name;
  }

  protected convertToId(idString: string): string {
    return idString.replaceAll('.', '_').replaceAll(' ', '_');
  }

  protected scrollToElement(selector: string): void {
    this.router.navigate([], {
      relativeTo: this.route,
      scroll: 'manual',
      queryParams: {
        property: selector,
      },
    });
  }
}
