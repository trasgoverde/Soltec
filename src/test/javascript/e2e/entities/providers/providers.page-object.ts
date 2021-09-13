import { element, by, ElementFinder, ElementArrayFinder } from 'protractor';

import { waitUntilAnyDisplayed, waitUntilDisplayed, click, waitUntilHidden, isVisible } from '../../util/utils';

import NavBarPage from './../../page-objects/navbar-page';

import ProvidersUpdatePage from './providers-update.page-object';

const expect = chai.expect;
export class ProvidersDeleteDialog {
  deleteModal = element(by.className('modal'));
  private dialogTitle: ElementFinder = element(by.id('soltecApp.providers.delete.question'));
  private confirmButton = element(by.id('jhi-confirm-delete-providers'));

  getDialogTitle() {
    return this.dialogTitle;
  }

  async clickOnConfirmButton() {
    await this.confirmButton.click();
  }
}

export default class ProvidersComponentsPage {
  createButton: ElementFinder = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('div table .btn-danger'));
  title: ElementFinder = element(by.id('providers-heading'));
  noRecords: ElementFinder = element(by.css('#app-view-container .table-responsive div.alert.alert-warning'));
  table: ElementFinder = element(by.css('#app-view-container div.table-responsive > table'));

  records: ElementArrayFinder = this.table.all(by.css('tbody tr'));

  getDetailsButton(record: ElementFinder) {
    return record.element(by.css('a.btn.btn-info.btn-sm'));
  }

  getEditButton(record: ElementFinder) {
    return record.element(by.css('a.btn.btn-primary.btn-sm'));
  }

  getDeleteButton(record: ElementFinder) {
    return record.element(by.css('a.btn.btn-danger.btn-sm'));
  }

  async goToPage(navBarPage: NavBarPage) {
    await navBarPage.getEntityPage('providers');
    await waitUntilAnyDisplayed([this.noRecords, this.table]);
    return this;
  }

  async goToCreateProviders() {
    await this.createButton.click();
    return new ProvidersUpdatePage();
  }

  async deleteProviders() {
    const deleteButton = this.getDeleteButton(this.records.last());
    await click(deleteButton);

    const providersDeleteDialog = new ProvidersDeleteDialog();
    await waitUntilDisplayed(providersDeleteDialog.deleteModal);
    expect(await providersDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/soltecApp.providers.delete.question/);
    await providersDeleteDialog.clickOnConfirmButton();

    await waitUntilHidden(providersDeleteDialog.deleteModal);

    expect(await isVisible(providersDeleteDialog.deleteModal)).to.be.false;
  }
}
