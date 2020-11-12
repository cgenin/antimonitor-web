import {SET_CURRENT_SERVICE, SET_NPM_COMPONENT_INFOS, SET_NPM_LIST, SET_SERVERS, SET_URL} from './mutations-type';
import {MutationTree} from 'vuex';
import {ByName, MonithorService, MonithorState} from 'src/store/moniThor/types';

export const mutations: MutationTree<MonithorState> = {
  [SET_URL](state, url: string) {
    state.url = url;
  },
  [SET_SERVERS](state, servers: any[]) {
    state.servers = servers;
  },
  [SET_CURRENT_SERVICE](state, service: MonithorService) {
    state.service = service;
  },
  [SET_NPM_LIST](state, list: any[]) {
    state.npm.list = list;
  },
  [SET_NPM_COMPONENT_INFOS](state, {name, content}: ByName) {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    state.npm.byName[name] = content;
  },
};
