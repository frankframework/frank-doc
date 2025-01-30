import { Routes } from '@angular/router';
import { HomeComponent } from './views/home/home.component';
import { IndexComponent } from './views/index/index.component';
import { DetailsComponent } from './views/details/details.component';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    title: 'Home | Frank!Doc',
  },
  {
    path: 'index',
    component: IndexComponent,
    title: 'Index | Frank!Doc',
  },
  {
    path: 'All',
    redirectTo: '',
  },
  {
    path: ':fullname',
    component: DetailsComponent,
    title: 'Element details | Frank!Doc',
  },
  {
    // old url structure, only checks element short name
    path: ':label/:element',
    component: DetailsComponent,
    title: 'Element details | Frank!Doc',
  },
  {
    path: ':filter/:label/:element',
    component: DetailsComponent,
    title: 'Element details | Frank!Doc',
  },
  {
    path: '**',
    redirectTo: '',
  },
];
