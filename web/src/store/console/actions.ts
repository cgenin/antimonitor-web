import {ActionTree} from 'vuex';
import {ConsoleState, Message} from './types';
import {RootState} from '../types';
import EventBus from 'vertx3-eventbus-client';
import {ADD_MESSAGE, CLEAR, SET_LISTENNING} from './mutations-type';

let isListenning = false;

export const actions: ActionTree<ConsoleState, RootState> = {
  initialize({commit}) {
    if (!isListenning) {
      isListenning = true;
      commit(SET_LISTENNING, true);
      // eslint-disable-next-line @typescript-eslint/no-unsafe-call
      const eb = new EventBus(`http://${window.location.host}/eventbus`, {server: ''});
      // eslint-disable-next-line @typescript-eslint/no-unsafe-call
      eb.enableReconnect(true);
      eb.onopen = () => {
        const messageConnected: Message = {date: new Date().getTime(), msg: 'Console connecté', type: 'info'};
        commit(ADD_MESSAGE, messageConnected);
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        eb.registerHandler('console.text', (error, message) => {
          if (error) {
            console.error(error);
            return;
          }
          const {body} = message;
          commit(ADD_MESSAGE, body);
        });
      };
    }
  },
  erase({commit}) {
    commit(CLEAR);
  }
};


