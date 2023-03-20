import { ChangeDetectionStrategy, Component, Directive, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, Subscription } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { Group } from 'src/app/frankdoc.types';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss'],
  host: { class: 'element' }
  // changeDetection: ChangeDetectionStrategy.OnPush
})
export class OverviewComponent implements OnInit, OnDestroy {

  private subs?: Subscription;
  version = "";

  constructor(private appService: AppService, private route: ActivatedRoute) { }

  ngOnInit() {
    this.subs = combineLatest(
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
    this.subs?.unsubscribe();
  }
}