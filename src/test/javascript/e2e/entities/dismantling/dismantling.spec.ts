import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import DismantlingComponentsPage from './dismantling.page-object';
import DismantlingUpdatePage from './dismantling-update.page-object';
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

describe('Dismantling e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let dismantlingComponentsPage: DismantlingComponentsPage;
  let dismantlingUpdatePage: DismantlingUpdatePage;
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
    dismantlingComponentsPage = new DismantlingComponentsPage();
    dismantlingComponentsPage = await dismantlingComponentsPage.goToPage(navBarPage);
  });

  it('should load Dismantlings', async () => {
    expect(await dismantlingComponentsPage.title.getText()).to.match(/Dismantlings/);
    expect(await dismantlingComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Dismantlings', async () => {
    const beforeRecordsCount = (await isVisible(dismantlingComponentsPage.noRecords))
      ? 0
      : await getRecordsCount(dismantlingComponentsPage.table);
    dismantlingUpdatePage = await dismantlingComponentsPage.goToCreateDismantling();
    await dismantlingUpdatePage.enterData();
    expect(await isVisible(dismantlingUpdatePage.saveButton)).to.be.false;

    expect(await dismantlingComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(dismantlingComponentsPage.table);
    await waitUntilCount(dismantlingComponentsPage.records, beforeRecordsCount + 1);
    expect(await dismantlingComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await dismantlingComponentsPage.deleteDismantling();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(dismantlingComponentsPage.records, beforeRecordsCount);
      expect(await dismantlingComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(dismantlingComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
