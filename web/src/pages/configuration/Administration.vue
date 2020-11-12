<template>
  <div class="administration-page">
    <q-list>
      <q-list-header>Filtres sur les librairies Java.
        <span class="text-grey-6" style="font-size:.95em;">
          (Ce filtre permet de spécifier uniquement les librairies interne à afficher dans la vue "projet".)
        </span>
      </q-list-header>
      <q-item class="column items-stretch">

        <div>
          <q-chips-input color="green" v-model="javaFilters" placeholder="Ajouter un nouveau filtre"></q-chips-input>
        </div>
      </q-item>
      <q-item-separator/>
      <q-list-header>Filtres sur les dépendences Npm</q-list-header>
      <q-item class="column items-stretch">
        <div>
          <q-chips-input color="blue-grey" v-model="npmFilters" placeholder="Ajouter un nouveau filtre"></q-chips-input>
        </div>
        <hr>
        <div class="buttons">
          <div class="button">
            <q-btn flat color="black" @click="refresh" icon="refresh">Rafraîchir</q-btn>
          </div>
          <div class="button">
            <q-btn color="primary" @click="save" icon="save">Sauvegarder</q-btn>
          </div>
        </div>
      </q-item>
    </q-list>
    <q-inner-loading :visible="testConnectionLoading">
      <q-spinner-gears size="50px" color="primary"></q-spinner-gears>
    </q-inner-loading>

    <stack-trace-modal :opened="errorModal" :callback="hideModal" :stacktrace="modal.stacktrace"
                       :message="modal.message"></stack-trace-modal>
  </div>
</template>
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import {namespace} from 'vuex-class';
  import {
    global as state,
    initialize,
    nameModule as namespaceConf,
    save as saveConfiguration
  } from '../../store/configuration/constants';
  import StackTraceModal from '../../components/StackTraceModal';
  import {error, success} from '../../Toasts';
  import {ConfiguationState, Mysql} from '../../store/configuration/types';

  const conf = namespace(namespaceConf);

  interface ModalResult {
    message: string
    stacktrace?: string
  }

  @Component({
    components: {StackTraceModal},
  })
  export default class ConfigurationAdministration extends Vue {

    javaFilters: string[] = [];
    npmFilters: string[] = [];
    errorModal = false;
    modal: ModalResult = {
      message: '',
    };

    @conf.Action(initialize) initialize;
    @conf.Action(saveConfiguration) saveConfiguration;
    @conf.Getter(state) state: ConfiguationState;

    hideModal() {
      this.errorModal = false;
    }




    refresh() {
      const {javaFilters, npmFilters, mysql} = this.state;
      this.javaFilters = javaFilters;
      this.npmFilters = npmFilters;
    }

    save() {
      const {javaFilters, npmFilters} = this;
      const configuration = Object.assign({}, this.state, {javaFilters, npmFilters});
      this.saveConfiguration(configuration)
        .then(() => success())
        .catch(err => error(err));
    }

    mounted() {
      this.initialize()
        .then(() => {
          this.refresh();
        })
        .catch(err => console.log(err));
    }
  };
</script>
<style lang="stylus" scoped>
  @import "../../css/pages/administration.styl"
</style>
