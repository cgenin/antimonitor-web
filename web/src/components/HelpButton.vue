<template>
  <div class="help-modal-btn">
    <q-btn v-if="text" @click="modal=true" icon="fas fa-question" color="primary" round/>
    <q-dialog ref="lModal" v-model="modal">
      <q-layout view="hHh Lpr fFf" container class="bg-white layout-modal-help">
        <q-header class="bg-primary">
          <q-toolbar>
            <q-toolbar-title>
              Aide
            </q-toolbar-title>
            <q-btn flat v-close-popup round dense icon="close"/>
          </q-toolbar>
        </q-header>
        <q-page-container>
          <q-page padding>
            <div>
              <vue-markdown :source="text" :show="true"></vue-markdown>
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
import VueMarkdown from './VueMarkdown';
import {Prop} from 'vue-property-decorator';

@Component({
  components: {
    VueMarkdown,
  },
})
export default class HelpButton extends Vue {
  modal = false;
  @Prop(String) text: string;
}
</script>
<style>
.layout-modal-help {
  min-width: 90vw;
  min-height: 90vh;
}
</style>
