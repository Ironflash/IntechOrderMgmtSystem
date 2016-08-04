export class OrderSystemInterfacePage {
  navigateTo() {
    return browser.get('/');
  }

  getParagraphText() {
    return element(by.css('order-system-interface-app h1')).getText();
  }
}
