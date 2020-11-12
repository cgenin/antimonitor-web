<template>
  <div class="row align-stretch">
    <q-btn class="btn-version" @click="openModal" color="secondary" v-if="serviceName && server" size="sm">
        <span
          v-if="serviceInfos.info && serviceInfos.info.build">
          <strong>{{ serviceInfos.info.build.version }}</strong>
          <br>
          <span>{{ formatYYYYMMDDHHmm(serviceInfos.info.build.timestamp) }}</span>
        </span>
    </q-btn>
    <markdown-button @click="openProperties" icon="list" color="orange-4" title="Properties" :content="properties"/>
    <q-dialog @hide="closingModal"
              ref="layoutModal"
              v-model="modal"
              >
      <q-layout view="hHh Lpr fFf" container class="bg-white"
                :style="{minWidth: '55vw', minHeight: '85vh', padding:'1em'}">
        <q-header class="bg-primary">
          <q-toolbar>
            <q-toolbar-title>
              "{{ serviceName }}" sur le serveur {{ server }}
            </q-toolbar-title>
            <q-btn flat v-close-popup round dense icon="close"/>
          </q-toolbar>
        </q-header>
        <q-page-container>
          <q-page padding>
            <div class="layout-padding">
              <q-inner-loading :visible="loading">
                <q-spinner-gears size="50px" color="primary"/>
              </q-inner-loading>
              <div v-if="service && modal && displayGraph">
                <h2>Temps de réponse</h2>
                <div>Moyenne : {{ getAverage() }}ms</div>
                <div>80% : {{ get80() }}ms</div>
                <hr>
                <response-time-chart
                  :service="service">
                </response-time-chart>
                <h2>Nombre de requête par url</h2>
                <counter-chart :service="service">
                </counter-chart>
                <h2>Répartition des status HTTP</h2>
                <success-chart :service="service">
                </success-chart>
              </div>
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
import {getServiceForServer, nameModule} from '../store/moniThor/constants';
import ResponseTimeChart from './charts/ResponseTimeChart';
import CounterChart from './charts/CounterChart';
import SuccessChart from './charts/SuccessChart';
import {formatYYYYMMDDHHmm, mavenToDate} from '../Dates';
import MarkdownButton from './MarkdownButton.vue';
import {Prop} from 'vue-property-decorator';
import {Environnement, ServerInServiceContent, ServiceContent} from 'src/store/moniThor/types';

const monithorStore = namespace(nameModule);

@Component({
  components: {
    MarkdownButton,
    ResponseTimeChart,
    CounterChart,
    SuccessChart,
  },
})
export default class ChartsButton extends Vue {
  modal = false;
  service?: ServerInServiceContent;
  displayGraph = false;
  loading = false;
  properties = ' ';
  @Prop() serviceName;
  @Prop() server;
  @Prop() serviceInfos;
  @monithorStore.Action(getServiceForServer) getServiceForServer: (p: any) => Promise<any>;


  async loadServiceData() {
    this.loading = true;
    if (this.serviceName && this.server) {
      const payload = {service: this.serviceName, server: this.server};
      await this.getServiceForServer(payload)
        .then((service: ServiceContent) => {
          this.service = service.servers.find(s => s.host === this.server);
        }).catch((err) => {
          console.error('Error while recuperating datas', err);
        })
        .then(() => {
          // Sans le timeout je n'arrive pas a afficher le graph...
          setTimeout(() => {
            this.loading = false;
            this.displayGraph = true;
          }, 0);
        });
    }
  }

  getAverage() {
    const metrics = this.getMetrics();
    return metrics.reduce((a, b) => a + b, 0) / metrics.length;
  }

  get80() {
    const metrics = this.getMetrics();
    return metrics
      .sort((a, b) => a - b)[Math.floor(metrics.length * 0.80)];
  }

  getMetrics(): number[] {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    const filteredMetrics = Object.keys(this.service.metrics)
      .filter(key => key.startsWith('gauge') && !key.includes('hystrix'));
    return filteredMetrics
      .reduce((obj, key) => {
        obj.push(this.service.metrics[key]);
        return obj;
      }, []);
  }

  async openModal() {
    this.modal = true;
    await this.loadServiceData();
  }

  formatKeys(a: Environnement) {
    return (Object.keys(a)
      .sort((c, b) => c.localeCompare(b))
      .reduce((a1, b1) => `
${a1}
${b1}=${a[b1]}
`, ''));
  }

  async openProperties() {
    this.properties = 'Chargement des données en cours...';
    await this.loadServiceData();
    const env = Object.keys(this.service.env)
      .filter(k => k !== 'systemProperties' && k !== 'systemEnvironment' && k !== 'servletContextInitParams')
      .sort((a, b) => a.localeCompare(b))
      .reduce((a, b) => `
${a}
----
${b}
----
${this.formatKeys(this.service.env[b])}
          `, '');
    this.properties = env;
  }

  closingModal() {
    this.displayGraph = false;
  }

  formatYYYYMMDDHHmm(date) {
    return formatYYYYMMDDHHmm(mavenToDate(date));
  }
}
</script>

<style lang="stylus">
.page-list
  .q-table
    .q-btn
      &.btn-version
        margin 3px 7px 3px 0
        min-width 146px
</style>
