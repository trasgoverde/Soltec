import { element, by, ElementFinder, ElementArrayFinder } from 'protractor';

import { waitUntilAnyDisplayed, waitUntilDisplayed, click, waitUntilHidden, isVisible } from '../../util/utils';

import NavBarPage from './../../page-objects/navbar-page';

import LogisticsUpdatePage from './logistics-update.page-object';

const expect = chai.expect;
export class LogisticsDeleteDialog {
  deleteModal = element(by.className('modal'));
  private dialogTitle: ElementFinder = element(by.id('soltecApp.logistics.delete.question'));
  private confirmButton = element(by.id('jhi-confirm-delete-logistics'));

  getDialogTitle() {
    return this.dialogTitle;
  }

  async clickOnConfirmButton() {
    await this.confirmButton.click();
  }
}

export default class LogisticsComponentsPage {
  createButton: ElementFinder = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('div table .btn-danger'));
  title: ElementFinder = element(by.id('logistics-heading'));
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
    await navBarPage.getEntityPage('logistics');
    await waitUntilAnyDisplayed([this.noRecords, this.table]);
    return this;
  }

  async goToCreateLogistics() {
    await this.createButton.click();
    return new LogisticsUpdatePage();
  }

  async deleteLogistics() {
    const deleteButton = this.getDeleteButton(this.records.last());
    await click(deleteButton);

    const logisticsDeleteDialog = new LogisticsDeleteDialog();
    await waitUntilDisplayed(logisticsDeleteDialog.deleteModal);
    expect(await logisticsDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/soltecApp.logistics.delete.question/);
    await logisticsDeleteDialog.clickOnConfirmButton();

    await waitUntilHidden(logisticsDeleteDialog.deleteModal);

    expect(await isVisible(logisticsDeleteDialog.deleteModal)).to.be.false;
  }
}
