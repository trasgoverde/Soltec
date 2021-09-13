import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Terrain from './terrain';
import Teams from './teams';
import Providers from './providers';
import Logistics from './logistics';
import HumanResources from './human-resources';
import Machinery from './machinery';
import Resources from './resources';
import Dismantling from './dismantling';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}terrain`} component={Terrain} />
      <ErrorBoundaryRoute path={`${match.url}teams`} component={Teams} />
      <ErrorBoundaryRoute path={`${match.url}providers`} component={Providers} />
      <ErrorBoundaryRoute path={`${match.url}logistics`} component={Logistics} />
      <ErrorBoundaryRoute path={`${match.url}human-resources`} component={HumanResources} />
      <ErrorBoundaryRoute path={`${match.url}machinery`} component={Machinery} />
      <ErrorBoundaryRoute path={`${match.url}resources`} component={Resources} />
      <ErrorBoundaryRoute path={`${match.url}dismantling`} component={Dismantling} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
