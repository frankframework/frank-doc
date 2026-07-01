import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { IconBaseComponent } from '@frankframework/angular-components';

type CaretRotation = 'up' | 'down';

@Component({
  selector: 'app-icon-caret',
  imports: [],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './icon-caret.component.html',
})
export class IconCaretComponent extends IconBaseComponent {
  @Input() rotation: CaretRotation = 'down';
}
