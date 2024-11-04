import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { AlertComponent, ChipComponent } from '@frankframework/angular-components';
import { JavadocPipe } from '../../../components/javadoc.pipe';
import { KeyValuePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Attribute, Element, FrankDoc } from '../../../frankdoc.types';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-details-element',
  standalone: true,
  imports: [ChipComponent, JavadocPipe, KeyValuePipe, RouterLink, AlertComponent],
  templateUrl: './details-element.component.html',
  styleUrl: './details-element.component.scss',
})
export class DetailsElementComponent implements OnChanges {
  @Input({ required: true }) element!: Element | null;
  @Input({ required: true }) frankDocElements!: FrankDoc['elements'] | null;

  protected attributesRequired: Attribute[] = [];
  protected attributesOptional: Attribute[] = [];

  private filterColours: string[] = ['#CD55EB', '#037CD4', '#00B31D'];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['element'] && this.element?.attributes) {
      this.attributesRequired = this.element?.attributes.filter((attribute) => attribute.mandatory === true) ?? [];
      this.attributesOptional = this.element?.attributes.filter((attribute) => !attribute.mandatory) ?? [];
    }
  }

  getLabelColor(index: number): string {
    return this.filterColours[index % this.filterColours.length];
  }

  githubUrlOf(name: string): string {
    return `${environment.githubWikiBaseUrl}/${name}`;
  }

  javaDocUrlOf(fullName: string): string | null {
    return fullName.includes('.')
      ? `${environment.javadocBaseUrl}/${fullName.replaceAll('.', '/')}.html`
      : // We only have a JavaDoc URL if we have an element with a Java class. The
        // exception we handle here is <Module>.
        null;
  }
}
