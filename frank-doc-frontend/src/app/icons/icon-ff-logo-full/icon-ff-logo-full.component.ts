import { Component, Input } from '@angular/core';
import { IconBaseComponent } from '@frankframework/angular-components';

@Component({
  selector: 'app-icon-ff-logo-full',
  standalone: true,
  imports: [],
  templateUrl: './icon-ff-logo-full.component.html',
})
export class IconFfLogoFullComponent extends IconBaseComponent {
  override width: number = 510;
  override height: number = 95;
  override colour: string = 'white';
  @Input() colourExclamationMark: string = '#FDC300';
}
