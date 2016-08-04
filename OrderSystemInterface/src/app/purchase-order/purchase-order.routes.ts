/**
 * Created by gregorylaflash on 7/31/16.
 */
import { PurchaseOrderComponent } from './purchase-order.component';
import { PurchaseOrderUploadComponent } from './purchase-order-upload/purchase-order-upload.component';
import { ROUTER_DIRECTIVES} from '@angular/router';
import { PurchaseOrderGuard }     from './purchase-order.guard';

export const PurchaseOrderRoutes = [
  {
    path: '/purchaseOrder',
    component: PurchaseOrderComponent,
    index: true,
    children: [
      { path: '/upload', component: PurchaseOrderUploadComponent, index: true },
      { path: '', component: PurchaseOrderComponent, index: true }
      // { path: '/:id', component: PurchaseOrderComponent, canDeactivate: [PurchaseOrderGuard] }
    ],
    directives: [ROUTER_DIRECTIVES]
  }
];
