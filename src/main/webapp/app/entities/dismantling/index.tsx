import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Dismantling from './dismantling';
import DismantlingDetail from './dismantling-detail';
import DismantlingUpdate from './dismantling-update';
import DismantlingDeleteDialog from './dismantling-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={DismantlingUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={DismantlingUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={DismantlingDetail} />
      <ErrorBoundaryRoute path={match.url} component={Dismantling} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={DismantlingDeleteDialog} />
  </>
);

export default Routes;
