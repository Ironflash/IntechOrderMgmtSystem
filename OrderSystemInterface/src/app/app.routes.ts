/**
 * Created by gregorylaflash on 7/31/16.
 */
import { provideRouter, RouterConfig } from '@angular/router';

import { PurchaseOrderRoutes } from './purchase-order/purchase-order.routes';
import { PurchaseOrderGuard }  from './purchase-order/purchase-order.guard';

const routes: RouterConfig = [
  ...PurchaseOrderRoutes
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes)
];
