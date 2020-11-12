import {MutationTree} from 'vuex';
import {SET_DEPENDENCIES, SET_RESOURCIES, UPDATE_DEPENDENCIES_BY_FIELD} from './mutations-type';
import {Dependencies, DependenciesState, UpdateOneDependency} from './types';

export const mutations: MutationTree<DependenciesState> = {
  [SET_DEPENDENCIES](state, deps: Dependencies) {
    state.dependencies = deps;
  },
  [UPDATE_DEPENDENCIES_BY_FIELD](state, {resource, content}: UpdateOneDependency) {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    state.dependencies[resource] = content;
  },
  [SET_RESOURCIES](state, resources: string[]) {
    state.resources = resources;
  },
};
