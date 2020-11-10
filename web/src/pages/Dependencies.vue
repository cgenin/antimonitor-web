<template>
  <div class="dependencies-page page-list container">
    <header-app :bc-datas="[{icon:'link', label:'Dépendance Intra Service'}]"></header-app>
    <q-card>
      <q-card-title>
        <h3>Dépendance Intra Service</h3>
      </q-card-title>
      <q-card-separator/>
      <q-card-main>
        <div class="noprint row inputs">
          <div class="label-container col-sm-2 col-xs-12">
            <label>Les projets qui nécessitent</label>
          </div>
          <q-field class="col-sm-7 col-xs-12" icon="search">
            <q-input color="secondary" v-model="terms"
                     placeholder="Sélectionner la ressource">
              <q-autocomplete
                @search="search"
                :min-characters="1"
                @selected="selected"
              />
            </q-input>
          </q-field>
          <div class="row col-sm-2 col-xs-12">
            <q-btn class="btn-flat-primary full-width" @click="reset" flat>Reset</q-btn>
          </div>
        </div>
      </q-card-main>
    </q-card>

    <router-view :key="$route.fullPath"></router-view>
  </div>
</template>
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import {namespace} from 'vuex-class';
  import filter from 'quasar-framework/src/utils/filter';
  import {ms} from '../Routes';
  import HeaderApp from '../components/HeaderApp';
  import {initialize, nameModule, resources} from '../store/dependencies/constants';

  const dependenciesStore = namespace(nameModule);

  @Component(
    {
      components: {
        HeaderApp,
      },
    }
  )
  export default class Configuration extends Vue {
    terms: string = null;
    @dependenciesStore.Getter(resources) resources: string[];
    @dependenciesStore.Action(initialize) initialize: () => Promise<any>;

    search(terms, done) {
      const t = terms.toUpperCase();
      done(filter(t, {field: 'value', list: this.resources}));
    }

    selected(result) {
      this.$router.push(ms(`/dependencies/search/${result.value.toUpperCase()}`));
    }

    reset() {
      this.terms = '';
      this.$router.push(ms('/dependencies'));
    }

    mounted() {
      this.initialize()
        .then(() => {
          const {resource} = (<any>this.$router).history.current.params;
          if (resource) {
            this.terms = resource;
          }
        });
    }
  }
</script>
<style lang="stylus">
  @import "../css/pages/dependencies.styl"
</style>
