import { OrderSystemInterfacePage } from './app.po';

describe('order-system-interface App', function() {
  let page: OrderSystemInterfacePage;

  beforeEach(() => {
    page = new OrderSystemInterfacePage();
  })

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('order-system-interface works!');
  });
});
