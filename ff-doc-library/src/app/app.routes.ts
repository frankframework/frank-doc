import { Routes } from '@angular/router';
import { IndexComponent } from './index/index.component';
import { AngularImplementationComponent } from './angular-implementation/angular-implementation.component';
import { ReactImplementationComponent } from './react-implementation/react-implementation.component';

export const routes: Routes = [
  { path: '', component: IndexComponent },
  { path: 'angular', component: AngularImplementationComponent },
  { path: 'react', component: ReactImplementationComponent },
];
