<template>
    <div
        style="touch-action: manipulation"
        :id="tid"
        class="clickable"
        @click="onClick($event)"
    >
        <slot></slot>
    </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { Notify } from 'vant';
import { generateElemUid } from '@/ts/utils';

@Component({})
export default class ClickableComponent extends Vue {
    // @Prop({default: ""}) classNames!: string
    // @Prop({default: ""}) styles!: string

    tid = generateElemUid();

    onClick($event: any) {
        let c = document.getElementById(this.tid)!;
        c.classList.add('active');
        setTimeout(() => {
            c.classList.remove('active');
        }, 100);
        this.$emit('click', $event);
    }
}
</script>

<style lang="scss">
.clickable.active {
    background-color: #a8adbd !important;
}
</style>
