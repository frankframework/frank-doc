import { Component, Input } from '@angular/core';
import { Element } from '../../../frankdoc.types';
import { KeyValuePipe } from '@angular/common';
import { ButtonComponent } from '@frankframework/angular-components';

@Component({
  selector: 'app-home-component-list',
  standalone: true,
  imports: [KeyValuePipe, ButtonComponent],
  templateUrl: './home-component-list.component.html',
  styleUrl: './home-component-list.component.scss',
})
export class HomeComponentListComponent {
  @Input() components: Record<string, Element> = {};
  @Input() searchQuery: string = '';
  protected readonly Object = Object;
}
