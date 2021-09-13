import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Resources from './resources';
import ResourcesDetail from './resources-detail';
import ResourcesUpdate from './resources-update';
import ResourcesDeleteDialog from './resources-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ResourcesUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ResourcesUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ResourcesDetail} />
      <ErrorBoundaryRoute path={match.url} component={Resources} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ResourcesDeleteDialog} />
  </>
);

export default Routes;
