import { element, by, ElementFinder, ElementArrayFinder } from 'protractor';

import { waitUntilAnyDisplayed, waitUntilDisplayed, click, waitUntilHidden, isVisible } from '../../util/utils';

import NavBarPage from './../../page-objects/navbar-page';

import TeamsUpdatePage from './teams-update.page-object';

const expect = chai.expect;
export class TeamsDeleteDialog {
  deleteModal = element(by.className('modal'));
  private dialogTitle: ElementFinder = element(by.id('soltecApp.teams.delete.question'));
  private confirmButton = element(by.id('jhi-confirm-delete-teams'));

  getDialogTitle() {
    return this.dialogTitle;
  }

  async clickOnConfirmButton() {
    await this.confirmButton.click();
  }
}

export default class TeamsComponentsPage {
  createButton: ElementFinder = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('div table .btn-danger'));
  title: ElementFinder = element(by.id('teams-heading'));
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
    await navBarPage.getEntityPage('teams');
    await waitUntilAnyDisplayed([this.noRecords, this.table]);
    return this;
  }

  async goToCreateTeams() {
    await this.createButton.click();
    return new TeamsUpdatePage();
  }

  async deleteTeams() {
    const deleteButton = this.getDeleteButton(this.records.last());
    await click(deleteButton);

    const teamsDeleteDialog = new TeamsDeleteDialog();
    await waitUntilDisplayed(teamsDeleteDialog.deleteModal);
    expect(await teamsDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/soltecApp.teams.delete.question/);
    await teamsDeleteDialog.clickOnConfirmButton();

    await waitUntilHidden(teamsDeleteDialog.deleteModal);

    expect(await isVisible(teamsDeleteDialog.deleteModal)).to.be.false;
  }
}
