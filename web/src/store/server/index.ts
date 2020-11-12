import {Module} from 'vuex';
import state from './state'
import {getters} from './getters';
import actions from './actions';
import {mutations} from './mutations';
import {ServerState} from './type';
import {RootState} from '../types';

const server: Module<ServerState, RootState> = {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};

export default server;
