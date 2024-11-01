import { Component, Input } from '@angular/core';
import { ChipComponent } from '@frankframework/angular-components';
import { JavadocPipe } from '../../../components/javadoc.pipe';
import { KeyValuePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Element, FrankDoc } from '../../../frankdoc.types';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-details-element',
  standalone: true,
  imports: [ChipComponent, JavadocPipe, KeyValuePipe, RouterLink],
  templateUrl: './details-element.component.html',
  styleUrl: './details-element.component.scss',
})
export class DetailsElementComponent {
  @Input({ required: true }) element!: Element | null;
  @Input({ required: true }) frankDocElements!: FrankDoc['elements'] | null;

  private filterColours: string[] = ['#CD55EB', '#037CD4', '#00B31D'];

  getLabelColor(index: number): string {
    return this.filterColours[index % this.filterColours.length];
  }

  javaDocUrlOf(fullName: string): string | null {
    return fullName.includes('.')
      ? `${environment.javadocBaseUrl}/${fullName.replaceAll('.', '/')}.html`
      : // We only have a JavaDoc URL if we have an element with a Java class. The
        // exception we handle here is <Module>.
        null;
  }
}
