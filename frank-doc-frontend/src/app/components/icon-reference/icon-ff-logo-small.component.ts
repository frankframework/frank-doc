import { Component, ChangeDetectionStrategy, input } from '@angular/core';

@Component({
  selector: 'app-icon-ff-logo-small',
  imports: [],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './icon-ff-logo-small.component.html',
})
export class IconFfLogoSmallComponent {
  public size = input<number>(24);
}
