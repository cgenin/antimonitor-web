<template>
  <li @click="click" :class="{nopointer:notOpens, withpointer:!notOpens}">
    <p>
      <i v-if="!notOpens && open" class="fas fa-minus-circle"></i>
      <i v-if="!notOpens && !open" class="fas fa-plus-circle"></i>
      {{ resource }}
    </p>
    <ul v-if="open">
      <sub-tree v-for="sub in subs" :key="sub" :resource="sub"></sub-tree>
    </ul>
  </li>
</template>
<script lang="ts">
import Vue from 'vue';
import Component from 'vue-class-component';
import {namespace} from 'vuex-class';
import {dependencies, nameModule, usedBy} from '../store/dependencies/constants';
import {Prop} from 'vue-property-decorator';
import {Dependencies} from 'src/store/dependencies/types';

const dependenciesStore = namespace(nameModule);

@Component
export default class SubTree extends Vue {
  @Prop(String) resource: string;
  open = false;
  subs: string[] = [];
  notOpens = false;
  @dependenciesStore.Getter(dependencies) dependencies: Dependencies;
  @dependenciesStore.Action(usedBy) usedBy: (string) => Promise<string[]>;

  async click() {
    if (this.notOpens) {
      return;
    }
    this.open = !this.open;
    if (this.open) {
      this.subs = await this.usedBy(this.resource)
    } else {
      this.subs = [];
    }
  }

  mounted() {
    this.notOpens = !!this.dependencies[this.resource];
  }
}
</script>
