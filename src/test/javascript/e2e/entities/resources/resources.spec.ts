import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import ResourcesComponentsPage from './resources.page-object';
import ResourcesUpdatePage from './resources-update.page-object';
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

describe('Resources e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let resourcesComponentsPage: ResourcesComponentsPage;
  let resourcesUpdatePage: ResourcesUpdatePage;
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
    resourcesComponentsPage = new ResourcesComponentsPage();
    resourcesComponentsPage = await resourcesComponentsPage.goToPage(navBarPage);
  });

  it('should load Resources', async () => {
    expect(await resourcesComponentsPage.title.getText()).to.match(/Resources/);
    expect(await resourcesComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Resources', async () => {
    const beforeRecordsCount = (await isVisible(resourcesComponentsPage.noRecords))
      ? 0
      : await getRecordsCount(resourcesComponentsPage.table);
    resourcesUpdatePage = await resourcesComponentsPage.goToCreateResources();
    await resourcesUpdatePage.enterData();
    expect(await isVisible(resourcesUpdatePage.saveButton)).to.be.false;

    expect(await resourcesComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(resourcesComponentsPage.table);
    await waitUntilCount(resourcesComponentsPage.records, beforeRecordsCount + 1);
    expect(await resourcesComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await resourcesComponentsPage.deleteResources();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(resourcesComponentsPage.records, beforeRecordsCount);
      expect(await resourcesComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(resourcesComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
