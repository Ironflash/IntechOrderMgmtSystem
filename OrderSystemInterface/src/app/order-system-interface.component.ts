import { Component } from '@angular/core';
import { ROUTER_DIRECTIVES} from '@angular/router';
import { PurchaseOrderComponent } from './purchase-order';


@Component({
  moduleId: module.id,
  selector: 'order-system-interface-app',
  templateUrl: 'order-system-interface.component.html',
  styleUrls: ['order-system-interface.component.css'],
  directives: [ROUTER_DIRECTIVES]
})
export class OrderSystemInterfaceAppComponent {
  title = 'order-system-interface works!';
}
