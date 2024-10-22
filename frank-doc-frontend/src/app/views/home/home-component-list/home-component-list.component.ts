import { Component, Input } from '@angular/core';
import { KeyValuePipe } from '@angular/common';
import { ButtonComponent } from '@frankframework/angular-components';
import { FuseResult } from 'fuse.js';
import { Element } from '../../../frankdoc.types';

@Component({
  selector: 'app-home-component-list',
  standalone: true,
  imports: [KeyValuePipe, ButtonComponent],
  templateUrl: './home-component-list.component.html',
  styleUrl: './home-component-list.component.scss',
})
export class HomeComponentListComponent {
  @Input() components: FuseResult<Element>[] = [];

  protected relatedComponents: FuseResult<Element>[] = [];
}
