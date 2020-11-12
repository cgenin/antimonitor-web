<template>
  <div class="export-page">
    <div class="row q-gutter-sm">
      <div class="col-md-6 buttons">
        <button-export type="projects" label="Export des projets"></button-export>
        <button-export type="tables" label="Export des tables"></button-export>
        <button-export type="apis" label="Export des Apis"></button-export>
        <button-export type="dependencies" label="Export des Dépendances"></button-export>
        <button-export type="web-apps" label="Export des applications web"></button-export>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import Vue from 'vue'
import Component from 'vue-class-component';
import ButtonExport from './ButtonExport.vue';

@Component({ components: { ButtonExport } })
export default class ConfigurationExport extends Vue {
}

</script>
<style scoped>
.export-page {
  margin-top: 2em;
  width: 100%;
}

.export-page .buttons {
  width: 100%;
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}
</style>

