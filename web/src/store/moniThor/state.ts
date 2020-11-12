import {MonithorState} from 'src/store/moniThor/types';

const state: MonithorState = {
  url: null,
  service: {},
  npm: {
    list: null,
    byName: {},
  },
  servers: [],
};
export default state;
