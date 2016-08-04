/**
 * Created by gregorylaflash on 7/31/16.
 */
import { Injectable }    from '@angular/core';
import { CanDeactivate } from '@angular/router';
import { Observable }    from 'rxjs/Observable';

import { PurchaseOrderComponent } from './purchase-order.component';

@Injectable()
export class PurchaseOrderGuard implements CanDeactivate<PurchaseOrderComponent> {

  canDeactivate(component: PurchaseOrderComponent): Observable<boolean> | boolean {
    return false;
  }
}
