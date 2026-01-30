import { ApplicationConfig, Provider, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, TitleStrategy, withHashLocation, withInMemoryScrolling } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http';
import { ViewTitleStrategy } from './view-title-strategy';

const provideTitleStrategy: Provider = { provide: TitleStrategy, useClass: ViewTitleStrategy };

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection(),
    provideRouter(routes, withHashLocation(), withInMemoryScrolling({ scrollPositionRestoration: 'enabled' })),
    provideHttpClient(),
    provideTitleStrategy,
  ],
};
