import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './teams.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const TeamsDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const teamsEntity = useAppSelector(state => state.teams.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="teamsDetailsHeading">
          <Translate contentKey="soltecApp.teams.detail.title">Teams</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{teamsEntity.id}</dd>
          <dt>
            <span id="originMaterials">
              <Translate contentKey="soltecApp.teams.originMaterials">Origin Materials</Translate>
            </span>
          </dt>
          <dd>{teamsEntity.originMaterials}</dd>
          <dt>
            <span id="originSteal">
              <Translate contentKey="soltecApp.teams.originSteal">Origin Steal</Translate>
            </span>
          </dt>
          <dd>{teamsEntity.originSteal}</dd>
          <dt>
            <span id="originAluminium">
              <Translate contentKey="soltecApp.teams.originAluminium">Origin Aluminium</Translate>
            </span>
          </dt>
          <dd>{teamsEntity.originAluminium}</dd>
          <dt>
            <span id="sustainableProviders">
              <Translate contentKey="soltecApp.teams.sustainableProviders">Sustainable Providers</Translate>
            </span>
          </dt>
          <dd>{teamsEntity.sustainableProviders ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/teams" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/teams/${teamsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TeamsDetail;
