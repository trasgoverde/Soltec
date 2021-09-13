import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './resources.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ResourcesDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const resourcesEntity = useAppSelector(state => state.resources.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="resourcesDetailsHeading">
          <Translate contentKey="soltecApp.resources.detail.title">Resources</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{resourcesEntity.id}</dd>
          <dt>
            <span id="waterConsumtion">
              <Translate contentKey="soltecApp.resources.waterConsumtion">Water Consumtion</Translate>
            </span>
          </dt>
          <dd>{resourcesEntity.waterConsumtion}</dd>
          <dt>
            <span id="reforestryIndex">
              <Translate contentKey="soltecApp.resources.reforestryIndex">Reforestry Index</Translate>
            </span>
          </dt>
          <dd>{resourcesEntity.reforestryIndex}</dd>
        </dl>
        <Button tag={Link} to="/resources" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/resources/${resourcesEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ResourcesDetail;
