import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import TeamsComponentsPage from './teams.page-object';
import TeamsUpdatePage from './teams-update.page-object';
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

describe('Teams e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let teamsComponentsPage: TeamsComponentsPage;
  let teamsUpdatePage: TeamsUpdatePage;
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
    teamsComponentsPage = new TeamsComponentsPage();
    teamsComponentsPage = await teamsComponentsPage.goToPage(navBarPage);
  });

  it('should load Teams', async () => {
    expect(await teamsComponentsPage.title.getText()).to.match(/Teams/);
    expect(await teamsComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Teams', async () => {
    const beforeRecordsCount = (await isVisible(teamsComponentsPage.noRecords)) ? 0 : await getRecordsCount(teamsComponentsPage.table);
    teamsUpdatePage = await teamsComponentsPage.goToCreateTeams();
    await teamsUpdatePage.enterData();
    expect(await isVisible(teamsUpdatePage.saveButton)).to.be.false;

    expect(await teamsComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(teamsComponentsPage.table);
    await waitUntilCount(teamsComponentsPage.records, beforeRecordsCount + 1);
    expect(await teamsComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await teamsComponentsPage.deleteTeams();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(teamsComponentsPage.records, beforeRecordsCount);
      expect(await teamsComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(teamsComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
