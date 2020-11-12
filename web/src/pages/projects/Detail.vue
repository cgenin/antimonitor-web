<template>
  <div class="main container">
    <header-app :help="txtHelp"
                :bc-datas="[{icon:'view_list', label:'Liste des Projets', path : ProjectsList},{icon:'fas fa-file-alt', label:title}]"></header-app>
    <q-card>
      <q-card-section>
        <h3>{{ title }}</h3>
      </q-card-section>
      <q-card-section>
        <p class="caption">Sélectionner une version</p>
        <q-select v-model="id" :options="selectVersions" emit-value @change="changeVersion">
        </q-select>
      </q-card-section>
    </q-card>
    <div class="selected-list">
      <div>
      </div>
      <q-list separator class="bg-white">
        <q-expansion-item icon="fas fa-coffee" label="Librairies Java" v-if="selected.javaDeps"
                          :disable="selected.javaDeps.length === 0"
                          :caption="`Nombre : ${selected.javaDeps.length}`">
          <div class="list-table">
            <ul>
              <li v-for="deps in selected.javaDeps" :key="deps">{{ deps }}</li>
            </ul>
          </div>
        </q-expansion-item>
        <q-expansion-item icon="border_all" label="Tables" v-if="selected.tables"
                          :disable="selected.tables.length === 0"
                          :caption="`Nombre : ${selected.tables.length}`">
          <div class="list-table">
            <ul>
              <li v-for="deps in selected.tables" :key="deps">{{ deps }}</li>
            </ul>
          </div>
        </q-expansion-item>
        <q-expansion-item icon="explore" label="Apis" v-if="selected.apis"
                          :disable="selected.apis.length === 0"
                          :caption="`Nombre : ${selected.apis.length}`">
          <div class="list-table">
            <ul>
              <li v-for="deps in selected.apis" :key="deps">{{ deps }}</li>
            </ul>
          </div>
        </q-expansion-item>
        <q-expansion-item icon="change_history" label="Change Log">
          <vue-markdown :source="selected.changelog">
          </vue-markdown>
        </q-expansion-item>
      </q-list>
    </div>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import Component from 'vue-class-component';
import {namespace} from 'vuex-class';
import VueMarkdown from '../../components/VueMarkdown';
import {ProjectsList} from 'src/Routes';
import {getProject, getVersionsForProject, nameModule, project, versions} from 'src/store/microservices/constants';
import HeaderApp from '../../components/HeaderApp.vue';
import {sortStringForSorter} from 'src/FiltersAndSorter';
import {Project, VersionDto} from 'src/store/microservices/types';
import {OptionWithStamp} from 'src/datatable-utils';

// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
const txtHelp: string = require('./help.md');

const microServices = namespace(nameModule);


const convertToOption = (v: VersionDto) => {
  const stamp = (v.isSnapshot) ? 'snapshot' : 'release';
  const newVar: OptionWithStamp = {
    label: v.name,
    value: v.id,
    stamp,
  };
  return newVar;
};

@Component({
  components: {
    HeaderApp,
    VueMarkdown,
  },
})
export default class ProjectDetail extends Vue {
  id = null;
  latest: VersionDto;
  txtHelp = txtHelp;
  selected: VersionDto = {
    latest: '',
    id: '',
    name: '',
    javaDeps: [],
    tables: [],
    apis: [],
    changelog: '',
    latestUpdate: 0,
    isSnapshot: false
  };

  selectVersions = [];
  title = '';
  ProjectsList = ProjectsList;
  @microServices.Getter(project) project?: Project;
  @microServices.Getter(versions) versions: VersionDto[];
  @microServices.Action(getProject) getProject: (id: string) => Promise<Project>;
  @microServices.Action(getVersionsForProject) getVersionsForProject: (id: string) => Promise<VersionDto[]>;

  changeVersion(id) {
    this.selected = this.versions.find(v => v.id === id);
  }

  mounted() {
    const {id} = this.$route.params;
    Promise.all([this.getProject(id), this.getVersionsForProject(id)])
      .then(() => {
        this.title = `Détail du projet : ${this.project.name}`;
        this.latest = this.versions
          .map((v) => {
            v.tables = v.tables.sort();
            v.javaDeps = v.javaDeps.sort();
            v.apis = v.apis.sort();
            return v;
          })
          .reduce((a, b) => {
            if (a.latestUpdate > b.latestUpdate) {
              return a;
            }
            return b;
          });
        this.id = convertToOption(this.latest);
        this.selected = this.latest;
        this.selectVersions = this.versions
          .map(convertToOption)
          .sort(sortStringForSorter());
      })
      .catch(err => console.error(err));
  }
}
</script>
<style lang="stylus" scoped>
.main
  background inherit

.selected-list
  padding 10px 0
  background none
</style>
