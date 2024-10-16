import { Component, Input } from '@angular/core';
import { Element } from '../../../frankdoc.types';

@Component({
  selector: 'app-home-component-list',
  standalone: true,
  imports: [],
  templateUrl: './home-component-list.component.html',
  styleUrl: './home-component-list.component.scss',
})
export class HomeComponentListComponent {
  @Input() components: Element[] = [];
  @Input() searchQuery: string = '';
}
