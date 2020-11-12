<template>
  <div class="dependencies-page page-list container">
    <header-app :bc-datas="[{icon:'link', label:'Dépendances service -> front'}]"></header-app>
    <q-card>
      <q-card-section>
        <h3>Dépendances service -> front</h3>
      </q-card-section>
      <q-separator/>
      <q-card-section>
        <div class="noprint row inputs">
          <div class="label-container col-sm-2 col-xs-12">
            <label>Les projets qui nécessitent</label>
          </div>

          <q-select
            class="col-sm-7 col-xs-12"
            color="secondary"
            placeholder="Sélectionner la ressource"
            filled
            :value="terms"
            use-input
            hide-selected
            fill-input
            input-debounce="0"
            :options="options"
            @filter="search"
            @input="selected"
          />
          <div class="row col-sm-2 col-xs-12">
            <q-btn class="btn-flat-primary full-width" @click="reset" flat>Reset</q-btn>
          </div>
        </div>
      </q-card-section>
    </q-card>

    <router-view :key="$route.fullPath"></router-view>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import Component from 'vue-class-component';
import {namespace} from 'vuex-class';
import HeaderApp from '../../components/HeaderApp.vue';
import {FrontDependencies} from 'src/Routes';

import {loadServices, nameModule, services} from 'src/store/fronts/constants';

const fronts = namespace(nameModule);

@Component({
  components: {
    HeaderApp,
  },
})
export default class FrontDependenciesPage extends Vue {
  terms: string = null;
  options: string[] =[];
  @fronts.Getter(services) services: string[];
  @fronts.Action(loadServices) loadServices: () => Promise<void>;

  search(terms: string, update: (Function) => void) {
    update(() => {
      const t = terms.toUpperCase();
      this.options = this.services.filter(s => s.toUpperCase().indexOf(t) > -1);
    });
  }

  async selected(result: string) {
    console.log(result)
    await this.$router.push(`${FrontDependencies}/search/${result.toUpperCase()}`);
  }

  async reset() {
    this.terms = '';
    this.options = [];
    await this.$router.push((FrontDependencies));
  }

  async mounted() {
    await this.loadServices();
    const {resource} = (<any> this.$router).history.current.params;
    if (resource) {
      this.terms = resource;
    }
  }
}
</script>
<style lang="stylus">
@import "../../css/pages/dependencies.styl"
</style>
