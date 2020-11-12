<template>
  <div class="tables-page container">
    <header-app :bc-datas="[{icon:'border_all', label:'Tables'}]"></header-app>
    <q-card>
      <q-card-section>

        <div class=" bg-light">
          <q-tabs
            align="left"
            class="bg-white">
            <q-route-tab icon="view_list" label="Liste des tables"
                         :to="TablesList" color="primary">
            </q-route-tab>
            <q-route-tab icon="show_chart" label="Graphique"
                         :to="TablesCharts" color="primary">
            </q-route-tab>
          </q-tabs>
        </div>
        <router-view/>
      </q-card-section>
    </q-card>
  </div>
</template>
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import HeaderApp from '../components/HeaderApp.vue';
  import { TablesCharts, TablesList } from '../Routes';

  @Component({
    components: { HeaderApp },
  })
  export default class Tables extends Vue {
    TablesCharts = TablesCharts;
    TablesList = TablesList;
  }
</script>
<style lang="stylus" scoped>
  @import "../css/pages/tables.styl"
</style>
