import { Component, OnInit } from '@angular/core';
import { ROUTER_DIRECTIVES} from '@angular/router';
import {FILE_UPLOAD_DIRECTIVES, FileUploader} from 'ng2-file-upload/ng2-file-upload';


@Component({
  moduleId: module.id,
  selector: 'app-purchase-order-upload',
  templateUrl: 'purchase-order-upload.component.html',
  styleUrls: ['purchase-order-upload.component.css'],
  directives: [ROUTER_DIRECTIVES, FILE_UPLOAD_DIRECTIVES]
})
export class PurchaseOrderUploadComponent implements OnInit {

  constructor() {}

  ngOnInit() {
    console.log("In purchase order upload ctrl");
  }

  private URL:string = 'http://localhost:9000/purchaseOrder/create?customerOrderID=customerOrderID1&customer=testCustomer';
  public uploader:FileUploader = new FileUploader(
    {
      url: this.URL,
      queueLimit: 1
    });
  public hasBaseDropZoneOver:boolean = false;

  public fileOverBase(e:any):void {
    this.hasBaseDropZoneOver = e;
  }

  public upload():void {
    this.uploader.queue[0].withCredentials = false;
    this.uploader.queue[0].upload();
  }

}
