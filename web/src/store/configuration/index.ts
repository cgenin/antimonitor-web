import {Module} from 'vuex';
import {getters} from './getters';
import {actions} from './actions';
import {mutations} from './mutations';
import {ConfiguationState} from './types';
import {RootState} from '../types';


export const state: ConfiguationState = {
  javaFilters: null,
  npmFilters: null,
  moniThorUrl: null,
  ignoreJava: null
};


const namespaced = true;

const configuration: Module<ConfiguationState, RootState> = {
  namespaced,
  state,
  getters,
  actions,
  mutations
};
export default configuration;

