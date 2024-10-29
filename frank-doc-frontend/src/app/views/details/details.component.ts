import { Component } from '@angular/core';
import { ChipComponent, SearchComponent } from '@frankframework/angular-components';

@Component({
  selector: 'app-details',
  standalone: true,
  imports: [SearchComponent, ChipComponent],
  templateUrl: './details.component.html',
  styleUrl: './details.component.scss',
})
export class DetailsComponent {}
