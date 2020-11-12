<template>
  <div class="projects-page page-list container">
    <header-app :bc-datas="[{icon:'view_list', label:'Liste des Projets NPM'}]"></header-app>

    <q-card>
      <q-card-section>
        <h3>Liste des Projets NPM</h3>
      </q-card-section>
      <q-separator></q-separator>
      <q-card-section>
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
            <template slot="top-left" >
              <q-input
                v-model="filter"
                :debounce="300"
                class="col-auto"
              >
                <template v-slot:append>
                  <q-icon name="search"/>
                </template>
              </q-input>
            </template>
            <template slot="top-right" >
              <q-select
                color="secondary"
                v-model="separator"
                :options="separatorOptions"
                emit-value
              ></q-select>
            </template>
            <q-td slot="body-cell-readme" slot-scope="props" :props="props">
              <markdown-button title="Readme" color="accent" icon="description" :key="props.row.id"
                               :content="props.value"
                               v-if="props.value !== 'ERROR: No README data found!'"></markdown-button>
            </q-td>
            <q-td slot="body-cell-infos" slot-scope="props" :props="props">
              <markdown-button title="Infos" icon="info" :key="props.row.id" :content="props.value"></markdown-button>
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
import {getNpmComponentInfos, loadNpmList, nameModule} from 'src/store/moniThor/constants';
import MarkdownButton from '../../components/MarkdownButton.vue';
import HeaderApp from '../../components/HeaderApp.vue';
import filtering from '../../FiltersAndSorter';
import {
  noData,
  noDataAfterFiltering,
  pagination,
  rowsPerPageOptions,
  separator,
  separatorOptions,
} from 'src/datatable-utils';
import {formatYYYYMMDDHHmm} from 'src/Dates';

const monithorStore = namespace(nameModule);
// eslint-disable-next-line @typescript-eslint/no-unsafe-return
const objectAsArray: (object) => any[] = obj => Object.keys(obj).map(key => obj[key]);

interface BuildTime {
  [key: string]: string
}

const getBuildTime = (component: any) => {
  // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
  const v: BuildTime = component.versions;
  return Object.values(v)
    .reverse()
    .reduce((a: string, b: string) => `
    ${a}
    ### ${b}
    Build time : ${component.time[b] ? formatYYYYMMDDHHmm(component.time[b]) : ''}
    `, '');
};
const createInfos = component => {
  // eslint-disable-next-line @typescript-eslint/restrict-template-expressions,no-undef
  const description:string = component.description;
  return `
    ## Description
    ${description}
    ## Versions
        ${getBuildTime(component)}`;
};

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

  @monithorStore.Action(loadNpmList) loadNpmList: () => Promise<any>;

  @monithorStore.Action(getNpmComponentInfos) getNpmComponentInfos: (string) => Promise<any>;

  private original: any[];

  async refresh() {
    this.filter = '';
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    const obj = await this.loadNpmList();
    this.list = objectAsArray(obj)
      .filter(component => component.name)
      .map((el) => {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        const latestVersion = (el['dist-tags'] || {}).latest;
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        const latestBuildTime = formatYYYYMMDDHHmm(new Date(el.time[latestVersion]));
        return {
          ...el,
          // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
          latestVersion,
          latestBuildTime,
          infos: createInfos(el),
        };
      })
      // eslint-disable-next-line @typescript-eslint/no-unsafe-call
      .sort((a, b) => a.name.localeCompare(b.name));
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    this.original = this.list;
  }

  async selectComponent(component:string) {
    this.loading = true;
    try {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
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
    await this.refresh();
  }
}
</script>
<style>
</style>
