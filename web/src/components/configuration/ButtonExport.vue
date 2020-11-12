<template>
  <div class="export-btn">
  <a class="none" ref="link" :href="href" target="_blank">export</a>
  <q-btn @click="doExportJson" icon="file_download" color="primary">{{label}}</q-btn>
  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import Component from 'vue-class-component';
import {Prop} from 'vue-property-decorator';

@Component
export default class ButtonExport extends Vue {
  @Prop(String) type: string;
  @Prop(String) label: string;


  get href() {
    return `/api/data/${this.type}.json`;
  }

  doExportJson() {
    const link = this.$refs.link;
    const l = <HTMLElement> link;
    l.click();
  }
}
</script>
<style scoped>
.export-btn {
  margin:5px
}

.none {
  display: none;
}

</style>
