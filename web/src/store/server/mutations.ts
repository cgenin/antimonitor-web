import {MutationTree} from 'vuex';
import {SET_DELETED_COLLECTION, UPDATE} from './mutations-type';
import {Health, ServerState} from 'src/store/server/type';

export const mutations: MutationTree<ServerState> = {
  [UPDATE](state, health: Health) {
    state.health = health;
  },
  [SET_DELETED_COLLECTION](state, deletedCollections: string[]) {
    state.deletedCollections = deletedCollections;
  },
};

