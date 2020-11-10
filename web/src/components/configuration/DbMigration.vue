<template>
  <q-table
    class="db-migration-table"
    :data="flyway"
    :columns="columns"
    row-key="installedrank"
  ></q-table>
</template>
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import {namespace} from 'vuex-class';
  import {flyway, nameModule as namespaceServer} from '../../store/server/constants';


  const server = namespace(namespaceServer);

  @Component
  export default class DbMigration extends Vue {
    columns = [
      {
        name: 'installedrank', label: '#', field: 'installedRank', align: 'left', sortable: true,
      },
      {
        name: 'strState', label: 'State', field: 'state', align: 'left', sortable: true,
      },
      {
        name: 'desc',
        required: true,
        label: 'Description',
        align: 'left',
        field: 'description',
        sortable: true,
      },
      {
        name: 'executiontime', align: 'left', label: 'Execution time (ms)', field: 'executionTime', sortable: true,
      },
      {
        name: 'dt', align: 'left', label: 'Date', field: 'installedOn', sortable: true,
      },
      {
        name: 'script', align: 'left', label: 'Script\'s name', field: 'script', sortable: true,
      },
    ];
    @server.Getter(flyway) flyway;
  }
</script>
<style scoped>
  .db-migration-table {
    box-shadow: none;
  }
</style>
