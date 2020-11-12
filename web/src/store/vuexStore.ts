/* eslint-disable global-require */
import Vue from 'vue';
import Vuex, { StoreOptions } from 'vuex';
import { RootState } from './types';
import hotupdate from './hotupdate';
// we first import the module
import configuration from './configuration/index';
import console from './console/index';
import server from './server';
import microservices from './microservices/index';
import dependencies from './dependencies';
import fronts from './fronts/index';
import moniThor from './moniThor/index';

Vue.use(Vuex);

export default function () {
  const data: StoreOptions<RootState> = {
    state: {
      version: '1.0.0'
    },
    modules: {
      // then we reference it
      configuration,
      console,
      server,
      microservices,
      moniThor,
      dependencies,
      fronts
    }
  };

  const store = new Vuex.Store<RootState>(data);

// if we want some HMR magic for it, we handle
// the hot update like below. Notice we guard this
// code with "process.env.DEV" -- so this doesn't
// get into our production build (and it shouldn't).

  // eslint-disable-next-line @typescript-eslint/no-unsafe-return
  return hotupdate(store);
}
