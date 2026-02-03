import { Component, Input } from '@angular/core';
import { IconBaseComponent } from '@frankframework/angular-components';

@Component({
  selector: 'app-icon-ff-logo-full',
  imports: [],
  templateUrl: './icon-ff-logo-full.component.html',
})
export class IconFfLogoFullComponent extends IconBaseComponent {
  @Input() colourExclamationMark = '#FDC300';
  override width = 510;
  override height = 95;
  override colour = 'white';
}
