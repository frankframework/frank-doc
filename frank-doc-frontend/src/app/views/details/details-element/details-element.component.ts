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
import {
  Attribute,
  Child,
  DeprecationInfo,
  Element,
  FrankDoc,
  Note,
  ParsedTag,
  RawFrankDoc,
  Value,
} from '../../../frankdoc.types';
import { environment } from '../../../../environments/environment';
import { JavadocTransformDirective } from '../../../components/javadoc-transform.directive';
import { CollapseDirective } from '../../../components/collapse.directive';
import { IconCaretComponent } from '../../../icons/icon-caret-down/icon-caret.component';
import { IconArrowRightUpComponent } from '../../../icons/icon-arrow-right-up/icon-arrow-right-up.component';
import { AppService } from '../../../app.service';
import { DEFAULT_RETURN_CHARACTER } from '../../../app.constants';
import { HasInheritedProperties } from '../details.component';
import { Title } from '@angular/platform-browser';
import { NameWbrPipe } from '../../../components/name-wbr.pipe';

type InheritedParentElementProperties<T> = {
  parentElementName: string;
  properties: T[];
};

type InheritedProperties = {
  attributesRequired: InheritedParentElementProperties<Attribute>[];
  attributesOptional: InheritedParentElementProperties<Attribute>[];
  parameters: InheritedParentElementProperties<ParsedTag>[];
  children: InheritedParentElementProperties<Child>[];
  // forwards: InheritedParentElementProperties<ParsedTag>[];
  forwards: ParsedTag[];
};

@Component({
  selector: 'app-details-element',
  imports: [
    ChipComponent,
    KeyValuePipe,
    RouterLink,
    AlertComponent,
    JavadocTransformDirective,
    CollapseDirective,
    NgTemplateOutlet,
    IconCaretComponent,
    IconArrowRightUpComponent,
    NgClass,
    NameWbrPipe,
  ],
  templateUrl: './details-element.component.html',
  styleUrl: './details-element.component.scss',
})
export class DetailsElementComponent implements OnInit, OnChanges {
  @Input({ required: true }) element!: Element | null;
  @Input({ required: true }) frankDocElements!: FrankDoc['elements'] | null;
  @Input({ required: true }) frankDocEnums!: FrankDoc['enums'] | null;
  @Output() hasInheritedProperties = new EventEmitter<HasInheritedProperties>();

  protected readonly rawElements: Signal<RawFrankDoc['elements'] | null> = computed(
    () => this.appService.rawFrankDoc()?.elements ?? null,
  );
  protected attributesRequired: Attribute[] = [];
  protected attributesOptional: Attribute[] = [];
  protected inheritedProperties: InheritedProperties = {
    attributesRequired: [],
    attributesOptional: [],
    parameters: [],
    children: [],
    forwards: [],
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

  ngOnInit(): void {
    this.appService.applicationLoaded$.subscribe(() => (this.loading = false));
    this.loading = !this.appService.hasLoaded;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['element']) {
      this.resetInheritedProperties();
      if (this.element?.attributes) {
        this.attributesRequired = this.element.attributes.filter((attribute) => attribute.mandatory === true) ?? [];
        this.attributesOptional = this.element.attributes.filter((attribute) => !attribute.mandatory) ?? [];
      }
      if (this.element?.parent) {
        this.inheritedProperties = {
          attributesRequired: [],
          attributesOptional: [],
          parameters: [],
          children: [],
          forwards: [],
        };
        this.setInheritedProperties(this.element.parent);
        this.hasInheritedProperties.emit({ ...this._hasInheritedProperties });
      }
      this.generateElementSyntax();
      if (this.element?.name) this.titleService.setTitle(`${this.element?.name} | ${environment.applicationName}`);
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

  protected getEnumValues(enumName: string): Value[] | undefined {
    return this.frankDocEnums?.find((enumItem) => enumItem.name === enumName)?.values;
  }

  protected enumValuesHaveDescriptions(enumValues: Value[]): boolean {
    return enumValues.some((value) => !!value.description);
  }

  private resetInheritedProperties(): void {
    this.inheritedProperties = {
      attributesRequired: [],
      attributesOptional: [],
      parameters: [],
      children: [],
      forwards: [],
    };
    this._hasInheritedProperties = {
      required: false,
      optional: false,
      forwards: false,
    };
  }

  private setInheritedProperties(elementIndex: string): void {
    const element = this.rawElements()?.[elementIndex];
    if (!element) return;

    if (element.attributes) {
      const attributesRequired = element.attributes.filter((attribute) => attribute.mandatory === true);
      const attributesOptional = element.attributes.filter((attribute) => !attribute.mandatory);
      if (attributesRequired.length > 0) {
        this.inheritedProperties.attributesRequired.push({
          parentElementName: element.name,
          properties: attributesRequired,
        });
        this._hasInheritedProperties.required = true;
      }
      if (attributesOptional.length > 0) {
        this.inheritedProperties.attributesOptional.push({
          parentElementName: element.name,
          properties: attributesOptional,
        });
        this._hasInheritedProperties.optional = true;
      }
    }

    if (element.forwards) {
      this.inheritedProperties.forwards.push(...element.forwards);
      this._hasInheritedProperties.forwards = true;
    }

    if (element.parent) this.setInheritedProperties(element.parent);
  }

  private getInheritedCollapseOptions(
    map: Map<string, boolean>,
    parentElementName: string,
    defaultValue: boolean,
  ): boolean {
    return map.get(parentElementName) ?? defaultValue;
  }

  private getAllRequiredAttributes(): Attribute[] {
    return [
      ...this.inheritedProperties.attributesRequired.flatMap((item) => item.properties),
      ...this.attributesRequired,
    ];
  }

  private generateElementSyntax(): void {
    if (this.element) {
      const requiredAttributes = this.getAllRequiredAttributes()
        .map((attribute) => `${attribute.name}="${attribute.default ?? this.getTypeDefaultValue(attribute.type)}"`)
        .join(' ');
      this.elementSyntax = `
<${this.element.name} ${requiredAttributes}/>
`.trim();
    }
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
