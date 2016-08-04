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
import { PurchaseOrderComponent } from './purchase-order.component';

describe('Component: PurchaseOrder', () => {
  let builder: TestComponentBuilder;

  beforeEachProviders(() => [PurchaseOrderComponent]);
  beforeEach(inject([TestComponentBuilder], function (tcb: TestComponentBuilder) {
    builder = tcb;
  }));

  it('should inject the component', inject([PurchaseOrderComponent],
      (component: PurchaseOrderComponent) => {
    expect(component).toBeTruthy();
  }));

  it('should create the component', inject([], () => {
    return builder.createAsync(PurchaseOrderComponentTestController)
      .then((fixture: ComponentFixture<any>) => {
        let query = fixture.debugElement.query(By.directive(PurchaseOrderComponent));
        expect(query).toBeTruthy();
        expect(query.componentInstance).toBeTruthy();
      });
  }));
});

@Component({
  selector: 'test',
  template: `
    <app-purchase-order></app-purchase-order>
  `,
  directives: [PurchaseOrderComponent]
})
class PurchaseOrderComponentTestController {
}

