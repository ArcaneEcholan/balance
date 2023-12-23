<template>
    <button :class="`confirm-btn ${type}`" @click="onclick">
        <slot></slot>
    </button>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
@Component({})
export default class CommonButtonComponent extends Vue {
    // support: inline, the default style sets width to 100%;
    @Prop({ default: '' })
    type!: string;

    onclick($event: any) {
        this.$emit('click', $event);
    }
}
</script>
<style lang="scss" scoped>
.confirm-btn {
    background: none;
    border: none;
    outline: none;
    box-shadow: none;

    width: 100%;

    border-radius: 10px;
    padding: 10px 18px;
    background-color: #3574f0;

    font-size: 16px;
    font-weight: 600;
    color: white;

    &.inline {
        width: auto;
        padding: 4px 8px;
        font-size: 14px;
        border-radius: 20px;
    }
}
</style>
