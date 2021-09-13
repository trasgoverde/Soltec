import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity, updateEntity, createEntity, reset } from './teams.reducer';
import { ITeams } from 'app/shared/model/teams.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const TeamsUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const teamsEntity = useAppSelector(state => state.teams.entity);
  const loading = useAppSelector(state => state.teams.loading);
  const updating = useAppSelector(state => state.teams.updating);
  const updateSuccess = useAppSelector(state => state.teams.updateSuccess);

  const handleClose = () => {
    props.history.push('/teams');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(props.match.params.id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...teamsEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...teamsEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="soltecApp.teams.home.createOrEditLabel" data-cy="TeamsCreateUpdateHeading">
            <Translate contentKey="soltecApp.teams.home.createOrEditLabel">Create or edit a Teams</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="teams-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('soltecApp.teams.originMaterials')}
                id="teams-originMaterials"
                name="originMaterials"
                data-cy="originMaterials"
                type="text"
              />
              <ValidatedField
                label={translate('soltecApp.teams.originSteal')}
                id="teams-originSteal"
                name="originSteal"
                data-cy="originSteal"
                type="text"
              />
              <ValidatedField
                label={translate('soltecApp.teams.originAluminium')}
                id="teams-originAluminium"
                name="originAluminium"
                data-cy="originAluminium"
                type="text"
              />
              <ValidatedField
                label={translate('soltecApp.teams.sustainableProviders')}
                id="teams-sustainableProviders"
                name="sustainableProviders"
                data-cy="sustainableProviders"
                check
                type="checkbox"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/teams" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TeamsUpdate;
