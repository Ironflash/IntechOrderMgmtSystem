import {
  beforeEachProviders,
  describe,
  expect,
  it,
  inject
} from '@angular/core/testing';
import { OrderSystemInterfaceAppComponent } from '../app/order-system-interface.component';

beforeEachProviders(() => [OrderSystemInterfaceAppComponent]);

describe('App: OrderSystemInterface', () => {
  it('should create the app',
      inject([OrderSystemInterfaceAppComponent], (app: OrderSystemInterfaceAppComponent) => {
    expect(app).toBeTruthy();
  }));

  it('should have as title \'order-system-interface works!\'',
      inject([OrderSystemInterfaceAppComponent], (app: OrderSystemInterfaceAppComponent) => {
    expect(app.title).toEqual('order-system-interface works!');
  }));
});
