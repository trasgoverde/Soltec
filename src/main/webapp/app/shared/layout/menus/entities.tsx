import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <>{/* to avoid warnings when empty */}</>
    <MenuItem icon="asterisk" to="/terrain">
      <Translate contentKey="global.menu.entities.terrain" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/teams">
      <Translate contentKey="global.menu.entities.teams" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/providers">
      <Translate contentKey="global.menu.entities.providers" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/logistics">
      <Translate contentKey="global.menu.entities.logistics" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/human-resources">
      <Translate contentKey="global.menu.entities.humanResources" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/machinery">
      <Translate contentKey="global.menu.entities.machinery" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/resources">
      <Translate contentKey="global.menu.entities.resources" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/dismantling">
      <Translate contentKey="global.menu.entities.dismantling" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
