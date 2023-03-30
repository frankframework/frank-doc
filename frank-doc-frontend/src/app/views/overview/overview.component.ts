import { ChangeDetectionStrategy, Component, Directive, HostBinding, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, Subscription } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { Group } from 'src/app/frankdoc.types';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss']
})
export class OverviewComponent implements OnInit, OnDestroy {
  @HostBinding('class') class = 'element'

  private subscriptions?: Subscription;
  version = "";

  constructor(private appService: AppService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.subscriptions = combineLatest(
      [this.appService.frankDoc$, this.route.paramMap]
    ).subscribe(([state, paramMap]) => {
      this.version = state.version || "";
      const groups = state.groups,
        stateGroup = state.group,
        groupParam = paramMap.get('group');

      if (groupParam && groups.length > 0) {
        const group = groups.find((group: Group) => group.name === groupParam);
        if (group && groupParam !== stateGroup?.name)
          this.appService.setGroupAndElement(group);
      }
    });
  }

  ngOnDestroy() {
    this.subscriptions?.unsubscribe();
  }
}
