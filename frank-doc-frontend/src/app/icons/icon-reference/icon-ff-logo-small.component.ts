import { Component } from '@angular/core';
import { IconBaseComponent } from '@frankframework/angular-components';
import { styleColours } from '../../app.constants';

@Component({
  selector: 'app-icon-ff-logo-small',
  standalone: true,
  imports: [],
  templateUrl: './icon-ff-logo-small.component.html',
})
export class IconFfLogoSmallComponent extends IconBaseComponent {
  protected readonly styleColours = styleColours;
}
