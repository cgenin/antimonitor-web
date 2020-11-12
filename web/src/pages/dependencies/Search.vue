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
              <li>Nombre de résultats : <strong>{{ usedBy.length }}</strong></li>
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
                <sub-tree v-for="u in list" :resource="u" :key="u"></sub-tree>
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
import SubTree from '../../components/SubTree.vue';
import {nameModule, reset, usedBy} from 'src/store/dependencies/constants';

const dependencies = namespace(nameModule);

@Component({
  components: {
    SubTree,
  },
})
export default class DependenciesSearch extends Vue {
  resource = '';
  list: any[] = [];
  vertical = false;
  @dependencies.Action(usedBy) usedBy: (c: string) => Promise<any[]>;
  @dependencies.Action(reset) reset: () => Promise<void>;

  print() {
    window.print();
  }

  async mounted() {
    const history = (<any> this.$router).history;
    this.resource = history.current.params.resource;
    await this.reset();
    const c = await this.usedBy(this.resource);
    this.list = c;
  }
}
</script>
