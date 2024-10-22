import { Component, Input } from '@angular/core';
import { IconFfLogoSmallComponent } from '../../icons/icon-reference/icon-ff-logo-small.component';
import { IconDownloadComponent } from '../../icons/icon-download/icon-download.component';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { IconDarkmodeComponent } from '../../icons/icon-darkmode/icon-darkmode.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [IconFfLogoSmallComponent, IconDownloadComponent, RouterLink, RouterLinkActive, IconDarkmodeComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  @Input() version: string = '';
}
