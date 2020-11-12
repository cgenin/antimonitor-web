<template>
  <div class="dependencies-search-page">
    <q-card>
      <q-card-section>
        <div class="row justify-around">
          <div>
            <ul>
              <li>Ressource : <strong>{{ resource }}</strong></li>
            </ul>
          </div>
          <div>
            <ul>
              <li>Nombre de résultats : <strong>{{ fronts.length }}</strong></li>
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
      </q-card-section>
    </q-card>
    <q-card>
      <div class="shadow-2 bg-white graph-container">
        <div class="ul-tree  fix " :class="{horizontal:vertical, vertical:!vertical}">
          <ul class="root">
            <li>
              <p class="root">{{ resource }}</p>
              <ul>
                <li v-for="u in fronts" :resource="u" :key="u" class="nopointer">
                  <p>
                    {{ u }}
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
import {findByService, fronts, nameModule, reset} from 'src/store/fronts/constants';
import {Fronts} from 'src/store/fronts/types';

const frontsStore = namespace(nameModule);

@Component
export default class DependenciesSearch extends Vue {
  resource = '';
  vertical = false;
  @frontsStore.Getter(fronts) fronts;
  @frontsStore.Action(findByService) findByService: (string) => Promise<Fronts[]>;
  @frontsStore.Action(reset) reset: () => Promise<void>;

  print() {
    window.print();
  }

  async mounted() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    this.resource = (<any> this.$router).history.current.params.resource;
    await this.reset();
    await this.findByService(this.resource);
  }
}
</script>
