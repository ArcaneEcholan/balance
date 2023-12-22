<template>
    <div class="shadow overflow-hidden br15">
        <slot></slot>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
@Component({})
export default class PanelComponent extends Vue {}
</script>
<style lang="scss" scoped>
@import '~@/style/common-style';
@import '~@/style/style-specification';
</style>
