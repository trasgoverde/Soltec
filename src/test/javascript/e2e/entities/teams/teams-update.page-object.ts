import { element, by, ElementFinder } from 'protractor';
import { waitUntilDisplayed, waitUntilHidden, isVisible } from '../../util/utils';

const expect = chai.expect;

export default class TeamsUpdatePage {
  pageTitle: ElementFinder = element(by.id('soltecApp.teams.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  originMaterialsInput: ElementFinder = element(by.css('input#teams-originMaterials'));
  originStealInput: ElementFinder = element(by.css('input#teams-originSteal'));
  originAluminiumInput: ElementFinder = element(by.css('input#teams-originAluminium'));
  sustainableProvidersInput: ElementFinder = element(by.css('input#teams-sustainableProviders'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setOriginMaterialsInput(originMaterials) {
    await this.originMaterialsInput.sendKeys(originMaterials);
  }

  async getOriginMaterialsInput() {
    return this.originMaterialsInput.getAttribute('value');
  }

  async setOriginStealInput(originSteal) {
    await this.originStealInput.sendKeys(originSteal);
  }

  async getOriginStealInput() {
    return this.originStealInput.getAttribute('value');
  }

  async setOriginAluminiumInput(originAluminium) {
    await this.originAluminiumInput.sendKeys(originAluminium);
  }

  async getOriginAluminiumInput() {
    return this.originAluminiumInput.getAttribute('value');
  }

  getSustainableProvidersInput() {
    return this.sustainableProvidersInput;
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
    await this.setOriginMaterialsInput('5');
    await waitUntilDisplayed(this.saveButton);
    await this.setOriginStealInput('5');
    await waitUntilDisplayed(this.saveButton);
    await this.setOriginAluminiumInput('5');
    await waitUntilDisplayed(this.saveButton);
    const selectedSustainableProviders = await this.getSustainableProvidersInput().isSelected();
    if (selectedSustainableProviders) {
      await this.getSustainableProvidersInput().click();
    } else {
      await this.getSustainableProvidersInput().click();
    }
    await this.save();
    await waitUntilHidden(this.saveButton);
  }
}
