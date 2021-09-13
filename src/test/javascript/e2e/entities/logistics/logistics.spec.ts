import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import LogisticsComponentsPage from './logistics.page-object';
import LogisticsUpdatePage from './logistics-update.page-object';
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

describe('Logistics e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let logisticsComponentsPage: LogisticsComponentsPage;
  let logisticsUpdatePage: LogisticsUpdatePage;
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
    logisticsComponentsPage = new LogisticsComponentsPage();
    logisticsComponentsPage = await logisticsComponentsPage.goToPage(navBarPage);
  });

  it('should load Logistics', async () => {
    expect(await logisticsComponentsPage.title.getText()).to.match(/Logistics/);
    expect(await logisticsComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Logistics', async () => {
    const beforeRecordsCount = (await isVisible(logisticsComponentsPage.noRecords))
      ? 0
      : await getRecordsCount(logisticsComponentsPage.table);
    logisticsUpdatePage = await logisticsComponentsPage.goToCreateLogistics();
    await logisticsUpdatePage.enterData();
    expect(await isVisible(logisticsUpdatePage.saveButton)).to.be.false;

    expect(await logisticsComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(logisticsComponentsPage.table);
    await waitUntilCount(logisticsComponentsPage.records, beforeRecordsCount + 1);
    expect(await logisticsComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await logisticsComponentsPage.deleteLogistics();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(logisticsComponentsPage.records, beforeRecordsCount);
      expect(await logisticsComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(logisticsComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
