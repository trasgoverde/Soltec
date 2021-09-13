import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Machinery from './machinery';
import MachineryDetail from './machinery-detail';
import MachineryUpdate from './machinery-update';
import MachineryDeleteDialog from './machinery-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={MachineryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={MachineryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={MachineryDetail} />
      <ErrorBoundaryRoute path={match.url} component={Machinery} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={MachineryDeleteDialog} />
  </>
);

export default Routes;
