import {GetterTree} from 'vuex';
import {RootState} from 'src/store/types';
import {MonithorState} from 'src/store/moniThor/types';

export const getters: GetterTree<MonithorState, RootState> = {

  url(state) {
    return state.url;
  },
  service(state) {
    return state.service;
  },
  servers(state) {
    return state.servers;
  },
};
