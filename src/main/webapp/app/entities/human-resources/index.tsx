import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import HumanResources from './human-resources';
import HumanResourcesDetail from './human-resources-detail';
import HumanResourcesUpdate from './human-resources-update';
import HumanResourcesDeleteDialog from './human-resources-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={HumanResourcesUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={HumanResourcesUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={HumanResourcesDetail} />
      <ErrorBoundaryRoute path={match.url} component={HumanResources} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={HumanResourcesDeleteDialog} />
  </>
);

export default Routes;
