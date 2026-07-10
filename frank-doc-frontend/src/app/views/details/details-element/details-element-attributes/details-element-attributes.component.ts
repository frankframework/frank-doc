import { NgClass, NgTemplateOutlet } from '@angular/common';
import { Component, computed, inject, input, Signal } from '@angular/core';
import {
  Attribute,
  DeprecationInfo,
  Elements,
  EnumValue,
  InheritedParentElementProperties,
} from '@frankframework/doc-library-core';
import { JavadocTransformDirective, NgFFDoc } from '@frankframework/doc-library-ng';
import { AppService } from '../../../../app.service';
import { CollapseDirective } from '../../../../components/collapse.directive';
import { NameWbrPipe } from '../../../../components/name-wbr.pipe';
import { IconCaretComponent } from '../../../../icons/icon-caret-down/icon-caret.component';
import { DEFAULT_RETURN_CHARACTER } from '../../../../app.constants';

type EnumValueEntry = {
  valueName: string;
  value: EnumValue;
};

@Component({
  selector: 'app-details-element-attributes',
  imports: [IconCaretComponent, NgTemplateOutlet, JavadocTransformDirective, NameWbrPipe, NgClass, CollapseDirective],
  templateUrl: './details-element-attributes.component.html',
  styleUrl: '../details-element.component.scss',
})
export class DetailsElementAttributesComponent {
  public attributesRequired = input.required<Record<string, Attribute>>();
  public attributesOptional = input.required<Record<string, Attribute>>();
  public inheritedAttributesRequired = input.required<InheritedParentElementProperties<Attribute>[]>();
  public inheritedAttributesOptional = input.required<InheritedParentElementProperties<Attribute>[]>();

  protected elements: Signal<Elements> = computed(() => this.ffDoc.elements() ?? {});
  protected collapsedInheritedThreshold = 1;
  protected collapsedOptions = {
    attributes: false,
    parameters: false,
    children: false,
    forwards: false,
    inheritedRequired: new Map<string, boolean>(),
    inheritedOptional: new Map<string, boolean>(),
  };
  protected readonly appService: AppService = inject(AppService);
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;
  protected readonly getRecordEntries = this.appService.getRecordEntries;
  protected readonly isRecordGreaterThanZero = this.appService.isRecordGreaterThanZero;
  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();

  protected getInheritedOptionalCollapseOptions(parentElementName: string, defaultValue: boolean): boolean {
    return this.getInheritedCollapseOptions(this.collapsedOptions.inheritedOptional, parentElementName, defaultValue);
  }

  protected getDeprecatedTitle(deprecatedInfo: DeprecationInfo): string {
    return `${deprecatedInfo.description ?? 'This has been deprecated!'}${deprecatedInfo.since ? `\nSince ${deprecatedInfo.since}` : ''}`;
  }

  protected getFriendlyType(type: Attribute['type']): string {
    switch (type) {
      case 'int': {
        return 'number';
      }
      case 'bool':
      case 'boolean': {
        return 'boolean';
      }
      default: {
        return 'text';
      }
    }
  }

  protected getEnumValues(enumName: string): EnumValueEntry[] {
    const enums = this.ffDoc.enums();
    return Object.entries(enums[enumName]).map(([enumValueName, enumValue]) => ({
      valueName: enumValueName,
      value: enumValue,
    }));
  }

  protected enumValuesHaveDescriptions(enumValuesEntries: EnumValueEntry[]): boolean {
    return enumValuesEntries.some((entry) => !!entry.value.description);
  }

  // stupid badly untyped angular templates
  protected castToAttribute(value: unknown): Attribute {
    return value as Attribute;
  }

  private getInheritedCollapseOptions(
    map: Map<string, boolean>,
    parentElementName: string,
    defaultValue: boolean,
  ): boolean {
    return map.get(parentElementName) ?? defaultValue;
  }
}
