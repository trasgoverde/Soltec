import { element, by, ElementFinder } from 'protractor';
import { waitUntilDisplayed, waitUntilHidden, isVisible } from '../../util/utils';

const expect = chai.expect;

export default class LogisticsUpdatePage {
  pageTitle: ElementFinder = element(by.id('soltecApp.logistics.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  co2EmitionsInput: ElementFinder = element(by.css('input#logistics-co2Emitions'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setCo2EmitionsInput(co2Emitions) {
    await this.co2EmitionsInput.sendKeys(co2Emitions);
  }

  async getCo2EmitionsInput() {
    return this.co2EmitionsInput.getAttribute('value');
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
    await this.setCo2EmitionsInput('5');
    await this.save();
    await waitUntilHidden(this.saveButton);
  }
}
