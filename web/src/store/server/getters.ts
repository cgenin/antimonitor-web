import {GetterTree} from 'vuex';
import {RootState} from 'src/store/types';
import {ServerState} from 'src/store/server/type';

export const getters: GetterTree<ServerState, RootState> = {
  health(state) {
    return state.health;
  },
  flyway(state) {
    const f = state.health.flyway;
    if (!f) {
      return [];
    }
    return [...f.appliedMigration, ...f.pendingMigration];
  },
  deletedCollections(state) {
    return state.deletedCollections;
  }
}
