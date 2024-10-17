import { Component, Input } from '@angular/core';
import { IconReferenceComponent } from '../../icons/icon-reference/icon-reference.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [IconReferenceComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  @Input() version: string = '';
}
