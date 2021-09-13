import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import TerrainComponentsPage from './terrain.page-object';
import TerrainUpdatePage from './terrain-update.page-object';
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

describe('Terrain e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let terrainComponentsPage: TerrainComponentsPage;
  let terrainUpdatePage: TerrainUpdatePage;
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
    terrainComponentsPage = new TerrainComponentsPage();
    terrainComponentsPage = await terrainComponentsPage.goToPage(navBarPage);
  });

  it('should load Terrains', async () => {
    expect(await terrainComponentsPage.title.getText()).to.match(/Terrains/);
    expect(await terrainComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Terrains', async () => {
    const beforeRecordsCount = (await isVisible(terrainComponentsPage.noRecords)) ? 0 : await getRecordsCount(terrainComponentsPage.table);
    terrainUpdatePage = await terrainComponentsPage.goToCreateTerrain();
    await terrainUpdatePage.enterData();
    expect(await isVisible(terrainUpdatePage.saveButton)).to.be.false;

    expect(await terrainComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(terrainComponentsPage.table);
    await waitUntilCount(terrainComponentsPage.records, beforeRecordsCount + 1);
    expect(await terrainComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await terrainComponentsPage.deleteTerrain();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(terrainComponentsPage.records, beforeRecordsCount);
      expect(await terrainComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(terrainComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
