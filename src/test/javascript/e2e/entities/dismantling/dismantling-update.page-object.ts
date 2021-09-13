import { element, by, ElementFinder } from 'protractor';
import { waitUntilDisplayed, waitUntilHidden, isVisible } from '../../util/utils';

const expect = chai.expect;

export default class DismantlingUpdatePage {
  pageTitle: ElementFinder = element(by.id('soltecApp.dismantling.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  guaranteeDismantlingInput: ElementFinder = element(by.css('input#dismantling-guaranteeDismantling'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setGuaranteeDismantlingInput(guaranteeDismantling) {
    await this.guaranteeDismantlingInput.sendKeys(guaranteeDismantling);
  }

  async getGuaranteeDismantlingInput() {
    return this.guaranteeDismantlingInput.getAttribute('value');
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
    await this.setGuaranteeDismantlingInput('5');
    await this.save();
    await waitUntilHidden(this.saveButton);
  }
}
