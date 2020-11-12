import {ActionTree} from 'vuex';
import {SET_CURRENT_SERVICE, SET_NPM_COMPONENT_INFOS, SET_NPM_LIST, SET_SERVERS} from './mutations-type';
import {getNpmComponentInfos, getServiceForServer, loadNpmList, loadServers, pingServer} from './constants';
import {RootState} from 'src/store/types';
import {
  ByName,
  Component,
  MonithorService,
  MonithorState,
  Server,
  ServiceContent,
  ServiceOnServer
} from 'src/store/moniThor/types';

export const actions: ActionTree<MonithorState, RootState> = {
  [getServiceForServer](context, {service, server}: ServiceOnServer) {
    if (context.state.url) {
      return fetch(`${context.state.url}/api/services/ping?service=${service}&server=${server}`)
        .then(response => response.json())
        .then((content : ServiceContent) => {
          const currentService: MonithorService = {service, server, content};
          context.commit(SET_CURRENT_SERVICE, currentService);
          return content;
        });
    }
    return Promise.reject(new Error('no moniThorUrl'));
  },
  [getNpmComponentInfos]({state, commit}, component: Component) {
    const {name} = component;
    if (state.npm.byName[name]) {
      return Promise.resolve(state.npm.byName[component.name]);
    }
    return fetch(`${state.url}/api/components/${name}`)
      .then(response => response.json())
      .then((content) => {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        const update: ByName = {name, content};
        commit(SET_NPM_COMPONENT_INFOS, update);
        return content;
      })
      .catch(e => console.error(e));
  },
  [loadNpmList]({state, commit}) {
    if (state.url) {
      if (state.npm.list) {
        return Promise.resolve(state.npm.list);
      }
      const url = `${state.url}/api/components/`;
      return fetch(url)
        .then(response => response.json())
        .then((content) => {
          commit(SET_NPM_LIST, content);
          return content;
        })
        .catch((err) => {
          console.error(`*** Error in contacting monithor server : ${url} ***`);
          console.error(err);
        });
    }
    console.log('No monithor URL.');
    return Promise.resolve([]);
  },
  [loadServers]({state, commit}) {
    return fetch(`${state.url}/api/servers`)
      .then(res => res.json())
      .then((serversWrappers: any) => {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        commit(SET_SERVERS, serversWrappers.servers);
      });
  },
  [pingServer]({state}, {server}: Server) {
    return fetch(`${state.url}/api/services/ping/all?server=${server}`)
      .then(res => res.json());
  },
};

