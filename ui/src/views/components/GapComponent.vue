<template>
    <div :style="`margin-top: ${value}`"></div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
@Component({})
export default class GapComponent extends Vue {
    @Prop({ default: '20px' })
    value!: string;
}
</script>
<style lang="scss" scoped>
@import '~@/style/common-style.scss';
</style>
