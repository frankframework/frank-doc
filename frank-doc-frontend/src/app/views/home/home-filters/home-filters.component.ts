import { Component } from '@angular/core';
import { ButtonComponent } from '@frankframework/angular-components';

@Component({
  selector: 'app-home-filters',
  standalone: true,
  imports: [ButtonComponent],
  templateUrl: './home-filters.component.html',
  styleUrl: './home-filters.component.scss',
})
export class HomeFiltersComponent {}
