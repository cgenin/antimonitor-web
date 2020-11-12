<template>
  <div class="reset-page">
    <q-list>
      <q-item-label header>Suppression des données calculées de nitrite</q-item-label>
      <q-item class="column items-start">
        <p>Cette option permet de supprimer les données calculées sur la base nitrite.</p>
        <q-btn icon="delete_forever" color="secondary" @click="doClearDatas">Données Calculer</q-btn>
      </q-item>
      <q-separator/>
      <q-item-label header>Réimportation des données</q-item-label>
      <q-item class="column items-start">
        <q-banner
          color="error"
          icon="warning"
          type="warning"
          appear class="bg-error">
          Attention ! Lors d'une opération de rejoue des évènements, l'applicatif sera indisponible. <br/>L'action de
          fin n'est
          visible uniquement dans les logs du serveurs.
        </q-banner>
        <q-btn icon="import_export" class="reset-btn" big no-caps color="red" @click="doReset">
          Réimporter l'ensemble des données
        </q-btn>
      </q-item>
    </q-list>
    <q-dialog v-model="showResultOfClearing" class="reset-modal">
      <q-layout view="hHh Lpr fFf" container class="bg-white"
                :style="{minWidth: '50vw', minHeight: '80vh'}">
        <q-header class="bg-primary">
          <q-toolbar>
            <q-toolbar-title>Collection(s) deleted</q-toolbar-title>
            <q-btn flat v-close-popup round dense icon="close"/>
          </q-toolbar>
        </q-header>
        <q-footer slot="footer" class="modal-footer" right inverted>
          <q-btn
            color="primary"
            v-close-popup
            label="Close"
          ></q-btn>
        </q-footer>
        <q-page-container>
          <q-page padding>

            <div class="modal-body">
              <ul class="list">
                <li v-for="coll in deletedCollections" :key="coll.collectionName">
                  <strong>{{ coll.collectionName }}</strong>
                  :{{ coll.size }}
                </li>
              </ul>
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
