import { Component, Input, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-icon-ff-logo-full',
  imports: [],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './icon-ff-logo-full.component.html',
})
export class IconFfLogoFullComponent {
  @Input() width = 510;
  @Input() height = 95;
  @Input() color = 'white';
  @Input() colorExclamationMark = '#FDC300';
}
