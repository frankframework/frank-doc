import { Routes } from '@angular/router';
import { HomeComponent } from './views/home/home.component';
import { IndexComponent } from './views/index/index.component';
import { DetailsComponent } from './views/details/details.component';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    title: 'Home',
  },
  {
    path: 'index',
    component: IndexComponent,
    title: 'Index',
  },
  {
    path: ':filter/:label/:element',
    component: DetailsComponent,
  },
  {
    path: '**',
    redirectTo: '',
  },
];
