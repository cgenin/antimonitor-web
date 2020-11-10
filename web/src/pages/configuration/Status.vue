<template>
  <div class="status-page">
    <q-list>
      <q-list-header>Status</q-list-header>
      <q-item class="row sm-gutter">
        <div class="col-md-3 col-xs-6">
          <q-card class="sm-card row">
            <q-card-main class="no-padding row">
              <q-icon class="background-positive" v-if="health.up" name="check" color="white" size="2rem"></q-icon>
              <q-icon class="background-negative" v-if="!health.up" name="clear" color="white" size="2rem"></q-icon>
              <h5 class="text-center">
                Server Http
              </h5>
            </q-card-main>
          </q-card>
        </div>
        <div class="col-md-3 col-xs-6">
          <q-card class="sm-card row">
            <q-card-main class="no-padding row">
              <q-icon class="background-positive" v-if="health.dbState" name="check" color="white" size="2rem"></q-icon>
              <q-icon class="background-negative" v-if="!health.dbState" name="clear" color="white" size="2rem"></q-icon>
              <h5 class="text-center">
                Postgres sql
              </h5>
            </q-card-main>
          </q-card>
        </div>
      </q-item>
      <div>
        <q-item-separator/>
        <q-collapsible>
          <template slot="header">
            <q-chip color="primary" small class="q-mr-sm">
              Db migration.
            </q-chip>
            <q-item-main label=""/>
            <q-item-side right>
              if the DB must be updated or not.
            </q-item-side>
          </template>
          <div>
            <db-migration></db-migration>
          </div>
        </q-collapsible>
      </div>
      <q-item-separator/>
      <q-collapsible>
        <template slot="header">
          <q-chip color="primary" small class="q-mr-sm">
            Console
          </q-chip>
          <q-item-main label=""/>
          <q-item-side right>
            Get the server's logs.
          </q-item-side>
        </template>
        <div>
          <console></console>
        </div>
      </q-collapsible>
    </q-list>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import Component from 'vue-class-component';
import {namespace} from 'vuex-class';
import {getHealth, health, nameModule as namespaceServer} from '../../store/server/constants';
import DbMigration from '../../components/configuration/DbMigration';
import Console from '../../components/configuration/Console';

const server = namespace(namespaceServer);

@Component({
  components: {DbMigration, Console},
})
export default class ConfigurationStatus extends Vue {
  @server.Action(getHealth) getHealth;
  @server.Getter(health) health;


  mounted() {
    this.getHealth();
  }
}
</script>
