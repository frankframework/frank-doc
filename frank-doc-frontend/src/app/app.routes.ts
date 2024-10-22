import { Routes } from '@angular/router';
import { HomeComponent } from './views/home/home.component';
import { IndexComponent } from './views/index/index.component';

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
  /*{
    path: 'element',
    component: ElemementComponent,
  },*/
  {
    path: '**',
    redirectTo: '',
  },
];
