import { Component, Input } from '@angular/core';
import { AppService } from 'src/app/app.service';
import { Attribute } from 'src/app/app.types';

@Component({
  selector: 'element-attributes',
  templateUrl: './element-attributes.component.html',
  styleUrls: ['./element-attributes.component.scss']
})
export class ElementAttributesComponent {

  @Input() attributes!: Attribute[];
  @Input() showDeprecatedElements!: boolean;

  constructor(private appService: AppService) {
    if(!this.attributes[0].from){
      
    }
  }

  javaDocUrlOf = (fullName: string) => this.appService.javaDocUrlOf(fullName);

}
