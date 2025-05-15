import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

function main(): void {
  bootstrapApplication(AppComponent, appConfig).catch((error) => console.error(error));
}
main();
