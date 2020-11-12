<template>
  <div class="status-page">
    <q-list>
      <q-item-label header>Status</q-item-label>
      <q-item class="row q-gutter-sm">
        <div class="col-md-3 col-xs-6">
          <q-card class="sm-card row">
            <q-card-section class="no-padding row">
              <q-icon class="background-positive" v-if="health.up" name="check" color="white" size="2rem"></q-icon>
              <q-icon class="background-negative" v-if="!health.up" name="clear" color="white" size="2rem"></q-icon>
              <h5 class="text-center">
                Server Http
              </h5>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-md-3 col-xs-6">
          <q-card class="sm-card row">
            <q-card-section class="no-padding row">
              <q-icon class="background-positive" v-if="health.dbState" name="check" color="white" size="2rem"></q-icon>
              <q-icon class="background-negative" v-if="!health.dbState" name="clear" color="white"
                      size="2rem"></q-icon>
              <h5 class="text-center">
                Postgres sql
              </h5>
            </q-card-section>
          </q-card>
        </div>
      </q-item>
      <div>
        <q-separator/>
        <q-expansion-item>
          <template slot="header">
            <q-chip color="primary" small class="q-mr-sm">
              Db migration.
            </q-chip>
            <q-item-label></q-item-label>
            <q-item-side right>
              if the DB must be updated or not.
            </q-item-side>
          </template>
          <div>
            <db-migration></db-migration>
          </div>
        </q-expansion-item>
      </div>
      <q-separator/>
      <q-expansion-item>
        <template slot="header">
          <q-chip color="primary" small class="q-mr-sm">
            Console
          </q-chip>
          <q-item-label label=""/>
          <q-item-side right>
            Get the server's logs.
          </q-item-side>
        </template>
        <div>
          <console></console>
        </div>
      </q-expansion-item>
    </q-list>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import Component from 'vue-class-component';
import {namespace} from 'vuex-class';
import {getHealth, health, nameModule as namespaceServer} from '../../store/server/constants';
import DbMigration from '../../components/configuration/DbMigration.vue';
import Console from '../../components/configuration/Console.vue';
import {Health} from 'src/store/server/type';

const server = namespace(namespaceServer);

@Component({
  components: {DbMigration, Console},
})
export default class ConfigurationStatus extends Vue {
  @server.Action(getHealth) getHealth: () => Promise<any>;
  @server.Getter(health) health: Health;


  async mounted() {
    await this.getHealth();
  }
}
</script>
