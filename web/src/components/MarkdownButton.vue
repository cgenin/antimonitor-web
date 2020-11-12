<template>
  <q-btn @click="openModal" :color="computedColor" v-if="content" size="sm">
    <q-icon :name="icon"/>
    <q-dialog ref="layoutModal" v-model="modal"  >
      <q-layout view="hHh Lpr fFf" container
                :style="{minWidth: '85vw', minHeight: '85vh', padding:'1em'}" class="bg-white">
        <q-header class="bg-primary">
          <q-toolbar>
            <q-toolbar-title>
            {{title}}
            </q-toolbar-title>
            <q-btn flat v-close-popup round dense icon="close"/>
          </q-toolbar>
        </q-header>
        <q-page-container>
          <q-page padding>
        <div>
          <vue-markdown :source="content" :show="true"></vue-markdown>
        </div></q-page></q-page-container>
      </q-layout>
    </q-dialog>
  </q-btn>

</template>
<script lang="ts">
  import Vue from 'vue'
  import Component from 'vue-class-component';
  import VueMarkdown from './VueMarkdown';
  import {Prop} from 'vue-property-decorator';

  @Component({
    components: {
      VueMarkdown
    },
  })
  export default class MarkdownButton extends Vue {
    modal = false;
    @Prop(String) content:string;
    @Prop(String) title:string;
    @Prop(String) icon:string;
    @Prop(String) color?: string;

    get computedColor() {
      return this.color || 'primary';
    }

    openModal() {
      this.modal = true;
      this.$emit('click');
    }
  }
</script>
