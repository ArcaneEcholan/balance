<template>
    <div
        :style="`${disabled ? 'color: #dcdee0;' : ''}`"
        :id="tid"
        class="clickable"
        @click="onClick($event)"
    >
        <slot></slot>
    </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { generateElemUid } from '@/ts/utils';

@Component({
    components: {},
})
export default class ClickableComponent extends Vue {
    @Prop({ default: false }) disabled!: boolean;

    tid = generateElemUid();

    onClick($event: any) {
        if (this.disabled) {
            return;
        }
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
