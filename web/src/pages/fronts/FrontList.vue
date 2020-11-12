<template>
  <div class="front-list-page page-list container">
    <header-app :bc-datas="[{icon:'fab fa-chrome', label:'Liste des Applications'}]"></header-app>

    <q-card>
      <q-card-section>
        <h3>Liste des Applications</h3>
      </q-card-section>
      <q-separator/>
      <q-card-section>
        <transition
          appear
          enter-active-class="animated fadeIn"
          leave-active-class="animated fadeOut"
        >
          <q-table
            :data="frontApps"
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
            <q-td slot="body-cell-servicesClient" slot-scope="props" :props="props">
              <a href="#" v-if="props.value.length > 0" v-on:click.prevent="openServices(props.value)"
                 class="tooltip">
                <span>{{ props.value.length }}&nbsp;</span>
                <i class="material-icons">info </i>
              </a>
              <span v-else>{{ props.value.length }}&nbsp;</span>
            </q-td>
            <q-td slot="body-cell-packageJsonTxt" slot-scope="props" :props="props">
              <q-btn flat color="accent" @click="openPackageJson(props.value)" size="sm">
                <q-icon name="description"/>
              </q-btn>
            </q-td>
          </q-table>
        </transition>
      </q-card-section>
    </q-card>
    <q-dialog v-model="modal">
      <q-layout view="hHh Lpr fFf" container class="bg-white"
                :style="{minWidth: '75vw', minHeight: '85vh', padding:'1em'}">
        <q-header class="bg-primary">
          <q-toolbar>
            <q-toolbar-title>Package.json</q-toolbar-title>
            <q-btn flat v-close-popup round dense icon="close"/>
          </q-toolbar>
        </q-header>
        <q-page-container>
          <q-page padding>
            <div>
              <pre>{{ modalTxt }}</pre>
            </div>
          </q-page>
        </q-page-container>
      </q-layout>
    </q-dialog>
    <q-dialog v-model="modalServices">
      <q-layout view="hHh Lpr fFf" container class="bg-white"
                :style="{minWidth: '65vw', minHeight: '85vh', padding:'1em'}">
        <q-header class="bg-primary">
          <q-toolbar>
            <q-toolbar-title>
              Micro-Services :
            </q-toolbar-title>
            <q-btn flat v-close-popup round dense icon="close"/>
          </q-toolbar>
        </q-header>
        <q-page-container>
          <q-page padding>
            <div>
              <q-list padding>
                <q-item clickable v-ripple v-for="service in listServices" :key="service.key">
                  <q-item-section>{{ service.key }} : {{ service.value }}</q-item-section>
                </q-item>
              </q-list>
            </div>
          </q-page>
        </q-page-container>
      </q-layout>
    </q-dialog>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import Component from 'vue-class-component';
import {namespace} from 'vuex-class';
import {loadResume, nameModule as namespaceFronts, resume} from '../../store/fronts/constants';
import HeaderApp from '../../components/HeaderApp.vue';
import {noData, noDataAfterFiltering, pagination, rowsPerPageOptions, separator} from 'src/datatable-utils';


const frontsStore = namespace(namespaceFronts);

@Component({
  components: {
    HeaderApp,
  },
})
export default class FrontList extends Vue {
  listServices = [];
  filter = '';
  modal = false;
  modalServices = false;
  modalTxt = '';
  separator = separator;
  pagination = pagination;
  noData = noData;
  noDataAfterFiltering = noDataAfterFiltering;
  rowsPerPageOptions = rowsPerPageOptions;
  columns = [
    {
      label: 'Nom',
      field: 'name',
      name: 'name',
      align: 'left',
      sortable: true,
      type: 'string',
      filter: true,
    },
    {
      label: 'Dernière Mise à jour',
      field: 'lastUpdateDate',
      name: 'lastUpdateDate',
      sortable: false,
      align: 'left',
      type: 'string',
    },

    {
      label: 'Snapshot',
      field: 'snapshotVersion',
      name: 'snapshotVersion',
      align: 'left',

      sortable: false,
      type: 'string',
      filter: true,
    },
    {
      label: '-',
      field: 'lastUpdateSnapshotDate',
      name: 'lastUpdateSnapshotDate',
      align: 'left',
      sortable: true,
      type: 'string',
    },
    {
      label: 'Release',
      field: 'releaseVersion',
      name: 'releaseVersion',
      align: 'left',

      sortable: false,
      type: 'string',
      filter: true,
    },
    {
      label: '-',
      field: 'lastUpdateReleaseDate',
      name: 'lastUpdateReleaseDate',
      align: 'left',
      sortable: true,
      type: 'string',
    },
    {
      label: 'Services',
      field: 'servicesClient',
      name: 'servicesClient',
      align: 'left',
      sortable: false,
    },
    {
      label: 'Package.json',
      field: 'packageJsonTxt',
      name: 'packageJsonTxt',
      align: 'left',
      sortable: false,
    }];

  @frontsStore.Getter(resume) frontApps;
  @frontsStore.Action(loadResume) loadResume: () => Promise<any>;

  async refresh() {
    return await this.loadResume();
  }

  openPackageJson(obj: string): void {
    this.modalTxt = obj;
    this.modal = true;
  }

  openServices(services: string[]): void {
    this.listServices = services;
    this.modalServices = true;
  }

  async mounted() {
    await this.refresh();
  }
}
</script>
