import { Component, ChangeDetectionStrategy } from '@angular/core';
import { SolarDownload, SolarSquareTopDown } from '@solar-icons/angular';
import { IconFfLogoFullComponent } from '../icon-ff-logo-full/icon-ff-logo-full.component';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-footer',
  imports: [IconFfLogoFullComponent, SolarDownload, SolarSquareTopDown],
  templateUrl: './footer.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
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
