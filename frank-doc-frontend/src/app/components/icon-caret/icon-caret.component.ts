import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { SolarAltArrowDown, SolarAltArrowUp } from '@solar-icons/angular';

type CaretRotation = 'up' | 'down';

@Component({
  selector: 'app-icon-caret',
  imports: [SolarAltArrowDown, SolarAltArrowUp],
  changeDetection: ChangeDetectionStrategy.Eager,
  template: `@if (rotation === 'down') {
      <svg solarAltArrowDown [size]="size" />
    } @else if (rotation === 'up') {
      <svg solarAltArrowUp [size]="size" />
    }`,
})
export class IconCaretComponent {
  @Input() rotation: CaretRotation = 'down';
  @Input() size = 24;
}
