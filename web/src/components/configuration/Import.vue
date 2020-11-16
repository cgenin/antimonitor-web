<template>
  <div class="import-page">
    <div class="q-if row no-wrap items-center relative-position q-input text-primary">
      <div class="uploaders">
        <q-uploader
          color="secondary"
          field-name="file"
          url="/api/apps/upload"
          label="Upload Service REST"
          :filter="checkFileType"
          @rejected="onRejected"
        />

        <q-uploader
          color="secondary"
          field-name="file"
          url="/api/fronts/upload"
          label="Upload Front Application"
          :filter="checkFileType"
          @rejected="onRejected"
        />
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import Vue from 'vue'
import Component from 'vue-class-component';
import {error} from 'src/Toasts';

interface RejectEntry {
  failedPropValidation: string
  file: File
}

@Component
export default class ConfigurationImport extends Vue {
  jsonImport: string = null;

  checkFileType(files: File[]) {
    return files.filter(file => file.type === 'application/json')
  }

  onRejected(rejectedEntries: RejectEntry[]) {
    const msg = rejectedEntries.reduce((acc, r) => {
      if (acc.length === 0) {
        return r.file.name
      }
      return `${acc}, ${r.file.name}`;
    }, '');
    error(null, `Les fichiers n'ont pas pu  être uploader : ${msg}`);
  }
}
</script>
<style scoped>
.import-page {
  margin-top: 2em;
  width: 100%;
}

.import-page .uploaders{
  width: 100%;
  margin:5px;
  display: flex;
  justify-content: space-around;
  align-items: center;
}
</style>
