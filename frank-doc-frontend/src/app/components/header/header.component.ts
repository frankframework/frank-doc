import { Component, Input } from '@angular/core';
import { IconFfLogoSmallComponent } from '../../icons/icon-reference/icon-ff-logo-small.component';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { VersionFormatDirective } from './version-format.directive';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-header',
  imports: [
    IconFfLogoSmallComponent,
    RouterLink,
    RouterLinkActive,
    /*IconDarkmodeComponent,*/
    VersionFormatDirective,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  @Input() version: string = '';
  protected applicationName = environment.applicationName.replace('FF! ', '');
}
