import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Terrain from './terrain';
import TerrainDetail from './terrain-detail';
import TerrainUpdate from './terrain-update';
import TerrainDeleteDialog from './terrain-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TerrainUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TerrainUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TerrainDetail} />
      <ErrorBoundaryRoute path={match.url} component={Terrain} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={TerrainDeleteDialog} />
  </>
);

export default Routes;
