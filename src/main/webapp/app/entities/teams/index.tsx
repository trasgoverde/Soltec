import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Teams from './teams';
import TeamsDetail from './teams-detail';
import TeamsUpdate from './teams-update';
import TeamsDeleteDialog from './teams-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TeamsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TeamsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TeamsDetail} />
      <ErrorBoundaryRoute path={match.url} component={Teams} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={TeamsDeleteDialog} />
  </>
);

export default Routes;
