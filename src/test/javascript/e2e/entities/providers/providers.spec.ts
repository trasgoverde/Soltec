import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import ProvidersComponentsPage from './providers.page-object';
import ProvidersUpdatePage from './providers-update.page-object';
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

describe('Providers e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let providersComponentsPage: ProvidersComponentsPage;
  let providersUpdatePage: ProvidersUpdatePage;
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
    providersComponentsPage = new ProvidersComponentsPage();
    providersComponentsPage = await providersComponentsPage.goToPage(navBarPage);
  });

  it('should load Providers', async () => {
    expect(await providersComponentsPage.title.getText()).to.match(/Providers/);
    expect(await providersComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Providers', async () => {
    const beforeRecordsCount = (await isVisible(providersComponentsPage.noRecords))
      ? 0
      : await getRecordsCount(providersComponentsPage.table);
    providersUpdatePage = await providersComponentsPage.goToCreateProviders();
    await providersUpdatePage.enterData();
    expect(await isVisible(providersUpdatePage.saveButton)).to.be.false;

    expect(await providersComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(providersComponentsPage.table);
    await waitUntilCount(providersComponentsPage.records, beforeRecordsCount + 1);
    expect(await providersComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await providersComponentsPage.deleteProviders();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(providersComponentsPage.records, beforeRecordsCount);
      expect(await providersComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(providersComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
