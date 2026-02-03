import { Directive } from '@angular/core';

@Directive({
  selector: '[appCard]',
})
export class CardDirective {
  activeHover = false;
}
