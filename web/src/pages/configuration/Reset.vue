<template>
  <div class="reset-page">
    <q-list>
      <q-list-header>Suppression des données calculées de nitrite</q-list-header>
      <q-item class="column items-start">
        <p>Cette option permet de supprimer les données calculées sur la base nitrite.</p>
        <q-btn icon="delete_forever" color="secondary" @click="doClearDatas">Données Calculer</q-btn>
      </q-item>
      <q-item-separator/>
      <q-list-header>Réimportation des données</q-list-header>
      <q-item class="column items-start">
        <q-alert
          color="error"
          icon="warning"
          type="warning"
          appear>
          Attention ! Lors d'une opération de rejoue des évènements, l'applicatif sera indisponible. <br/>L'action de
          fin n'est
          visible uniquement dans les logs du serveurs.
        </q-alert>
        <q-btn icon="import_export" class="reset-btn" big no-caps color="red" @click="doReset">
          Réimporter l'ensemble des données
        </q-btn>
      </q-item>
    </q-list>
    <q-modal v-model="showResultOfClearing" class="reset-modal" :content-css="{minWidth: '50vw', minHeight: '80vh'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <h5 class="title">Collection(s) deleted</h5>
        </q-toolbar>
        <q-toolbar slot="footer" class="modal-footer" right inverted>
          <q-btn
            color="primary"
            @click="closeModal"
            label="Close"
          ></q-btn>
        </q-toolbar>
        <div class="modal-body">
          <ul class="list">
            <li v-for="coll in deletedCollections" :key="coll.collectionName">
              <strong>{{coll.collectionName}}</strong>
              :{{coll.size}}
            </li>
          </ul>
        </div>
      </q-modal-layout>
    </q-modal>
  </div>
</template>
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import {namespace} from 'vuex-class';
  import {clearDatas, deletedCollections, nameModule as namespaceServer, remove} from '../../store/server/constants';
  import {error, success} from '../../Toasts';

  const serverStore = namespace(namespaceServer);

  @Component
  export default class ConfigurationReset extends Vue {
    showResultOfClearing = false;
    @serverStore.Getter(deletedCollections) deletedCollections;
    @serverStore.Action(clearDatas) clearDatas: () => Promise<void>;
    @serverStore.Action(remove) remove: () => Promise<void>;

    doReset() {
      this.remove()
        .then(() => {
          success();
        })
        .catch((err) => {
          error(err);
        });
    }

    doClearDatas() {
      this.clearDatas()
        .then(() => {
          this.showResultOfClearing = true;
        })
        .catch((err) => {
          error(err);
        });
    }

    closeModal() {
      this.showResultOfClearing = false;
    }

  }
</script>
<style lang="stylus" scoped>
  @import "../../css/pages/reset.styl"
</style>
