import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { OverviewComponent } from './views/overview/overview.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { SidebarElementsComponent } from './components/sidebar-elements/sidebar-elements.component';
import { ElementComponent } from './views/element/element.component';
import { ElementChildrenComponent } from './views/element-children/element-children.component';
import { ElementParametersComponent } from './views/element-parameters/element-parameters.component';
import { ElementForwardsComponent } from './views/element-forwards/element-forwards.component';
import { ElementAttributesComponent } from './views/element-attributes/element-attributes.component';
import { AttributeDescriptionComponent } from './views/attribute-description/attribute-description.component';
import { AsTextPipe } from './pipes/as-text.pipe';
import { JavadocPipe } from './pipes/javadoc.pipe';

@NgModule({
  declarations: [
    AppComponent,
    OverviewComponent,
    SidebarComponent,
    SidebarElementsComponent,
    ElementComponent,
    ElementChildrenComponent,
    ElementParametersComponent,
    ElementForwardsComponent,
    ElementAttributesComponent,
    AttributeDescriptionComponent,
    AsTextPipe,
    JavadocPipe
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
