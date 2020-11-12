<template>
  <div class="am-chips">

    <q-input filled v-model="textToAdd" :label="labelInput" debounce="100" @keydown="addValue"></q-input>
    <div class="chips">
      <q-chip
        :key="oneValue"
        v-for="oneValue in value"
        removable
        color="secondary"
        text-color="white"
        :label="oneValue"
        @remove="removeChips(oneValue)"
      />
    </div>

  </div>
</template>
<script lang="ts">
import Vue from 'vue';
import Component from 'vue-class-component';
import {Emit, Model, Prop} from 'vue-property-decorator';


@Component
export default class AmChips extends Vue {
  textToAdd = ''
  @Prop({type: String, default: 'Ajouter une valeur', required: false}) labelInput: string;
  @Model('input') value!: string[];

  removeChips(v: string) {
    this.input([...this.value.filter(s => s !== v)]);
  }

  addValue(e) {
    if (e.key !== 'Enter') {
      return;
    }
    const v = this.textToAdd;
    if (!v || v.trim().length === 0 || this.value.find(s => s === v.trim())) {
      return;
    }

    this.input([...this.value, v.trim()]);
    this.textToAdd = '';
  }


  @Emit()
  input(newState: string[]) {
    return newState;
  }
}
</script>
<style scoped>
.am-chips {
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

.am-chips .chips {
  margin: 10px;
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  flex-wrap: wrap;
  min-height: 5em;
}
</style>
