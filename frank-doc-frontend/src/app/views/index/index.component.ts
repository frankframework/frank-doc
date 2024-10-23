import { Component, computed, Signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AppService, Filter } from '../../app.service';
import { ChipComponent } from '@frankframework/angular-components';

@Component({
  selector: 'app-index',
  standalone: true,
  imports: [RouterLink, ChipComponent],
  templateUrl: './index.component.html',
  styleUrl: './index.component.scss',
})
export class IndexComponent {
  protected filters: Signal<Filter[]> = computed(() => this.appService.filters());
  constructor(private appService: AppService) {}
}
