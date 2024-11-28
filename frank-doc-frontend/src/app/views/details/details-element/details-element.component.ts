import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { AlertComponent, ChipComponent } from '@frankframework/angular-components';
import { KeyValuePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Attribute, Element, FrankDoc, ParsedTag } from '../../../frankdoc.types';
import { environment } from '../../../../environments/environment';
import { JavadocTransformDirective } from '../../../components/javadoc-transform.directive';
import { CollapseDirective } from '../../../components/collapse.directive';

type InheritedParentElementProperties<T> = {
  parentElementName: string;
  properties: T[];
};

type InheritedProperties = {
  attributes: InheritedParentElementProperties<Attribute>[];
  parameters: InheritedParentElementProperties<ParsedTag>[];
  forwards: InheritedParentElementProperties<ParsedTag>[];
};

@Component({
  selector: 'app-details-element',
  standalone: true,
  imports: [ChipComponent, KeyValuePipe, RouterLink, AlertComponent, JavadocTransformDirective, CollapseDirective],
  templateUrl: './details-element.component.html',
  styleUrl: './details-element.component.scss',
})
export class DetailsElementComponent implements OnChanges {
  @Input({ required: true }) element!: Element | null;
  @Input({ required: true }) frankDocElements!: FrankDoc['elements'] | null;

  protected attributesRequired: Attribute[] = [];
  protected attributesOptional: Attribute[] = [];
  protected inheritedProperties: InheritedProperties = {
    attributes: [],
    parameters: [],
    forwards: [],
  };

  private filterColours: string[] = ['#CD55EB', '#037CD4', '#00B31D'];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['element']) {
      if (this.element?.attributes) {
        this.attributesRequired = this.element?.attributes.filter((attribute) => attribute.mandatory === true) ?? [];
        this.attributesOptional = this.element?.attributes.filter((attribute) => !attribute.mandatory) ?? [];
      }
      if (this.element?.parent) {
        this.getInheritedProperties(this.element.parent);
      }
    }
  }

  protected getLabelColor(index: number): string {
    return this.filterColours[index % this.filterColours.length];
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

  private getInheritedProperties(elementIndex: string): void {
    elementIndex;
    // TODO get inherited properties from parent element recursively
  }
}
