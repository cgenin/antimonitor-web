<template>
  <div class="administration-page">
    <q-list>
      <q-item-label header>Filtres sur les librairies Java.
        <span class="text-grey-6" style="font-size:.95em;">
          (Ce filtre permet de spécifier uniquement les librairies interne à afficher dans la vue "projet".)
        </span>
      </q-item-label>
      <q-item class="column items-stretch">

        <div>
          <am-chips v-model="javaFilters" label-input="Ajouter un nouveau filtre"></am-chips>
        </div>
      </q-item>
      <q-separator/>
      <q-item-label header>Filtres sur les dépendences Npm</q-item-label>
      <q-item class="column items-stretch">
        <div>
          <am-chips v-model="npmFilters"></am-chips>

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
import AmChips from '../../components/AmChips.vue';
import {error, success} from 'src/Toasts';
import {ConfiguationState} from 'src/store/configuration/types';

const conf = namespace(namespaceConf);


@Component({
  components: {AmChips},
})
export default class ConfigurationAdministration extends Vue {
  javaFilters: string[] = [];
  npmFilters: string[] = [];

  @conf.Action(initialize) initialize: () => Promise<any>;
  @conf.Action(saveConfiguration) saveConfiguration: (ConfiguationState) => Promise<any>;
  @conf.Getter(state) state: ConfiguationState;


  refresh(): void {
    const {javaFilters, npmFilters} = this.state;
    this.javaFilters = javaFilters;
    this.npmFilters = npmFilters;
  }

  save(): void {
    const {javaFilters, npmFilters} = this;
    const configuration = Object.assign({}, this.state, {javaFilters, npmFilters});
    this.saveConfiguration(configuration)
      .then(() => success())
      .catch(err => error(err));
  }

  mounted(): void {
    this.initialize()
      .then(() => {
        this.refresh();
      })
      .catch(err => console.error(err));
  }
}
</script>
<style lang="stylus" scoped>
@import "../../css/pages/administration.styl"
</style>
