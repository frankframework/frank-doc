import { Component, Input } from '@angular/core';
import { Elements } from 'src/app/app.types';
import { ParsedTag } from 'src/app/frankdoc.types';

@Component({
  selector: 'element-forwards',
  templateUrl: './element-forwards.component.html',
  styleUrls: ['./element-forwards.component.scss']
})
export class ElementForwardsComponent {

  @Input() elements!: Elements;
  @Input() forwards?: ParsedTag[];

}
