<template>
  <!-- Don't drop "q-app" class -->
  <div id="q-app">
    <q-layout
      view="lHh Lpr lFf"
      :left-class="{'bg-primary': false}"
    >
      <q-header>
        <q-toolbar color="header" text-color="header">
          <q-btn
            flat
            @click="menuExpand()"
            class="burger-icon"
            :icon="opened ? 'close' : 'menu'"
          >
          </q-btn>

          <q-toolbar-title>
            Anti-monitor
            <div slot="subtitle">Récapitualtif des projets Micro services</div>
          </q-toolbar-title>
        </q-toolbar>
      </q-header>
      <q-drawer

        v-model="opened"
      >
        <div class="tool-bar">
          <q-list no-border link inset-delimiter>
            <div class="tool-bar-header">
              <q-item-label header class="main">
                <img src="./assets/sinestro_corps_logo_small.png" alt="logo anti-monitor">
                <h3>&nbsp;Anti-monitor</h3>
              </q-item-label>
              <q-item-label header>
                <h4>Menu</h4>
              </q-item-label>
            </div>
            <q-item :to="Welcome" exact>
              <q-item-section avatar>
                <q-icon name="home"></q-icon>
              </q-item-section>
              <q-item-section>
                <q-item-label>Accueil</q-item-label>
              </q-item-section>
            </q-item>
            <q-expansion-item icon="fas fa-cogs" label="Micro services" v-model="openedMs">
              <q-item :to="ProjectsList">
                <q-item-section avatar>
                  <q-avatar icon="view_list"></q-avatar>
                </q-item-section>
                <q-item-section>
                  <q-item-label>Liste des projets</q-item-label>
                  <q-item-label caption>Résumé des derniers build</q-item-label>
                </q-item-section>
              </q-item>
              <q-item :to="Tables">
                <q-item-section avatar>
                  <q-avatar icon="border_all"/>
                </q-item-section>
                <q-item-section>
                  <q-item-label>Liste des tables</q-item-label>
                  <q-item-label caption>liaisons services / tables</q-item-label>
                </q-item-section>
              </q-item>
              <q-item :to="ApisList">
                <q-item-section avatar>
                  <q-avatar icon="explore"/>
                </q-item-section>
                <q-item-section>
                  <q-item-label>Liste des apis</q-item-label>
                  <q-item-label caption> Liste des traitements</q-item-label>
                </q-item-section>
              </q-item>
              <q-item :to="Dependencies">
                <q-item-section avatar>
                  <q-avatar icon="link"/>
                </q-item-section>
                <q-item-section>
                  <q-item-label>Dépendances</q-item-label>
                  <q-item-label caption> Dép entre MicroServices</q-item-label>
                </q-item-section>
              </q-item>
            </q-expansion-item>
            <q-expansion-item icon="fab fa-chrome" label="Web apps" v-model="openedFront">
              <q-item :to="FrontList">
                <q-item-section avatar>
                  <q-avatar icon="list_alt"/>
                </q-item-section>
                <q-item-section>
                  <q-item-label>Liste des applications</q-item-label>
                  <q-item-label caption> Résumé des derniers build</q-item-label>
                </q-item-section>
              </q-item>
              <q-item :to="FrontDependencies">
                <q-item-section avatar>
                  <q-avatar icon="link"/>
                </q-item-section>
                <q-item-section>
                  <q-item-label>Dépendances</q-item-label>
                  <q-item-label caption> Dép entre un MicroService et applications</q-item-label>
                </q-item-section>
              </q-item>
            </q-expansion-item>
            <q-item to="/rt/npm-list" v-if="moniThorUrl">
              <q-item-section avatar>
                <q-avatar icon="featured_play_list"/>
              </q-item-section>
              <q-item-section>
                <q-item-label>NPM</q-item-label>
                <q-item-label caption> Informations sur les projets NPM</q-item-label>
              </q-item-section>
            </q-item>
            <q-item to="/rt/monitoring" v-if="moniThorUrl">
              <q-item-section avatar>
                <q-avatar icon="graphic_eq"/>
              </q-item-section>
              <q-item-section>
                <q-item-label>Monitoring</q-item-label>
                <q-item-label caption> Informations serveurs</q-item-label>
              </q-item-section>
            </q-item>
            <q-item to="/rt/configuration">
              <q-item-section avatar>
                <q-avatar icon="build"/>
              </q-item-section>
              <q-item-section>
                <q-item-label>Console d'administration</q-item-label>
                <q-item-label caption> Configuration et outils</q-item-label>
              </q-item-section>
            </q-item>
            <q-item @click.native="showAbout">
              <q-item-section avatar>
                <q-avatar icon="copyright"/>
              </q-item-section>
              <q-item-section>
                <q-item-label>A propos</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </div>
      </q-drawer>
      <q-page-container>
        <transition :name="transitionName">
          <router-view/>
        </transition>
        <q-dialog v-model="openAboutModal" minimized>
          <div class="bg-white" style="padding: 50px">
            <div class="text-h3 q-mb-md">Version : {{ packageJson.version }}</div>
            <p>Tu comprends, je suis mon meilleur modèle car entre penser et dire, il y a un monde de différence et
              c'est très, très beau d'avoir son propre moi-même ! Ça respire le meuble de Provence, hein ?</p>
            <p>Tu comprends, je ne suis pas un simple danseur car là, j'ai un chien en ce moment à côté de moi et je
              le caresse, parce que spirituellement, on est tous ensemble, ok ? Et là, vraiment, j'essaie de tout
              coeur de donner la plus belle réponse de la terre !</p>
            <q-btn color="red" v-close-popup label="Close" @click="openAboutModal = false"/>
          </div>
        </q-dialog>
      </q-page-container>
    </q-layout>

  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import Component from 'vue-class-component';
import {namespace} from 'vuex-class';
import {
  ApisList,
  Dependencies,
  FrontDependencies,
  FrontList,
  isFront,
  isMicroService,
  ProjectsList,
  Tables,
  Welcome,
} from './Routes';
import {initialize, moniThorUrl, nameModule as namespaceConf} from './store/configuration/constants';
import {loadNpmList, nameModule as namespaceMonithor} from './store/moniThor/constants';
import {Watch} from 'vue-property-decorator';
import {Route} from 'vue-router';
import packageJson from '../package.json';

let first = true;
const confStore = namespace(namespaceConf);
const monithorStore = namespace(namespaceMonithor);
/*
 * Root component
 */
@Component
export default class App extends Vue {
  $q: any;
  openedMs = false;
  openedFront = false;
  transitionName = 'slide-right';
  opened = this.$q.platform.is.desktop;
  Welcome = Welcome;
  ProjectsList = ProjectsList;
  ApisList = ApisList;
  Dependencies = Dependencies;
  Tables = Tables;
  FrontList = FrontList;
  FrontDependencies = FrontDependencies;
  openAboutModal = false;
  packageJson = packageJson;
  @confStore.Getter(moniThorUrl) moniThorUrl;
  @confStore.Action(initialize) initialize: () => Promise<void>;
  @monithorStore.Action(loadNpmList) loadNpmList: () => Promise<void>;

  menuExpand() {
    this.opened = !this.opened;
  }

  showAbout() {
    this.openAboutModal = true;
  }

  @Watch('$route')
  onRouteChanged(to: Route, from: Route) {
    const reload = to || {path: ''};
    const toDepth = reload.path.split('/').length;
    const fromDepth = from.path.split('/').length;
    this.transitionName = toDepth < fromDepth ? 'slide-right' : 'slide-left';
    if (first) {
      this.openedMs = isMicroService(to);
      this.openedFront = isFront(to);
      first = false;
    }
  }

  async mounted() {
    await this.initialize();
    await this.loadNpmList();
  }
}
</script>
