import { RouterStateSnapshot, TitleStrategy } from '@angular/router';
import { inject, Injectable } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class ViewTitleStrategy extends TitleStrategy {
  private readonly title: Title = inject(Title);

  updateTitle(snapshot: RouterStateSnapshot): void {
    const title = this.buildTitle(snapshot);
    this.title.setTitle(`${title} | ${environment.applicationName}`);
  }
}
