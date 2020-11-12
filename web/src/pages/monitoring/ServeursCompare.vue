<template>
  <div class="projects-page page-list container">
    <header-app :bc-datas="[{icon:'view_list', label:'Liste des Projets NPM'}]"></header-app>

    <q-card>
      <q-card-section>
        <h3>Comparaison de serveurs</h3>
      </q-card-section>
      <q-separator/>
      <q-card-section>
        <q-select
          class="selectServer"
          float-label="Sélectionner un (ou plusieurs) serveurs"
          v-model="serverList"
          :options="servers"
          @input="serverChanged"
          v-if="servers"
          multiple
        />
        <div v-if="services">
          <q-inner-loading :visible="loading">
            <q-spinner-gears size="50px" color="primary"/>
          </q-inner-loading>
          <q-table
            v-if="columns.length > 1"
            :data="services"
            :columns="columns"
            row-key="field"
            :filter="filter"
            :separator="separator"
            :no-data-label="noData"
            :pagination.sync="pagination"
            :no-results-label="noDataAfterFiltering"
            :rowsPerPageOptions="rowsPerPageOptions"
            @refresh="refresh"
          >
            <template slot="top-left" >
              <q-input
                v-model="filter"
                class="col-auto"
                :debounce="300"
              >
                <template v-slot:append>
                  <q-icon name="search"/>
                </template>
              </q-input>
            </template>
            <template slot="top-right">
              <q-select
                color="secondary"
                v-model="separator"
                :options="separatorOptions"
                hide-underline
                emit-value
              ></q-select>
            </template>
            <q-td slot="body-cell-compare" slot-scope="props" :props="props">
              <div
                v-if="servicesByServer[props.col.label][props.value].info && servicesByServer[props.col.label][props.value].info.build">
                <charts-button :serviceInfos="servicesByServer[props.col.label][props.value]"
                               :serviceName="props.value" :server="props.col.label"></charts-button>
              </div>
              <span
                v-if="!servicesByServer[props.col.label][props.value].responding">
                <q-item-side class="down" icon="portable_wifi_off"></q-item-side>
                </span>
              <span
                v-if="!(servicesByServer[props.col.label][props.value].info && servicesByServer[props.col.label][props.value].info.build) && servicesByServer[props.col.label][props.value].responding">
                  <q-item-side class="up" icon="graphic_eq"/> Up but no infos...
                </span>
            </q-td>
          </q-table>
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import Component from 'vue-class-component';
import {namespace} from 'vuex-class';
import {loadServers, nameModule, pingServer, servers as serverFromStore} from '../../store/moniThor/constants';
import ChangelogButton from '../../components/ChangeLogButton.vue';
import ChartsButton from '../../components/ChartsButton.vue';
import HeaderApp from '../../components/HeaderApp.vue';
import filtering from '../../FiltersAndSorter';
import {
  noData,
  noDataAfterFiltering,
  pagination,
  rowsPerPageOptions,
  separator,
  separatorOptions,
} from '../../datatable-utils';

const serverStore = namespace(nameModule);

@Component({
  components: {
    HeaderApp,
    ChangelogButton,
    ChartsButton,
  },
})
export default class ServeursCompare extends Vue {
  serverList = [];
  services = [];
  private original: any[];
  servicesByServer = [];
  selected = null;
  loading = false;
  filter = '';
  separator = separator;
  separatorOptions = separatorOptions;
  pagination = pagination;
  noData = noData;
  noDataAfterFiltering = noDataAfterFiltering;
  rowsPerPageOptions = rowsPerPageOptions;
  servers = null;
  server = null;
  columns = [];
  runningPromise = null;
  cancelRunningPromise = false;
  moniThorUrl = null;
  @serverStore.Getter(serverFromStore) serverFromStore: any;
  @serverStore.Action(loadServers) loadServers: () => Promise<any>;
  @serverStore.Action(pingServer) pingServer: ({server: string}) => Promise<any>;

  refresh() {
    this.filter = '';
  }

  objectAsArray(obj) {
    return Object.keys(obj).map(key => obj[key]);
  }

  serverChanged(servers) {
    this.cancelRunningPromise = !!this.runningPromise;
    this.loading = true;
    this.columns = [{
      label: 'Services',
      field: 'serviceName',
      name: 'serviceName',
      type: 'string',
      filter: true,
      align: 'left',
    }];
    // eslint-disable-next-line @typescript-eslint/no-unsafe-call,@typescript-eslint/no-unsafe-member-access,@typescript-eslint/no-unsafe-assignment
    const responsePromises = servers.map(server => this.pingServer({server})
      .then((services) => {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        this.servicesByServer[server] = services.reduce((a, b) => {
          a[b.serviceName] = b;
          return a;
        }, {});
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        this.services = services
          .map(s => ({serviceName: s.serviceName}))
          // eslint-disable-next-line @typescript-eslint/no-unsafe-call
          .sort((a, b) => a.serviceName.localeCompare(b.serviceName));
        this.original = this.services;
        return server;
      }));

    this.runningPromise = Promise.all(responsePromises)
      .then((results) => {
        if (!this.cancelRunningPromise) {
          results.forEach((server) => {
            if (this.serverList.includes(server) && this.columns.filter(c => c.label === server).length === 0) {
              this.columns.push({
                label: server,
                field: 'serviceName',
                name: 'compare',
                sortable: true,
                type: 'string',
                filter: true,
                align: 'left',
              });
            }
          });
        } else {
          this.cancelRunningPromise = false;
        }
      })
      .catch((error) => {
        console.error(error);
      })
      .then(() => {
        this.loading = false;
        this.runningPromise = null;
      });
  }

  filtering() {
    this.services = filtering(this.original, this.filter);
  }

  async mounted() {
    this.pagination.rowsPerPage = 50;
    await this.loadServers();
    // eslint-disable-next-line @typescript-eslint/no-unsafe-call,@typescript-eslint/no-unsafe-member-access,@typescript-eslint/no-unsafe-assignment
    this.servers = this.serverFromStore.map(s => ({
      label: s,
      value: s,
    }));
    this.refresh();
  }
}
</script>
<style lang="stylus">
@import "../../css/pages/serveursCompare.styl"
</style>
