import { Component, Input } from '@angular/core';
import { Forward } from 'src/app/frankdoc.types';

@Component({
  selector: 'element-forwards',
  templateUrl: './element-forwards.component.html',
  styleUrls: ['./element-forwards.component.scss']
})
export class ElementForwardsComponent {

  @Input() forwards?: Forward[];

}
