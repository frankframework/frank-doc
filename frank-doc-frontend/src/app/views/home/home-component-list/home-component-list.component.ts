import { Component, Input } from '@angular/core';
import { KeyValuePipe } from '@angular/common';
import { ButtonComponent } from '@frankframework/angular-components';
import { FuseResult } from 'fuse.js';
import { Element } from '../../../frankdoc.types';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home-component-list',
  standalone: true,
  imports: [KeyValuePipe, ButtonComponent, RouterLink],
  templateUrl: './home-component-list.component.html',
  styleUrl: './home-component-list.component.scss',
})
export class HomeComponentListComponent {
  @Input() components: FuseResult<Element>[] = [];

  protected relatedComponents: FuseResult<Element>[] = [];

  getFirstFilter(labels?: Record<string, string[]>): string {
    if (!labels) return '-';
    const labelGroups = Object.keys(labels);
    if (labelGroups.length === 0) return '-';
    return labelGroups[0];
  }

  getFirstLabel(labels?: Record<string, string[]>): string {
    if (!labels) return '-';
    const labelGroups = Object.keys(labels);
    if (labelGroups.length === 0) return '-';
    return labels[labelGroups[0]][0];
  }
}
