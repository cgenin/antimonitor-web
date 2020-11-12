<template>
  <div class="projects-page page-list container">
    <header-app :help="txtHelp" :bc-datas="[{icon:'view_list', label:'Liste des Projets'}]"></header-app>

    <q-card>
      <q-card-section>
        <h3>Liste des Projets</h3>
      </q-card-section>
      <q-separator/>
      <q-card-section>
        <div class="search-bar">
          <q-input
            v-model="filter"
            :debounce="300"
            placeholder="Rechercher par nom"
            class="col-auto"
            @input="filtering"
          ></q-input>
        </div>
        <transition
          appear
          enter-active-class="animated fadeIn"
          leave-active-class="animated fadeOut"
        >
          <div v-show="!loading">
            <q-table
              :data="list"
              :columns="columns"
              row-key="field"
              :separator="separator"
              :no-data-label="noDataAfterFiltering"
              :pagination.sync="pagination"
              :rowsPerPageOptions="rowsPerPageOptions"
              @refresh="refresh"
            >
              <template slot="top-right">
                <q-select
                  color="secondary"
                  v-model="separator"
                  :options="separatorOptions"
                  hide-underline
                ></q-select>
              </template>
              <q-td slot="body-cell-javaDeps" slot-scope="props" :props="props">
                <a href="#" v-if="props.value.length > 0" v-on:click.prevent="openInfos(props.value, 'Dépendance Java')"
                   class="tooltip">
                  <span>{{ props.value.length }}&nbsp;</span>
                  <i class="material-icons">info </i>
                </a>
                <span v-else>{{ props.value.length }}&nbsp;</span>
              </q-td>
              <q-td slot="body-cell-apis" slot-scope="props" :props="props">
                <a href="#" v-if="props.value.length > 0" v-on:click.prevent="openInfos(props.value, 'Apis')"
                   class="tooltip">
                  <span>{{ props.value.length }}&nbsp;</span>
                  <i class="material-icons">info </i>
                </a>
                <span v-else>{{ props.value.length }}&nbsp;</span>
              </q-td>
              <q-td slot="body-cell-tables" slot-scope="props" :props="props">
                <a href="#" v-if="props.value.length > 0" v-on:click.prevent="openInfos(props.value, 'Tables')"
                   class="tooltip">
                  <span>{{ props.value.length }}&nbsp;</span>
                  <i class="material-icons">info </i>
                </a>
                <span v-else>{{ props.value.length }}&nbsp;</span>
              </q-td>
              <q-td slot="body-cell-changelog" slot-scope="props" :props="props">
                <changelog-button :key="props.row.id" :content="props.value"/>
              </q-td>
              <q-td slot="body-cell-destinationUrl" slot-scope="props" :props="props">
                <q-btn flat color="accent" @click="$router.push(props.value)" size="sm">
                  <q-icon name="description"></q-icon>
                </q-btn>
              </q-td>
            </q-table>
          </div>
        </transition>
        <q-inner-loading :visible="loading">
          <q-spinner-gears size="50px" color="primary"></q-spinner-gears>
        </q-inner-loading>
      </q-card-section>
    </q-card>
    <q-dialog v-model="modal">
      <q-layout view="hHh Lpr fFf" container class="bg-white"
                :style="{minWidth: '55vw', minHeight: '85vh', padding:'1em'}">
        <q-header class="bg-primary">
          <q-toolbar>
            <q-toolbar-title>
            {{ modalOpt.title }}
            </q-toolbar-title>
            <q-btn flat v-close-popup round dense icon="close"/>
          </q-toolbar>
        </q-header>
        <q-page-container>
          <q-page padding>
        <div>
          <q-list highlight>
            <q-item clickable v-ripple v-for="d in modalOpt.data" :key="d">
              <q-item-section>{{ d }}</q-item-section>
            </q-item>
          </q-list>
        </div></q-page></q-page-container>
      </q-layout>
    </q-dialog>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import Component from 'vue-class-component';
import {namespace} from 'vuex-class';
import {loadProjects, nameModule, projects} from '../store/microservices/constants';
import ChangelogButton from '../components/ChangeLogButton.vue';
import HeaderApp from '../components/HeaderApp.vue';
import {noDataAfterFiltering, pagination, rowsPerPageOptions, separator, separatorOptions} from '../datatable-utils';
import {ProjectDto} from '../store/microservices/types';

const txtHelp = require('./projects/help.md');

const microServicesStore = namespace(nameModule);

@Component({
  components: {
    HeaderApp,
    ChangelogButton,
  },
})
export default class ProjectsList extends Vue {
  txtHelp = txtHelp;
  list: ProjectDto[] = [];
  filter = '';
  modal = false;
  modalOpt = {};
  loading = false;
  separator = separator;
  separatorOptions = separatorOptions;
  pagination = pagination;
  noDataAfterFiltering = noDataAfterFiltering;
  rowsPerPageOptions = rowsPerPageOptions;
  columns = [
    {
      label: 'Nom',
      field: 'name',
      name: 'name',
      sortable: true,
      type: 'string',
      filter: false,
    },
    {
      label: 'Snapshot',
      field: 'snapshot',
      name: 'snapshot',
      sortable: false,
      type: 'string',
      filter: false,
    },
    {
      label: 'Release',
      field: 'release',
      name: 'release',
      sortable: false,
      type: 'string',
      filter: false,
    },
    {
      label: 'Java',
      field: 'javaDeps',
      name: 'javaDeps',
      sort(a, b) {
        return (a.length - b.length);
      },
      type: 'number',
      filter: false,
    },
    {
      label: 'Apis',
      field: 'apis',
      name: 'apis',
      sort(a, b) {
        return (a.length - b.length);
      },
      type: 'number',
      filter: false,
    },
    {
      label: 'Tables',
      field: 'tables',
      name: 'tables',
      sort(a, b) {
        return (a.length - b.length);
      },
      type: 'number',
      filter: false,
    },
    {
      label: 'Dernière Mise à jour',
      field: 'latest',
      name: 'latest',
      sortable: true,
      type: 'date',
      filter: false,
    },
    {
      label: 'Log',
      field: 'changelog',
      name: 'changelog',
      sortable: false,
    },
    {
      label: 'Détail',
      field: 'destinationUrl',
      name: 'destinationUrl',
      sortable: false,
    },
  ];

  @microServicesStore.Getter(projects) projects: ProjectDto[];
  @microServicesStore.Action(loadProjects) loadProjects: () => Promise<ProjectDto[]>;

  async refresh() {
    this.loading = true;
    await this.loadProjects();
    this.list = this.projects;
    this.filter = '';
    this.loading = false;
  }

  openInfos(p: string[], title: string) {
    const data = p.sort();
    this.modalOpt = {
      data, title,
    };
    this.modal = true;
  }

  filtering() {
    const {filter} = this;
    if (!filter || filter.trim() === '') {
      this.list = this.projects;
      return;
    }

    const upFilter = filter.toUpperCase();
    this.list = this.projects
      .filter((p) => {
        const {name} = p;
        const upName = name.toUpperCase();
        return (upName.indexOf(upFilter) !== -1);
      });
  }

  async mounted() {
    await this.refresh();
  }
}
</script>
<style lang="stylus" scoped>
.projects-page
  .search-bar
    display flex
    justify-content center
    margin 5px 5px

    .q-input
      min-width 30vw
</style>
