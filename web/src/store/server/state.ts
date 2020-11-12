import { ServerState } from './type'

const defaultState: ServerState = {
  health: { up: false },
  deletedCollections: [],
};

export default defaultState;
