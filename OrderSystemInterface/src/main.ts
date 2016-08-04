import { bootstrap } from '@angular/platform-browser-dynamic';
import { enableProdMode } from '@angular/core';
import { APP_ROUTER_PROVIDERS } from './app/app.routes';
import { OrderSystemInterfaceAppComponent, environment } from './app/';

if (environment.production) {
  enableProdMode();
}

bootstrap(OrderSystemInterfaceAppComponent,APP_ROUTER_PROVIDERS);
