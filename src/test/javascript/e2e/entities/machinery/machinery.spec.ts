import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import MachineryComponentsPage from './machinery.page-object';
import MachineryUpdatePage from './machinery-update.page-object';
import {
  waitUntilDisplayed,
  waitUntilAnyDisplayed,
  click,
  getRecordsCount,
  waitUntilHidden,
  waitUntilCount,
  isVisible,
} from '../../util/utils';

const expect = chai.expect;

describe('Machinery e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let machineryComponentsPage: MachineryComponentsPage;
  let machineryUpdatePage: MachineryUpdatePage;
  const username = process.env.E2E_USERNAME ?? 'admin';
  const password = process.env.E2E_PASSWORD ?? 'admin';

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();
    await signInPage.username.sendKeys(username);
    await signInPage.password.sendKeys(password);
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();
    await waitUntilDisplayed(navBarPage.entityMenu);
    await waitUntilDisplayed(navBarPage.adminMenu);
    await waitUntilDisplayed(navBarPage.accountMenu);
  });

  beforeEach(async () => {
    await browser.get('/');
    await waitUntilDisplayed(navBarPage.entityMenu);
    machineryComponentsPage = new MachineryComponentsPage();
    machineryComponentsPage = await machineryComponentsPage.goToPage(navBarPage);
  });

  it('should load Machinery', async () => {
    expect(await machineryComponentsPage.title.getText()).to.match(/Machinery/);
    expect(await machineryComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Machinery', async () => {
    const beforeRecordsCount = (await isVisible(machineryComponentsPage.noRecords))
      ? 0
      : await getRecordsCount(machineryComponentsPage.table);
    machineryUpdatePage = await machineryComponentsPage.goToCreateMachinery();
    await machineryUpdatePage.enterData();
    expect(await isVisible(machineryUpdatePage.saveButton)).to.be.false;

    expect(await machineryComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(machineryComponentsPage.table);
    await waitUntilCount(machineryComponentsPage.records, beforeRecordsCount + 1);
    expect(await machineryComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await machineryComponentsPage.deleteMachinery();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(machineryComponentsPage.records, beforeRecordsCount);
      expect(await machineryComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(machineryComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
