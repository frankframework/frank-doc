import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

function main(): void {
  bootstrapApplication(AppComponent, appConfig).catch((error) => console.error(error));
}

main();
