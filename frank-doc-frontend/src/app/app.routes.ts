import { Routes } from '@angular/router';
import { HomeComponent } from './views/home/home.component';
import { IndexComponent } from './views/index/index.component';
import { DetailsComponent } from './views/details/details.component';
import { PropertiesComponent } from './views/properties/properties.component';
import { CredentialProvidersComponent } from './views/credential-providers/credential-providers.component';
import { ServletAuthenticatorsComponent } from './views/servlet-authenticators/servlet-authenticators.component';

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
    path: 'appconstants',
    component: PropertiesComponent,
    title: 'AppConstants',
  },
  {
    path: 'credential-providers',
    component: CredentialProvidersComponent,
    title: 'Credential Providers',
  },
  {
    path: 'servlet-authenticators',
    component: ServletAuthenticatorsComponent,
    title: 'Servlet Authenticators',
  },
  {
    path: 'All',
    redirectTo: '',
  },
  {
    path: ':name',
    component: DetailsComponent,
    title: 'Element details',
  },
  {
    // old url structure, only checks element short name
    path: ':label/:element',
    component: DetailsComponent,
    title: 'Element details',
  },
  {
    path: ':filter/:label/:element',
    component: DetailsComponent,
    title: 'Element details',
  },
  {
    path: '**',
    redirectTo: '',
  },
];
