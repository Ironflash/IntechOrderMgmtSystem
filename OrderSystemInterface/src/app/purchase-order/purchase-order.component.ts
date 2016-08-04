import { Component, OnInit } from '@angular/core';
import { ROUTER_DIRECTIVES} from '@angular/router';


@Component({
  moduleId: module.id,
  selector: 'app-purchase-order',
  templateUrl: 'purchase-order.component.html',
  styleUrls: ['purchase-order.component.css'],
  directives: [ROUTER_DIRECTIVES]
})
export class PurchaseOrderComponent implements OnInit {

  constructor() {}

  ngOnInit() {
    console.log("In purchase order ctrl");
  }

}
