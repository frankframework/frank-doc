import { Component, Input } from '@angular/core';
import { IconFfLogoSmallComponent } from '../../icons/icon-reference/icon-ff-logo-small.component';
import { IconDownloadComponent } from '../../icons/icon-download/icon-download.component';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { environment } from '../../../environments/environment';
import { VersionFormatDirective } from './version-format.directive';

@Component({
  selector: 'app-header',
  imports: [
    IconFfLogoSmallComponent,
    IconDownloadComponent,
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
}
