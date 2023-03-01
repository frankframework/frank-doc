import { Component, Input } from '@angular/core';
import { Forward } from 'src/app/frankdoc.types';

@Component({
  selector: 'element-parameters',
  templateUrl: './element-parameters.component.html',
  styleUrls: ['./element-parameters.component.scss']
})
export class ElementParametersComponent {

  @Input() parameters?: Forward[];
  @Input() parametersDescription?: string;

}
