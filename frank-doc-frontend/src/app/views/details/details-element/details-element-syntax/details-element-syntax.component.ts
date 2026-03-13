import { Component, input, OnChanges } from '@angular/core';
import {
  Attribute,
  Child,
  ElementDetails,
  Elements,
  groupAttributesByMandatory,
} from '@frankframework/doc-library-core';

@Component({
  selector: 'app-details-element-syntax',
  imports: [],
  template:
    '<pre><button (click)="copyToClipboard(elementSyntax)">Copy</button><code [innerText]="elementSyntax"></code></pre>',
  styleUrl: './details-element-syntax.component.scss',
})
export class DetailsElementSyntaxComponent implements OnChanges {
  public element = input.required<ElementDetails>();
  public elements = input<Elements>({});
  public allRequiredAttributes = input<Record<string, Attribute>>({});

  protected elementSyntax = '';

  ngOnChanges(): void {
    const element = this.element();
    const parameters = Object.keys(element.parameters ?? {});
    const nestedElements = element.children?.filter((child) => child.mandatory) ?? [];
    this.elementSyntax = this.buildSyntax(element.name, this.allRequiredAttributes(), parameters, nestedElements);
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

  private buildSyntax(
    elementName: string,
    attributes: Record<string, Attribute>,
    parameters: string[],
    nestedElements: Child[],
  ): string {
    const attributeSyntax = this.generateAttributeSyntax(attributes);
    const elementTag = `${elementName} ${attributeSyntax}`.trimEnd();
    const innerSyntax = this.generateInnerSyntax(parameters, nestedElements);
    return innerSyntax === '' ? `<${elementTag} />` : `<${elementTag}>${innerSyntax}\n</${elementName}>`;
  }

  private generateAttributeSyntax(attributes: Record<string, Attribute>): string {
    return Object.entries(attributes)
      .map(
        ([attributeName, attribute]) =>
          `${attributeName}="${attribute.default ?? this.getTypeDefaultValue(attribute.type)}"`,
      )
      .join(' ');
  }

  private generateInnerSyntax(parameters: string[], nestedElements: Child[]): string {
    const parametersSyntax = parameters.map((name) => `\n  <param name="${name}" .../>`).join('');
    const nestedElementsSyntax = nestedElements
      .map((child) => {
        const name = this.toTitleCase(child.roleName);
        const attributes = this.getNestedElementAttributesSyntax(child.type ?? child.roleName);
        const nestedTag = attributes === '' ? name : `${name} ${attributes}`;
        return `\n  <${nestedTag}>\n    ...\n  </${name}>`;
      })
      .join('');
    // TODO forwards
    return `${parametersSyntax}${nestedElementsSyntax}`;
  }

  private getNestedElementAttributesSyntax(elementName: string): string {
    const elements = this.elements();
    const element =
      elements[elementName] ?? Object.values(elements).find((element) => element.className === elementName);
    if (!element || !element.attributes) return '';
    const { required } = groupAttributesByMandatory(element.attributes);
    return this.generateAttributeSyntax(required);
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

  private toTitleCase(string: string): string {
    return `${string.charAt(0).toUpperCase()}${string.slice(1)}`;
  }
}
