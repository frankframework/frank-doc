import { Component, Input, OnInit } from '@angular/core';
import { AppService } from 'src/app/app.service';
import { Value } from 'src/app/frankdoc.types';

@Component({
  selector: 'attribute-description',
  templateUrl: './attribute-description.component.html',
  styleUrls: ['./attribute-description.component.scss']
})
export class AttributeDescriptionComponent implements OnInit{

  @Input() enum!: string;
  @Input() showDeprecatedElements!: boolean;

  enumFields: Value[] = [];
  //has at least 1 enum field with a description
  hasDiscriptionEnum = false;

  constructor(private appService: AppService) { }

  ngOnInit() {
    this.appService.frankDoc$.subscribe(state => {
      this.enumFields = state.enums[this.enum];
      this.hasDiscriptionEnum = this.enumFields.some(field => field.description != undefined);
    });
  }

}
