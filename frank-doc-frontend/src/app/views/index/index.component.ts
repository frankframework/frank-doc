import { Component, OnInit } from '@angular/core';
import { AppService, Filter } from '../../app.service';

@Component({
  selector: 'app-index',
  standalone: true,
  imports: [],
  templateUrl: './index.component.html',
  styleUrl: './index.component.scss',
})
export class IndexComponent implements OnInit {
  protected filters: Filter[] = this.appService.filters();
  constructor(private appService: AppService) {}

  ngOnInit(): void {
    setTimeout(() => {
      console.log(this.filters);
    });
  }
}
