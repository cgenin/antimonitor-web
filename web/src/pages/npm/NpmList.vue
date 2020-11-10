<template>
  <div class="projects-page page-list container">
    <header-app :bc-datas="[{icon:'view_list', label:'Liste des Projets NPM'}]"></header-app>

    <q-card>
      <q-card-title>
        <h3>Liste des Projets NPM</h3>
      </q-card-title>
      <q-card-separator></q-card-separator>
      <q-card-main>
        <div v-if="list">
          <q-table
            :data="list"
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
            <template slot="top-left" slot-scope="props">
              <q-search
                v-model="filter"
                class="col-auto"
              ></q-search>
            </template>
            <template slot="top-right" slot-scope="props">
              <q-select
                color="secondary"
                v-model="separator"
                :options="separatorOptions"
                hide-underline
              ></q-select>
            </template>
            <q-td slot="body-cell-readme" slot-scope="props" :props="props">
              <markdown-button title="Readme" color="tertiary" icon="description" :key="props.row.id"
                               :content="props.value"
                               v-if="props.value !== 'ERROR: No README data found!'"></markdown-button>
            </q-td>
            <q-td slot="body-cell-infos" slot-scope="props" :props="props">
              <markdown-button title="Infos" icon="info" :key="props.row.id" :content="props.value"></markdown-button>
            </q-td>
          </q-table>
        </div>
      </q-card-main>
    </q-card>
  </div>
</template>
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import {namespace} from 'vuex-class';
  import {getNpmComponentInfos, loadNpmList, nameModule} from '../../store/moniThor/constants';
  import MarkdownButton from '../../components/MarkdownButton';
  import HeaderApp from '../../components/HeaderApp';
  import filtering from '../../FiltersAndSorter';
  import {
    noData,
    noDataAfterFiltering,
    pagination,
    rowsPerPageOptions,
    separator,
    separatorOptions,
  } from '../../datatable-utils';
  import {formatYYYYMMDDHHmm} from '../../Dates';

  const monithorStore = namespace(nameModule);
  const objectAsArray = obj => Object.keys(obj).map(key => obj[key]);

  interface BuildTime {
    [key:string]:string
  }

  const getBuildTime = (component: any) => {
    const v:BuildTime = component.versions;
    return Object.values(v)
      .reverse()
      .reduce((a: string, b: string) => `
    ${a}
    ### ${b}
    Build time : ${component.time[b] ? formatYYYYMMDDHHmm(component.time[b]) : ''}
    `, '');
  };
  const createInfos = component => `
    ## Description
    ${component.description}
    ## Versions
        ${getBuildTime(component)}`;

  @Component({
    components: {
      HeaderApp,
      MarkdownButton,
    },
  })
  export default class NpmList extends Vue {
    list = null;
    selected = null;
    loading = false;
    filter = '';
    separator = separator;
    separatorOptions = separatorOptions;
    pagination = pagination;
    noData = noData;
    noDataAfterFiltering = noDataAfterFiltering;
    rowsPerPageOptions = rowsPerPageOptions;
    columns = [
      {
        label: 'Nom',
        field: 'name',
        name: 'name',
        width: '270px',
        sortable: true,
        type: 'string',
        filter: true,
      },
      {
        label: 'Version',
        field: 'latestVersion',
        name: 'latestVersion',
        sortable: true,
        type: 'string',
        filter: true,
      },
      {
        label: 'Build time',
        field: 'latestBuildTime',
        name: 'latestBuildTime',
        sortable: true,
        type: 'string',
        filter: true,
      },
      {
        label: 'Readme',
        field: 'readme',
        name: 'readme',
        width: '57px',
        sortable: true,
      },
      {
        label: 'Détail',
        field: 'infos',
        name: 'infos',
        width: '67px',
        sortable: false,
      },
    ];
    @monithorStore.Action(loadNpmList) loadNpmList;
    @monithorStore.Action(getNpmComponentInfos) getNpmComponentInfos;
    private original: any[];

    async refresh() {
      this.filter = '';
      const obj = await this.loadNpmList();
      this.list = objectAsArray(obj)
        .filter(component => component.name)
        .map((el) => {
          const latestBuild = (el['dist-tags'] || {}).latest;
          return {
            ...el,
            latestVersion: latestBuild,
            latestBuildTime: formatYYYYMMDDHHmm(new Date(el.time[latestBuild])),
            infos: createInfos(el),
          };
        })
        .sort((a, b) => a.name.localeCompare(b.name));
      this.original = this.list;
    }

    async selectComponent(component) {
      this.loading = true;
      try {
        this.selected = await this.getNpmComponentInfos(component);
      } catch (e) {
        console.error(e);
      }
      this.loading = false;
    }

    filtering() {
      this.list = filtering(this.original, this.filter);
    }

    async mounted() {
      this.refresh();
    }
  }
</script>
<style>
</style>
