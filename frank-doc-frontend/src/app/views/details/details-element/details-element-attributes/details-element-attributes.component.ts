import { NgClass, NgTemplateOutlet } from '@angular/common';
import { Component, inject, input } from '@angular/core';
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
  styleUrl: '../details-element-options.scss',
})
export class DetailsElementAttributesComponent {
  public elements = input.required<Elements>();
  public attributesRequired = input.required<Record<string, Attribute>>();
  public attributesOptional = input.required<Record<string, Attribute>>();
  public inheritedAttributesRequired = input.required<InheritedParentElementProperties<Attribute>[]>();
  public inheritedAttributesOptional = input.required<InheritedParentElementProperties<Attribute>[]>();

  protected collapsedInheritedThreshold = 1;
  protected inheritedCollapseOptions = new Map<string, boolean>();
  protected readonly appService: AppService = inject(AppService);
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;
  protected readonly getRecordEntries = this.appService.getRecordEntries;
  protected readonly isRecordGreaterThanZero = this.appService.isRecordGreaterThanZero;
  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();

  protected getInheritedOptionalCollapseOptions(parentElementName: string, defaultValue: boolean): boolean {
    return this.getInheritedCollapseOptions(this.inheritedCollapseOptions, parentElementName, defaultValue);
  }

  protected getDeprecatedDescription(deprecatedInfo: DeprecationInfo): string {
    let description = deprecatedInfo.since ? `Has been deprecated since v${deprecatedInfo.since}` : 'Is deprecated';
    if (deprecatedInfo.forRemoval) description += ' and has been marked for removal';
    if (deprecatedInfo.description) description += `\n${deprecatedInfo.description}`;
    return description;
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
