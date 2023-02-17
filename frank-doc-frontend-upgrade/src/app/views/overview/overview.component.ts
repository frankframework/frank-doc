import { ChangeDetectionStrategy, Component, Directive, OnInit } from '@angular/core';
import { AppService } from 'src/app/app.service';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss'],
  // changeDetection: ChangeDetectionStrategy.OnPush
})
export class OverviewComponent implements OnInit {

  version = "";

  constructor(private frankdoc: AppService) { }

  ngOnInit() {
    this.frankdoc.appState$.subscribe(state => {
      this.version = state.version || "";
    });
  }
}
