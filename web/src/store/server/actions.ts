import {SET_DELETED_COLLECTION, UPDATE} from './mutations-type';
import {clearDatas, getHealth, importDb, remove} from './constants';
// eslint-disable-next-line no-unused-vars
import {ActionTree} from 'vuex'
import {ServerState} from 'src/store/server/type';
import {RootState} from 'src/store/types';

const actions: ActionTree<ServerState, RootState> = {
  [getHealth]({commit}) {
    return fetch('/api/_health')
      .then(res => res.json())
      .then((data) => {
        commit(UPDATE, data);
      });
  },
  [importDb](context, data: any) {
    const body = JSON.stringify(data);
    return fetch('/api/configuration/db/import', {
      method: 'PUT',
      body,
    });
  },
  [remove]() {
    return fetch('/api/apps', {method: 'DELETE'});
  },
  [clearDatas]({commit}) {
    commit(SET_DELETED_COLLECTION, []);
    return fetch('/api/apps/calculate/datas', {method: 'DELETE'})
      .then(res => res.json())
      .then((coll) => {
        commit(SET_DELETED_COLLECTION, coll);
        return coll;
      });
  },
};

export default actions;
