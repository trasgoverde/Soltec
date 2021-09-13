import { element, by, ElementFinder } from 'protractor';
import { waitUntilDisplayed, waitUntilHidden, isVisible } from '../../util/utils';

const expect = chai.expect;

export default class ResourcesUpdatePage {
  pageTitle: ElementFinder = element(by.id('soltecApp.resources.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  waterConsumtionInput: ElementFinder = element(by.css('input#resources-waterConsumtion'));
  reforestryIndexInput: ElementFinder = element(by.css('input#resources-reforestryIndex'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setWaterConsumtionInput(waterConsumtion) {
    await this.waterConsumtionInput.sendKeys(waterConsumtion);
  }

  async getWaterConsumtionInput() {
    return this.waterConsumtionInput.getAttribute('value');
  }

  async setReforestryIndexInput(reforestryIndex) {
    await this.reforestryIndexInput.sendKeys(reforestryIndex);
  }

  async getReforestryIndexInput() {
    return this.reforestryIndexInput.getAttribute('value');
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
    await this.setWaterConsumtionInput('5');
    await waitUntilDisplayed(this.saveButton);
    await this.setReforestryIndexInput('5');
    await this.save();
    await waitUntilHidden(this.saveButton);
  }
}
