import { Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { AlertComponent, ChipComponent } from '@frankframework/angular-components';
import { KeyValuePipe, NgClass, NgTemplateOutlet } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Attribute, Child, DeprecationInfo, Element, FrankDoc, ParsedTag } from '../../../frankdoc.types';
import { environment } from '../../../../environments/environment';
import { JavadocTransformDirective } from '../../../components/javadoc-transform.directive';
import { CollapseDirective } from '../../../components/collapse.directive';
import { IconCaretComponent } from '../../../icons/icon-caret-down/icon-caret.component';
import { IconArrowRightUpComponent } from '../../../icons/icon-arrow-right-up/icon-arrow-right-up.component';
import { AppService } from '../../../app.service';
import { DEFAULT_RETURN_CHARACTER, filterColours } from '../../../app.constants';
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
  forwards: InheritedParentElementProperties<ParsedTag>[];
};

@Component({
  selector: 'app-details-element',
  standalone: true,
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
  protected undefinedType = 'string';
  protected elementSyntax: string = '';
  protected collapsedInheritedThreshold = 1;
  protected _hasInheritedProperties: HasInheritedProperties = {
    required: false,
    optional: false,
  };
  protected loading: boolean = true;
  protected attributesHasDefaults: boolean = false;
  protected usedEnums: FrankDoc['enums'] = [];

  private readonly appService: AppService = inject(AppService);
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;
  protected readonly filterColours = filterColours;
  protected readonly getLabelColor = this.appService.getLabelColor;

  private readonly titleService: Title = inject(Title);

  ngOnInit(): void {
    this.appService.applicationLoaded$.subscribe(() => (this.loading = false));
    this.loading = !this.appService.hasLoaded;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['element']) {
      if (this.element?.attributes) {
        this.attributesRequired = this.element?.attributes.filter((attribute) => attribute.mandatory === true) ?? [];
        this.attributesOptional = this.element?.attributes.filter((attribute) => !attribute.mandatory) ?? [];
        this.attributesHasDefaults = this.element.attributes.some((attribute) => !!attribute.default);
        this.usedEnums = this.filterUsedEnums();
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
      if (this.element?.name) this.titleService.setTitle(`${this.element?.name} | Frank!Doc`);
    }
  }

  protected getInheritedRequiredCollapseOptions(parentElementName: string, defaultValue: boolean): boolean {
    return this.getInheritedCollapseOptions(this.collapsedOptions.inheritedRequired, parentElementName, defaultValue);
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

  protected getEnumShortName(attribute: Attribute | string): string | null {
    let fullname = '';

    if (typeof attribute === 'string') fullname = attribute;
    else if (attribute.enum) fullname = attribute.enum;
    else return null;

    const nameParts = fullname.split('.');
    return nameParts[nameParts.length - 1];
  }

  private setInheritedProperties(elementIndex: string): void {
    const element = this.frankDocElements?.[elementIndex];
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

    if (element.parent) this.setInheritedProperties(element.parent);
  }

  private filterUsedEnums(): FrankDoc['enums'] {
    if (!this.element?.attributes) return [];

    const filteredEnums = new Set<FrankDoc['enums'][0]>();
    for (const attribute of this.element?.attributes) {
      if (attribute.enum) {
        const enumType = this.frankDocEnums?.find((enumItem) => enumItem.name === attribute.enum);
        if (enumType) filteredEnums.add(enumType);
      }
    }
    return [...filteredEnums];
  }

  private getInheritedCollapseOptions(
    map: Map<string, boolean>,
    parentElementName: string,
    defaultValue: boolean,
  ): boolean {
    return map.get(parentElementName) ?? defaultValue;
  }

  private generateElementSyntax(): void {
    if (this.element) {
      this.elementSyntax = `
<${this.element.name} name="my-${this.element.name.toLowerCase()}" />
`.trim();
    }
  }
}
