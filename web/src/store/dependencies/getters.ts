import {GetterTree} from 'vuex';
import {DependenciesState} from './types';
import {RootState} from '../types';

export const getters: GetterTree<DependenciesState, RootState> = {
  resources: state => state.resources,
  dependencies: state => state.dependencies,
};
