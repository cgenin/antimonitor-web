import {Module} from 'vuex';
import {getters} from './getters';
import state from './state';
import {actions} from './actions';
import {mutations} from './mutations';
import {MonithorState} from 'src/store/moniThor/types';
import {RootState} from 'src/store/types';

const moniThor: Module<MonithorState, RootState> = {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};

export default moniThor;
