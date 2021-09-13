import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './human-resources.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const HumanResourcesDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const humanResourcesEntity = useAppSelector(state => state.humanResources.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="humanResourcesDetailsHeading">
          <Translate contentKey="soltecApp.humanResources.detail.title">HumanResources</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{humanResourcesEntity.id}</dd>
          <dt>
            <span id="investmentsLocally">
              <Translate contentKey="soltecApp.humanResources.investmentsLocally">Investments Locally</Translate>
            </span>
          </dt>
          <dd>{humanResourcesEntity.investmentsLocally}</dd>
          <dt>
            <span id="laborAccidentsindex">
              <Translate contentKey="soltecApp.humanResources.laborAccidentsindex">Labor Accidentsindex</Translate>
            </span>
          </dt>
          <dd>{humanResourcesEntity.laborAccidentsindex}</dd>
        </dl>
        <Button tag={Link} to="/human-resources" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/human-resources/${humanResourcesEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default HumanResourcesDetail;
