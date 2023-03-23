import { Component, Input, OnInit } from '@angular/core';
import { AppService } from 'src/app/app.service';
import { Group, Element } from 'src/app/frankdoc.types';
import { Elements } from 'src/app/app.types';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  @Input() groups!: Group[];
  @Input() elements!: Elements;
  @Input() showDeprecatedElements!: boolean;
  @Input() showInheritance!: boolean;
  @Input() selectedGroup?: Group;
  @Input() element?: Element;

  search = "";

  constructor(private appService: AppService, private router: Router) { }

  showHideDeprecated = () => this.appService.showHideDeprecated();
  showHideInheritance = () => this.appService.showHideInheritance();

  downloadXSD(){
    const downloadUrl = '/xml/xsd/FrankConfig.xsd';

    const link = document.createElement('a');
    link.setAttribute('target', '_blank');
    link.setAttribute('href', downloadUrl);
    link.setAttribute('download', "");
    document.body.appendChild(link);
    link.click();
    link.remove();
  }

  selectGroup(groupName: string){
    if (this.selectedGroup?.name === groupName)
      return;
    if (!this.element){
      this.router.navigate([groupName]);
      return;
    }

    const newGroup = this.groups.find(group => group.name === groupName);
    if (newGroup)
      this.selectedGroup = newGroup;
  }

}
