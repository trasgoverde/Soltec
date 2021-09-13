import { element, by, ElementFinder } from 'protractor';
import { waitUntilDisplayed, waitUntilHidden, isVisible } from '../../util/utils';

const expect = chai.expect;

export default class ProvidersUpdatePage {
  pageTitle: ElementFinder = element(by.id('soltecApp.providers.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  agreementParisInput: ElementFinder = element(by.css('input#providers-agreementParis'));
  certifiedSustianableInput: ElementFinder = element(by.css('input#providers-certifiedSustianable'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setAgreementParisInput(agreementParis) {
    await this.agreementParisInput.sendKeys(agreementParis);
  }

  async getAgreementParisInput() {
    return this.agreementParisInput.getAttribute('value');
  }

  getCertifiedSustianableInput() {
    return this.certifiedSustianableInput;
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
    await this.setAgreementParisInput('5');
    await waitUntilDisplayed(this.saveButton);
    const selectedCertifiedSustianable = await this.getCertifiedSustianableInput().isSelected();
    if (selectedCertifiedSustianable) {
      await this.getCertifiedSustianableInput().click();
    } else {
      await this.getCertifiedSustianableInput().click();
    }
    await this.save();
    await waitUntilHidden(this.saveButton);
  }
}
