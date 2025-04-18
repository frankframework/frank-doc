import { Component } from '@angular/core';
import { IconFfLogoFullComponent } from '../../icons/icon-ff-logo-full/icon-ff-logo-full.component';
import { IconNavigateToExternalComponent } from '../../icons/icon-navigate-to-external/icon-navigate-to-external.component';
import {IconDownloadComponent} from "../../icons/icon-download/icon-download.component";
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-footer',
  imports: [IconFfLogoFullComponent, IconNavigateToExternalComponent, IconDownloadComponent],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss',
})
export class FooterComponent {
  protected year = new Date().getFullYear();

  downloadXSD(): void {
    const downloadUrl = environment.xsdUrl;

    const link = document.createElement('a');
    link.setAttribute('target', '_blank');
    link.setAttribute('href', downloadUrl);
    link.setAttribute('download', '');
    document.body.append(link);
    link.click();
    link.remove();
  }
}
