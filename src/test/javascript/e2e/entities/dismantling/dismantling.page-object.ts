import { element, by, ElementFinder, ElementArrayFinder } from 'protractor';

import { waitUntilAnyDisplayed, waitUntilDisplayed, click, waitUntilHidden, isVisible } from '../../util/utils';

import NavBarPage from './../../page-objects/navbar-page';

import DismantlingUpdatePage from './dismantling-update.page-object';

const expect = chai.expect;
export class DismantlingDeleteDialog {
  deleteModal = element(by.className('modal'));
  private dialogTitle: ElementFinder = element(by.id('soltecApp.dismantling.delete.question'));
  private confirmButton = element(by.id('jhi-confirm-delete-dismantling'));

  getDialogTitle() {
    return this.dialogTitle;
  }

  async clickOnConfirmButton() {
    await this.confirmButton.click();
  }
}

export default class DismantlingComponentsPage {
  createButton: ElementFinder = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('div table .btn-danger'));
  title: ElementFinder = element(by.id('dismantling-heading'));
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
    await navBarPage.getEntityPage('dismantling');
    await waitUntilAnyDisplayed([this.noRecords, this.table]);
    return this;
  }

  async goToCreateDismantling() {
    await this.createButton.click();
    return new DismantlingUpdatePage();
  }

  async deleteDismantling() {
    const deleteButton = this.getDeleteButton(this.records.last());
    await click(deleteButton);

    const dismantlingDeleteDialog = new DismantlingDeleteDialog();
    await waitUntilDisplayed(dismantlingDeleteDialog.deleteModal);
    expect(await dismantlingDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/soltecApp.dismantling.delete.question/);
    await dismantlingDeleteDialog.clickOnConfirmButton();

    await waitUntilHidden(dismantlingDeleteDialog.deleteModal);

    expect(await isVisible(dismantlingDeleteDialog.deleteModal)).to.be.false;
  }
}
