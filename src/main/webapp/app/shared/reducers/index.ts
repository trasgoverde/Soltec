import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale, { LocaleState } from './locale';
import authentication, { AuthenticationState } from './authentication';
import applicationProfile, { ApplicationProfileState } from './application-profile';

import administration, { AdministrationState } from 'app/modules/administration/administration.reducer';
import userManagement, { UserManagementState } from 'app/modules/administration/user-management/user-management.reducer';
import register, { RegisterState } from 'app/modules/account/register/register.reducer';
import activate, { ActivateState } from 'app/modules/account/activate/activate.reducer';
import password, { PasswordState } from 'app/modules/account/password/password.reducer';
import settings, { SettingsState } from 'app/modules/account/settings/settings.reducer';
import passwordReset, { PasswordResetState } from 'app/modules/account/password-reset/password-reset.reducer';
// prettier-ignore
import terrain from 'app/entities/terrain/terrain.reducer';
// prettier-ignore
import teams from 'app/entities/teams/teams.reducer';
// prettier-ignore
import providers from 'app/entities/providers/providers.reducer';
// prettier-ignore
import logistics from 'app/entities/logistics/logistics.reducer';
// prettier-ignore
import humanResources from 'app/entities/human-resources/human-resources.reducer';
// prettier-ignore
import machinery from 'app/entities/machinery/machinery.reducer';
// prettier-ignore
import resources from 'app/entities/resources/resources.reducer';
// prettier-ignore
import dismantling from 'app/entities/dismantling/dismantling.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer = {
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  register,
  activate,
  passwordReset,
  password,
  settings,
  terrain,
  teams,
  providers,
  logistics,
  humanResources,
  machinery,
  resources,
  dismantling,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
};

export default rootReducer;
