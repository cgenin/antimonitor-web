<template>
  <div class="dependencies-search-page">
    <q-card>
      <q-card-main>
        <div class="row justify-around">
          <div>
            <ul>
              <li>Ressource : <strong>{{resource}}</strong></li>
            </ul>
          </div>
          <div>
            <ul>
              <li>Nombre de résultats : <strong>{{fronts.length}}</strong></li>
            </ul>
          </div>
          <div>
            <ul class="no-bullet">
              <li>
                Style :
                <q-radio v-model="vertical" :val="true" label="Vertical"></q-radio>
                <q-radio v-model="vertical" :val="false" label="Horizontal"></q-radio>
              </li>
            </ul>
          </div>
          <div class="print-button">
            <q-btn outline icon="print" @click="print">&nbsp;Imprimer</q-btn>
          </div>

        </div>
      </q-card-main>
    </q-card>
    <q-card>
      <div class="shadow-2 bg-white graph-container">
        <div class="ul-tree  fix " :class="{horizontal:vertical, vertical:!vertical}">
          <ul class="root">
            <li>
              <p class="root">{{resource}}</p>
              <ul>
                <li v-for="u in fronts" :resource="u" :key="u" class="nopointer">
                  <p>
                    {{u}}
                  </p>
                </li>
              </ul>
            </li>
          </ul>
        </div>

      </div>
    </q-card>
  </div>
</template>
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import {namespace} from 'vuex-class';
  import {findByService, fronts, nameModule, reset} from '../../store/fronts/constants';

  const frontsStore = namespace(nameModule);

  @Component
  export default class DependenciesSearch extends Vue {
    resource = '';
    vertical = false;
    @frontsStore.Getter(fronts) fronts;
    @frontsStore.Action(findByService) findByService;
    @frontsStore.Action(reset) reset;

    print() {
      window.print();
    }

    mounted() {
      this.resource = (<any> this.$router).history.current.params.resource;
      this.reset()
        .then(() => this.findByService(this.resource));
    }
  }
</script>
