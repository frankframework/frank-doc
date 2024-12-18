import { Component, Input } from '@angular/core';
import { IconBaseComponent } from '@frankframework/angular-components';

type CaretRotation = 'up' | 'down';

@Component({
  selector: 'app-icon-caret',
  standalone: true,
  imports: [],
  templateUrl: './icon-caret.component.html',
})
export class IconCaretComponent extends IconBaseComponent {
  @Input() rotation: CaretRotation = 'down';
}
