import { Routes } from '@angular/router';
import { HomeComponent } from './views/home/home.component';
import { IndexComponent } from './views/index/index.component';
import { DetailsComponent } from './views/details/details.component';
import { PropertiesComponent } from './views/properties/properties.component';
import { CredentialProvidersComponent } from './views/credential-providers/credential-providers.component';

export const routes: Routes = [
  { path: '', redirectTo: '/search', pathMatch: 'full' },
  {
    path: 'search',
    component: HomeComponent,
    title: 'Search',
  },
  {
    path: 'components',
    component: IndexComponent,
    title: 'Components',
  },
  {
    path: 'properties',
    component: PropertiesComponent,
    title: 'Properties',
  },
  {
    path: 'credential-providers',
    component: CredentialProvidersComponent,
    title: 'Credential Providers',
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
