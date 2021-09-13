import { element, by, ElementFinder, ElementArrayFinder } from 'protractor';

import { waitUntilAnyDisplayed, waitUntilDisplayed, click, waitUntilHidden, isVisible } from '../../util/utils';

import NavBarPage from './../../page-objects/navbar-page';

import HumanResourcesUpdatePage from './human-resources-update.page-object';

const expect = chai.expect;
export class HumanResourcesDeleteDialog {
  deleteModal = element(by.className('modal'));
  private dialogTitle: ElementFinder = element(by.id('soltecApp.humanResources.delete.question'));
  private confirmButton = element(by.id('jhi-confirm-delete-humanResources'));

  getDialogTitle() {
    return this.dialogTitle;
  }

  async clickOnConfirmButton() {
    await this.confirmButton.click();
  }
}

export default class HumanResourcesComponentsPage {
  createButton: ElementFinder = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('div table .btn-danger'));
  title: ElementFinder = element(by.id('human-resources-heading'));
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
    await navBarPage.getEntityPage('human-resources');
    await waitUntilAnyDisplayed([this.noRecords, this.table]);
    return this;
  }

  async goToCreateHumanResources() {
    await this.createButton.click();
    return new HumanResourcesUpdatePage();
  }

  async deleteHumanResources() {
    const deleteButton = this.getDeleteButton(this.records.last());
    await click(deleteButton);

    const humanResourcesDeleteDialog = new HumanResourcesDeleteDialog();
    await waitUntilDisplayed(humanResourcesDeleteDialog.deleteModal);
    expect(await humanResourcesDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/soltecApp.humanResources.delete.question/);
    await humanResourcesDeleteDialog.clickOnConfirmButton();

    await waitUntilHidden(humanResourcesDeleteDialog.deleteModal);

    expect(await isVisible(humanResourcesDeleteDialog.deleteModal)).to.be.false;
  }
}
