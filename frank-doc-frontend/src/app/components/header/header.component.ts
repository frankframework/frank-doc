import { Component, Input } from '@angular/core';
import { IconFfLogoSmallComponent } from '../../icons/icon-reference/icon-ff-logo-small.component';
import { IconDownloadComponent } from '../../icons/icon-download/icon-download.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [IconFfLogoSmallComponent, IconDownloadComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  @Input() version: string = '';
}
