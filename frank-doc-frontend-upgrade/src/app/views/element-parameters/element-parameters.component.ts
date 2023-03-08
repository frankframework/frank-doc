import { Component, Input } from '@angular/core';
import { Elements } from 'src/app/app.types';
import { Forward } from 'src/app/frankdoc.types';

@Component({
  selector: 'element-parameters',
  templateUrl: './element-parameters.component.html',
  styleUrls: ['./element-parameters.component.scss']
})
export class ElementParametersComponent {

  @Input() elements!: Elements;
  @Input() parameters?: Forward[];
  @Input() parametersDescription?: string;

}
