import { Component } from '@angular/core';
import { SearchComponent } from '@frankframework/angular-components';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [SearchComponent, FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  protected searchQuery = '';
}
