<template>
    <div style="width: 100%">
        <van-button :disabled="disabled" plain round type="info" style="width:100%"
                    @click="onclick">
            <slot>button</slot>
        </van-button>
    </div>
</template>

<script lang='ts'>
import {Component, Prop, Vue} from 'vue-property-decorator';

@Component({})
export default class CommonButtonComponent extends Vue {


    @Prop({default: false})
    disabled!: boolean

    onclick() {
        this.$emit('click')
    }
}
</script>
<style lang='scss' scoped>
</style>
