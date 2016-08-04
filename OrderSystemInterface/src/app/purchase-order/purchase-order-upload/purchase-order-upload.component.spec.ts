import {
  beforeEach,
  beforeEachProviders,
  describe,
  expect,
  it,
  inject,
} from '@angular/core/testing';
import { ComponentFixture, TestComponentBuilder } from '@angular/compiler/testing';
import { Component } from '@angular/core';
import { By } from '@angular/platform-browser';
import { PurchaseOrderUploadComponent } from './purchase-order-upload.component';

describe('Component: PurchaseOrderUpload', () => {
  let builder: TestComponentBuilder;

  beforeEachProviders(() => [PurchaseOrderUploadComponent]);
  beforeEach(inject([TestComponentBuilder], function (tcb: TestComponentBuilder) {
    builder = tcb;
  }));

  it('should inject the component', inject([PurchaseOrderUploadComponent],
      (component: PurchaseOrderUploadComponent) => {
    expect(component).toBeTruthy();
  }));

  it('should create the component', inject([], () => {
    return builder.createAsync(PurchaseOrderUploadComponentTestController)
      .then((fixture: ComponentFixture<any>) => {
        let query = fixture.debugElement.query(By.directive(PurchaseOrderUploadComponent));
        expect(query).toBeTruthy();
        expect(query.componentInstance).toBeTruthy();
      });
  }));
});

@Component({
  selector: 'test',
  template: `
    <app-purchase-order-upload></app-purchase-order-upload>
  `,
  directives: [PurchaseOrderUploadComponent]
})
class PurchaseOrderUploadComponentTestController {
}

