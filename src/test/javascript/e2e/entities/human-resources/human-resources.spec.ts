import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import HumanResourcesComponentsPage from './human-resources.page-object';
import HumanResourcesUpdatePage from './human-resources-update.page-object';
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

describe('HumanResources e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let humanResourcesComponentsPage: HumanResourcesComponentsPage;
  let humanResourcesUpdatePage: HumanResourcesUpdatePage;
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
    humanResourcesComponentsPage = new HumanResourcesComponentsPage();
    humanResourcesComponentsPage = await humanResourcesComponentsPage.goToPage(navBarPage);
  });

  it('should load HumanResources', async () => {
    expect(await humanResourcesComponentsPage.title.getText()).to.match(/Human Resources/);
    expect(await humanResourcesComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete HumanResources', async () => {
    const beforeRecordsCount = (await isVisible(humanResourcesComponentsPage.noRecords))
      ? 0
      : await getRecordsCount(humanResourcesComponentsPage.table);
    humanResourcesUpdatePage = await humanResourcesComponentsPage.goToCreateHumanResources();
    await humanResourcesUpdatePage.enterData();
    expect(await isVisible(humanResourcesUpdatePage.saveButton)).to.be.false;

    expect(await humanResourcesComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(humanResourcesComponentsPage.table);
    await waitUntilCount(humanResourcesComponentsPage.records, beforeRecordsCount + 1);
    expect(await humanResourcesComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await humanResourcesComponentsPage.deleteHumanResources();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(humanResourcesComponentsPage.records, beforeRecordsCount);
      expect(await humanResourcesComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(humanResourcesComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
