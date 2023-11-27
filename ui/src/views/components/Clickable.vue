<template>
    <div
         class="clickable"
         @touchstart="ontouchStart"
         @touchend="ontouchEnd"
         @click="$emit('click', $event)"
    >
        <slot></slot>
    </div>
</template>

<script lang='ts'>
import {Component, Prop, Vue} from 'vue-property-decorator';

@Component({})
export default class ClickableComponent extends Vue {
    // @Prop({default: ""}) classNames!: string
    // @Prop({default: ""}) styles!: string

    ontouchStart(event: any) {
        let c = event.target.closest(".clickable")
        c.classList.add("active")
    }

    ontouchEnd(event: any) {
        let c = event.target.closest(".clickable")
        c.classList.remove("active")

        this.$emit("touchend", event)
    }
}
</script>

<style lang="scss">
.clickable.active {
    background-color: #A8ADBD !important;
}
</style>
