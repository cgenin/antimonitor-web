import {ActionTree} from 'vuex';
import {getResources, initialize, nameModule, reset, usedBy} from './constants';
import {SET_DEPENDENCIES, SET_RESOURCIES, UPDATE_DEPENDENCIES_BY_FIELD} from './mutations-type';
import {DependenciesState, UpdateOneDependency} from './types';
import {RootState} from '../types';

export const actions: ActionTree<DependenciesState, RootState> = {
  [initialize]({commit}) {
    commit(SET_RESOURCIES, []);
    commit(SET_DEPENDENCIES, {});
    return this.dispatch(`${nameModule}/${getResources}`);
  },

  [getResources]({commit}) {
    return fetch('/api/dependencies')
      .then(res => res.json())
      .then((content) => {
        commit(SET_RESOURCIES, content);
        return content;
      });
  },

  [reset]({commit}) {
    commit(SET_DEPENDENCIES, {});
  },

  [usedBy]({commit}, resource: string) {
    return fetch(`/api/dependencies/${resource}`)
      .then(res => res.json())
      .then((content) => {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        const update:UpdateOneDependency = {resource, content};
        commit(UPDATE_DEPENDENCIES_BY_FIELD, update);
        return content;
      });
  },
};
