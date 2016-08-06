import { Component, OnInit } from '@angular/core';
import { ROUTER_DIRECTIVES} from '@angular/router';
// import { PdfViewerComponent } from 'ng2-pdf-viewer';


@Component({
  moduleId: module.id,
  selector: 'app-purchase-order',
  templateUrl: 'purchase-order.component.html',
  styleUrls: ['purchase-order.component.css'],
  directives: [ROUTER_DIRECTIVES]
})
export class PurchaseOrderComponent implements OnInit {

  pdfSrc: string = 'fileUploads/2016/08/05/testCustomer/testCustomer184903.pdf';
  page: number = 1;

  constructor() {}

  ngOnInit() {
    console.log("In purchase order ctrl");
  }

  public viewPDF():void{
    window.open("http://localhost:9000/api/purchaseOrder/v1/1bd145c7-63d1-4aff-96aa-44dd0942ff85/download");
  }
}
