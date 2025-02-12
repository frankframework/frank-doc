import { Component } from '@angular/core';
import { IconFfLogoFullComponent } from '../../icons/icon-ff-logo-full/icon-ff-logo-full.component';
import { IconNavigateToExternalComponent } from '../../icons/icon-navigate-to-external/icon-navigate-to-external.component';

@Component({
  selector: 'app-footer',
  imports: [IconFfLogoFullComponent, IconNavigateToExternalComponent],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss',
})
export class FooterComponent {
  protected year = new Date().getFullYear();
}
