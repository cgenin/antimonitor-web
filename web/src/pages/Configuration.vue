<template>
  <div class="configuration-page container">
    <header-app :bc-datas="[{icon:'build', label:'Console d\'administration'}]"/>
    <q-card>
      <q-card-section>
        <h3>Console d'adminisration</h3>
      </q-card-section>
      <q-separator/>
      <q-card-section>
        <q-tabs
          align="left"
          class="bg-white">
          <q-route-tab icon="screen_share" label="Status"
                       to="/rt/configuration/status"
                       exact
                       />
          <q-route-tab icon="radio" label="Traitement"
                       to="/rt/configuration/administration"
                       exact
                       />
          <q-route-tab icon="cloud_upload" label="Import / Export"
                       to="/rt/configuration/import-export"
                       exact
                       />
          <q-route-tab icon="history" label="Reset"
                       to="/rt/configuration/reset"
                       exact
                       />
          <q-route-tab icon="graphic_eq" label="Moni-thor"
                       to="/rt/configuration/monithor"
                       exact
                       />
        </q-tabs>
        <router-view/>
      </q-card-section>
    </q-card>
  </div>
</template>
<script lang="ts">

  import HeaderApp from '../components/HeaderApp.vue';
  import Vue from 'vue';
  import Component from 'vue-class-component';

  @Component({
    components: {
      HeaderApp,
    },
  })
  export default class ConfigurationPage extends Vue {
  }
</script>

<style lang="stylus" scoped>
  @import "../css/pages/configuration.styl"
</style>
