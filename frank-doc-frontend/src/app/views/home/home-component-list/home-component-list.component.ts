import { Component, Input, OnChanges } from '@angular/core';
import { Element } from '../../../frankdoc.types';
import { KeyValuePipe } from '@angular/common';

@Component({
  selector: 'app-home-component-list',
  standalone: true,
  imports: [KeyValuePipe],
  templateUrl: './home-component-list.component.html',
  styleUrl: './home-component-list.component.scss',
})
export class HomeComponentListComponent implements OnChanges {
  @Input() components: Record<string, Element> = {};
  @Input() searchQuery: string = '';

  ngOnChanges(): void {
    console.log(this.components);
  }

  test(value: unknown): void {
    console.log(value);
  }
}
