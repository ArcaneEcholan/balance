<template>
    <div>
        <template v-if="clickable">
            <clickable @click="$emit('click', $event)"
                       class="flex center"
                       style="
            padding: 6px;
            color: #ffffff;
            background-color: #588CF3;
            border-radius: 10px;
         ">
                <i :class="iconClass"></i>
            </clickable>
        </template>
        <template v-else>
            <div @click="$emit('click', $event)" class="flex center"
                 style="
            padding: 6px;
            color: #ffffff;
            background-color: #588CF3;
            border-radius: 10px;
         ">
                <i :class="iconClass"></i>
            </div>
        </template>
    </div>


</template>

<script lang='ts'>
import {Component, Prop, Vue} from 'vue-property-decorator';
import Clickable from "@/views/components/Clickable.vue";

@Component({
    components: {Clickable}
})
export default class SolidIconComponent extends Vue {
    @Prop({required: true, default: ''}) iconClass!: string
    @Prop({required: false, default: true}) clickable!: boolean
}
</script>
<style lang='scss' scoped>
@import "~@/style/common-style.scss";

</style>
