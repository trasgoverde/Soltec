import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Logistics from './logistics';
import LogisticsDetail from './logistics-detail';
import LogisticsUpdate from './logistics-update';
import LogisticsDeleteDialog from './logistics-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={LogisticsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={LogisticsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={LogisticsDetail} />
      <ErrorBoundaryRoute path={match.url} component={Logistics} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={LogisticsDeleteDialog} />
  </>
);

export default Routes;
