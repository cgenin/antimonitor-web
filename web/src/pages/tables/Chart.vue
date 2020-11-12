<template>
  <div class="tables-chart-page">
    <q-card>
      <q-card-section>
        <div class="column items-center">
          <div class="chart-title">
            <h3>Nombre de tables par projets (Median : {{ median1 }})</h3>
          </div>
          <card-chart
            card-title="Nombre de Table par projet"
            :data="chart1"
            type="pie"
          />
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import Component from 'vue-class-component';
import {namespace} from 'vuex-class';
import {groupByProjects, nameModule, } from 'src/store/microservices/constants';
import CardChart from '../../components/CardChart.vue';
import {GroupProjects} from 'src/store/microservices/types';

const microservices = namespace(nameModule);


@Component({
  components: {
    CardChart,
  },
})
export default class TablesChart extends Vue {
  chart1: GroupProjects = {};
  median1 = 0;
  @microservices.Action(groupByProjects) groupByProjects: () => Promise<GroupProjects>;

  async mounted() {
    const chart1: GroupProjects = await this.groupByProjects()
    this.chart1 = chart1;
    const values = Object.values(chart1);
    const sum = values.reduce((acc, nb) => acc + nb, 0);
    const res = sum / values.length;
    this.median1 = Math.floor(res);
  }
}
</script>
<style lang="stylus" scoped>
.tables-chart-page
  .chart-title
    margin-bottom 1em
</style>
