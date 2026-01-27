import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import {provideHttpClient} from '@angular/common/http';
import { provideIonicAngular } from '@ionic/angular/standalone';
import { MatDialogModule } from '@angular/material/dialog';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    provideIonicAngular({
      mode: 'ios',
      rippleEffect: true,
      animated: true
    }),
    importProvidersFrom(MatDialogModule),
  ]
};
