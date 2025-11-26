import {
  Component,
  computed,
  EventEmitter,
  inject,
  Input,
  OnChanges,
  OnInit,
  Output,
  Signal,
  SimpleChanges,
} from '@angular/core';
import { AlertComponent, AlertType, ChipComponent } from '@frankframework/angular-components';
import { KeyValuePipe, NgClass, NgTemplateOutlet } from '@angular/common';
import { RouterLink } from '@angular/router';
import { environment } from '../../../../environments/environment';
import { CollapseDirective } from '../../../components/collapse.directive';
import { IconCaretComponent } from '../../../icons/icon-caret-down/icon-caret.component';
import { IconArrowRightUpComponent } from '../../../icons/icon-arrow-right-up/icon-arrow-right-up.component';
import { AppService } from '../../../app.service';
import { DEFAULT_RETURN_CHARACTER } from '../../../app.constants';
import { HasInheritedProperties } from '../details.component';
import { Title } from '@angular/platform-browser';
import { NameWbrPipe } from '../../../components/name-wbr.pipe';
import { JavadocTransformDirective, NgFFDoc } from '@frankframework/doc-library-ng';
import {
  Attribute,
  DeprecationInfo,
  ElementClass,
  ElementDetails,
  Elements,
  EnumValue,
  getInheritedProperties,
  groupAttributesByMandatory,
  InheritedProperties,
  Note,
} from '@frankframework/doc-library-core';

type EnumValueEntry = {
  valueName: string;
  value: EnumValue;
};

type RecordEntry<T> = {
  name: string;
  value: T;
};

@Component({
  selector: 'app-details-element',
  imports: [
    ChipComponent,
    KeyValuePipe,
    RouterLink,
    AlertComponent,
    CollapseDirective,
    NgTemplateOutlet,
    IconCaretComponent,
    IconArrowRightUpComponent,
    NgClass,
    NameWbrPipe,
    JavadocTransformDirective,
  ],
  templateUrl: './details-element.component.html',
  styleUrl: './details-element.component.scss',
})
export class DetailsElementComponent implements OnInit, OnChanges {
  @Input({ required: true }) element!: ElementDetails | null;
  @Output() hasInheritedProperties = new EventEmitter<HasInheritedProperties>();

  protected elements: Signal<Elements> = computed(() => this.ffDoc.elements() ?? {});
  protected attributesRequired: Record<string, Attribute> = {};
  protected attributesOptional: Record<string, Attribute> = {};
  protected allRequiredAttributes: Record<string, Attribute> = {};
  protected inheritedProperties: InheritedProperties = {
    parentElements: [],
    attributesRequired: [],
    attributesOptional: [],
    forwards: {},
    enums: {},
  };
  protected collapsedOptions = {
    attributes: false,
    parameters: false,
    children: false,
    forwards: false,
    inheritedRequired: new Map<string, boolean>(),
    inheritedOptional: new Map<string, boolean>(),
  };
  protected elementSyntax: string = '';
  protected collapsedInheritedThreshold = 1;
  protected _hasInheritedProperties: HasInheritedProperties = {
    required: false,
    optional: false,
    forwards: false,
  };
  protected loading: boolean = true;

  protected readonly appService: AppService = inject(AppService);
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;

  private readonly titleService: Title = inject(Title);
  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();

  ngOnInit(): void {
    this.appService.applicationLoaded$.subscribe(() => (this.loading = false));
    this.loading = !this.appService.hasLoaded;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['element']) {
      this.resetInheritedProperties();
      const classElement = this.getClassElement();
      if (classElement?.attributes) {
        const { required, optional } = groupAttributesByMandatory(classElement.attributes);
        this.attributesRequired = required;
        this.attributesOptional = optional;
      }
      if (classElement?.parent) {
        this.inheritedProperties = getInheritedProperties(
          classElement,
          this.ffDoc.ffDoc()?.elements ?? {},
          this.ffDoc.ffDoc()?.enums ?? {},
        );
      }
      this.allRequiredAttributes = groupAttributesByMandatory(this.element?.attributes ?? {});
      this.hasInheritedProperties.emit({ ...this._hasInheritedProperties });
      this.generateElementSyntax();

      if (this.element?.name)
        this.titleService.setTitle(`${environment.applicationName} | ${this.element?.name ?? 'Element details'}`);
    }
  }

  protected getInheritedOptionalCollapseOptions(parentElementName: string, defaultValue: boolean): boolean {
    return this.getInheritedCollapseOptions(this.collapsedOptions.inheritedOptional, parentElementName, defaultValue);
  }

  protected githubUrlOf(name: string): string {
    return `${environment.githubWikiBaseUrl}/${name}`;
  }

  protected javaDocUrlOf(fullName: string): string | null {
    return fullName.includes('.')
      ? `${environment.javadocBaseUrl}/${fullName.replaceAll('.', '/')}.html`
      : // We only have a JavaDoc URL if we have an element with a Java class. The
        // exception we handle here is <Module>.
        null;
  }

  protected copyToClipboard(text: string): void {
    const element = document.createElement('textarea');
    element.value = text;
    element.setAttribute('readonly', '');
    element.style.position = 'absolute';
    element.style.left = '-9999px';
    document.body.append(element);
    element.select();
    document.execCommand('copy'); // TODO: soon deprecated but no real solution yet
    element.remove();
  }

  protected hasAnyInheritedProperties(): boolean {
    return this._hasInheritedProperties.required || this._hasInheritedProperties.optional;
  }

  protected getDeprecatedTitle(deprecatedInfo: DeprecationInfo): string {
    return `${deprecatedInfo.description ?? 'This has been deprecated!'}${deprecatedInfo.since ? `\nSince ${deprecatedInfo.since}` : ''}`;
  }

  protected getWarningType(type: Note['type']): AlertType {
    switch (type) {
      case 'WARNING': {
        return 'warning';
      }
      case 'DANGER': {
        return 'error';
      }
      default: {
        return 'info';
      }
    }
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

  protected getRecordEntries<T>(Record: Record<string, T>): RecordEntry<T>[] {
    return Object.entries(Record).map(([name, value]) => ({ name, value }));
  }

  protected enumValuesHaveDescriptions(enumValuesEntries: EnumValueEntry[]): boolean {
    return enumValuesEntries.some((entry) => !!entry.value.description);
  }

  protected isRecordGreaterThanZero(record: Record<string, unknown>): boolean {
    return Object.keys(record).length > 0;
  }

  // stupid badly untyped angular templates
  protected castToAttribute(value: unknown): Attribute {
    return value as Attribute;
  }

  private getClassElement(): ElementClass | null {
    const classElements = this.ffDoc.ffDoc()?.elements;
    return classElements && this.element ? classElements[this.element?.className] : null;
  }

  private resetInheritedProperties(): void {
    this.inheritedProperties = {
      parentElements: [],
      attributesRequired: [],
      attributesOptional: [],
      forwards: {},
      enums: {},
    };
    this._hasInheritedProperties = {
      required: false,
      optional: false,
      forwards: false,
    };
  }

  private getInheritedCollapseOptions(
    map: Map<string, boolean>,
    parentElementName: string,
    defaultValue: boolean,
  ): boolean {
    return map.get(parentElementName) ?? defaultValue;
  }

  private getAllRequiredAttributes(): Record<string, Attribute> {
    return {
      ...this.inheritedProperties.attributesRequired
        .map((item) => item.properties)
        .reduce((mergedAttributes, attributes) => ({ ...mergedAttributes, ...attributes }), {}),
      ...this.attributesRequired,
    };
  }

  private generateElementSyntax(): void {
    if (!this.element) return;
    const requiredAttributes = Object.entries(this.getAllRequiredAttributes())
      .map(
        ([attributeName, attribute]) =>
          `${attributeName}="${attribute.default ?? this.getTypeDefaultValue(attribute.type)}"`,
      )
      .join(' ');
    const params = Object.keys(this.element.parameters ?? {})
      .map((paramName) => `  <param name="${paramName}" .../>`)
      .join('\n');
    const hasNestedElements = this.element.children !== undefined && this.element.children.length > 0;
    this.elementSyntax = this.buildSyntax(this.element.name, requiredAttributes, params, hasNestedElements);
  }

  private buildSyntax(elementName: string, attributes: string, params: string, hasNestedElements: boolean): string {
    const elementTag = `${elementName} ${attributes}`.trimEnd();
    const closing =
      hasNestedElements || params !== ''
        ? `>\n  ${this.generateInnerSyntax(params, hasNestedElements)}\n</${elementName}>`
        : ' />';
    return `<${elementTag}${closing}`.trim();
  }

  private generateInnerSyntax(params: string, hasNestedElements: boolean): string {
    return `${params} ${hasNestedElements ? '\n  ...' : ''}`.trim();
  }

  private getTypeDefaultValue(type: Attribute['type']): string {
    switch (type) {
      case 'int': {
        return '0';
      }
      case 'bool':
      case 'boolean': {
        return 'false';
      }
      default: {
        return '';
      }
    }
  }
}
