import { Component, inject, Input, OnChanges, SimpleChanges } from '@angular/core';
import { AlertComponent, ChipComponent } from '@frankframework/angular-components';
import { KeyValuePipe, NgTemplateOutlet } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Attribute, Child, Element, FrankDoc, ParsedTag } from '../../../frankdoc.types';
import { environment } from '../../../../environments/environment';
import { JavadocTransformDirective } from '../../../components/javadoc-transform.directive';
import { CollapseDirective } from '../../../components/collapse.directive';
import { IconCaretComponent } from '../../../icons/icon-caret-down/icon-caret.component';
import { IconArrowRightUpComponent } from '../../../icons/icon-arrow-right-up/icon-arrow-right-up.component';
import { AppService } from '../../../app.service';
import { DEFAULT_RETURN_CHARACTER, filterColours } from '../../../app.constants';

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
  ],
  templateUrl: './details-element.component.html',
  styleUrl: './details-element.component.scss',
})
export class DetailsElementComponent implements OnChanges {
  @Input({ required: true }) element!: Element | null;
  @Input({ required: true }) frankDocElements!: FrankDoc['elements'] | null;

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
  };
  protected undefinedType = 'string';
  protected elementSyntax: string = '';

  private readonly appService: AppService = inject(AppService);
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;
  protected readonly filterColours = filterColours;
  protected readonly getLabelColor = this.appService.getLabelColor;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['element']) {
      if (this.element?.attributes) {
        this.attributesRequired = this.element?.attributes.filter((attribute) => attribute.mandatory === true) ?? [];
        this.attributesOptional = this.element?.attributes.filter((attribute) => !attribute.mandatory) ?? [];
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
      }
      this.generateElementSyntax();
    }
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

  protected hasInheritedProperties(): boolean {
    return Object.values(this.inheritedProperties).some((properties) => properties.length > 0);
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
      }
      if (attributesOptional.length > 0) {
        this.inheritedProperties.attributesOptional.push({
          parentElementName: element.name,
          properties: attributesOptional,
        });
      }
    }

    if (element.parameters) {
      this.inheritedProperties.parameters.push({
        parentElementName: element.name,
        properties: element.parameters,
      });
    }

    if (element.children) {
      this.inheritedProperties.children.push({
        parentElementName: element.name,
        properties: element.children,
      });
    }

    if (element.forwards) {
      this.inheritedProperties.forwards.push({
        parentElementName: element.name,
        properties: element.forwards,
      });
    }

    if (element.parent) this.setInheritedProperties(element.parent);
  }

  private generateElementSyntax(): void {
    if (this.element) {
      this.elementSyntax = `
<${this.element.name} name="my-${this.element.name.toLowerCase()}" />
`.trim();
    }
  }
}
