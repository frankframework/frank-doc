import { Component, Input } from '@angular/core';
import { AppService } from 'src/app/app.service';
import { Attribute, Elements } from 'src/app/app.types';

@Component({
  selector: 'element-attributes',
  templateUrl: './element-attributes.component.html',
  styleUrls: ['./element-attributes.component.scss']
})
export class ElementAttributesComponent {

  @Input() elements!: Elements;
  @Input() attributes!: Attribute[];
  @Input() showDeprecatedElements!: boolean;

  constructor(private appService: AppService) { }

  javaDocUrlOf = (fullName: string) => this.appService.javaDocUrlOf(fullName);

}
