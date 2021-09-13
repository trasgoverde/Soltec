import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Providers from './providers';
import ProvidersDetail from './providers-detail';
import ProvidersUpdate from './providers-update';
import ProvidersDeleteDialog from './providers-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ProvidersUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ProvidersUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ProvidersDetail} />
      <ErrorBoundaryRoute path={match.url} component={Providers} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ProvidersDeleteDialog} />
  </>
);

export default Routes;
