import {ActionTree} from 'vuex';
import {ConfiguationState} from './types';
import { UPDATE } from './mutations-type';
import { initialize, save } from './constants';

import { setUrlMutation } from '../moniThor/constants';
import {RootState} from '../types';

export const actions: ActionTree<ConfiguationState, RootState> = {
  [initialize]({ commit }) {
    return fetch('/api/configuration')
      .then(res => res.json())
      .then((content) => {
        commit(UPDATE, content);
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        commit(setUrlMutation, content.moniThorUrl, { root: true });
      });
  },
  [save]({ commit }, configuration) {
    const body = JSON.stringify(configuration);
    return fetch('/api/configuration', {
      method: 'PUT',
      headers: new Headers({'content-type': 'application/json'}),
      body,
    })
      .then(() => {
        commit(UPDATE, configuration);
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        commit(setUrlMutation, configuration.moniThorUrl, { root: true });
        return configuration;
      });
  },
};
