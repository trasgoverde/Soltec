import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './providers.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProvidersDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const providersEntity = useAppSelector(state => state.providers.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="providersDetailsHeading">
          <Translate contentKey="soltecApp.providers.detail.title">Providers</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{providersEntity.id}</dd>
          <dt>
            <span id="agreementParis">
              <Translate contentKey="soltecApp.providers.agreementParis">Agreement Paris</Translate>
            </span>
          </dt>
          <dd>{providersEntity.agreementParis}</dd>
          <dt>
            <span id="certifiedSustianable">
              <Translate contentKey="soltecApp.providers.certifiedSustianable">Certified Sustianable</Translate>
            </span>
          </dt>
          <dd>{providersEntity.certifiedSustianable ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/providers" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/providers/${providersEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProvidersDetail;
