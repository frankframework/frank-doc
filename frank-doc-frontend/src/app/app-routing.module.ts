import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ElementComponent } from './views/element/element.component';
import { OverviewComponent } from './views/overview/overview.component';

const routes: Routes = [
  { path: ':group', component: OverviewComponent, pathMatch: 'full' },
  { path: ':group/:element', component: ElementComponent, pathMatch: 'full' },
  { path: '**', redirectTo: '/All' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
