import { Component } from '@angular/core';
import { ButtonComponent, SearchComponent } from '@frankframework/angular-components';
import { FormsModule } from '@angular/forms';
import { HomeComponentListComponent } from './home-component-list/home-component-list.component';
import { AppService } from '../../app.service';
import { Element } from '../../frankdoc.types';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [SearchComponent, FormsModule, ButtonComponent, HomeComponentListComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  protected searchQuery = '';
  protected elements: Element[] = [];

  constructor(private appService: AppService) {}
}
