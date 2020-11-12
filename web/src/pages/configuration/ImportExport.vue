<template>
  <div class="import-export-page">
    <q-list>
      <q-item-label header>Import</q-item-label>
      <q-item>
        <import></import>
      </q-item>
      <q-item-label header>Export</q-item-label>
      <q-item>
        <export></export>
      </q-item>
    </q-list>
  </div>
</template>
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import Import from '../../components/configuration/Import.vue';
  import Export from '../../components/configuration/Export.vue';

  @Component({components: {Import, Export}})
  export default class ConfigurationImportExport extends Vue {
  }

</script>
