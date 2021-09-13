import { element, by, ElementFinder } from 'protractor';
import { waitUntilDisplayed, waitUntilHidden, isVisible } from '../../util/utils';

const expect = chai.expect;

export default class MachineryUpdatePage {
  pageTitle: ElementFinder = element(by.id('soltecApp.machinery.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  paymentCycleInput: ElementFinder = element(by.css('input#machinery-paymentCycle'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setPaymentCycleInput(paymentCycle) {
    await this.paymentCycleInput.sendKeys(paymentCycle);
  }

  async getPaymentCycleInput() {
    return this.paymentCycleInput.getAttribute('value');
  }

  async save() {
    await this.saveButton.click();
  }

  async cancel() {
    await this.cancelButton.click();
  }

  getSaveButton() {
    return this.saveButton;
  }

  async enterData() {
    await waitUntilDisplayed(this.saveButton);
    await this.setPaymentCycleInput('5');
    await this.save();
    await waitUntilHidden(this.saveButton);
  }
}
