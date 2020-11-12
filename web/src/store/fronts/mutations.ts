import {MutationTree} from 'vuex';
import {SET_FRONTS, SET_RESUME, SET_SERVICES} from './mutations-type';
import {Fronts, FrontsState} from './types';

export const mutations: MutationTree<FrontsState> = {
  [SET_RESUME](state, resume: Fronts[]) {
    state.resume = resume;
  },
  [SET_SERVICES](state, services: string[]) {
    state.services = services;
  },
  [SET_FRONTS](state, fronts: Fronts[]) {
    state.fronts = fronts;
  },
};
