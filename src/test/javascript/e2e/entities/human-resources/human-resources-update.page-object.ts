import { element, by, ElementFinder } from 'protractor';
import { waitUntilDisplayed, waitUntilHidden, isVisible } from '../../util/utils';

const expect = chai.expect;

export default class HumanResourcesUpdatePage {
  pageTitle: ElementFinder = element(by.id('soltecApp.humanResources.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  investmentsLocallyInput: ElementFinder = element(by.css('input#human-resources-investmentsLocally'));
  laborAccidentsindexInput: ElementFinder = element(by.css('input#human-resources-laborAccidentsindex'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setInvestmentsLocallyInput(investmentsLocally) {
    await this.investmentsLocallyInput.sendKeys(investmentsLocally);
  }

  async getInvestmentsLocallyInput() {
    return this.investmentsLocallyInput.getAttribute('value');
  }

  async setLaborAccidentsindexInput(laborAccidentsindex) {
    await this.laborAccidentsindexInput.sendKeys(laborAccidentsindex);
  }

  async getLaborAccidentsindexInput() {
    return this.laborAccidentsindexInput.getAttribute('value');
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
    await this.setInvestmentsLocallyInput('5');
    await waitUntilDisplayed(this.saveButton);
    await this.setLaborAccidentsindexInput('5');
    await this.save();
    await waitUntilHidden(this.saveButton);
  }
}
