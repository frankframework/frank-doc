import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { OverviewComponent } from './views/overview/overview.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { SidebarElementsComponent } from './components/sidebar-elements/sidebar-elements.component';

@NgModule({
  declarations: [
    AppComponent,
    OverviewComponent,
    SidebarComponent,
    SidebarElementsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
