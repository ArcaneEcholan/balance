<template>
    <div>
        <template v-if="clickable">
            <div v-if="$store.getters.os === 'ios'">
                <clickable
                    @click.native="$emit('click', $event)"
                    class="flex center"
                    style="
                        padding: 6px 7px;
                        color: #ffffff;
                        background-color: #588cf3;
                        border-radius: 10px;
                    "
                >
                    <i :class="iconClass"></i>
                </clickable>
            </div>
            <div v-else>
                <clickable
                    @click.native="$emit('click', $event)"
                    class="flex center"
                    style="
                        padding: 6px 6px;
                        color: #ffffff;
                        background-color: #588cf3;
                        border-radius: 10px;
                    "
                >
                    <i :class="iconClass"></i>
                </clickable>
            </div>
        </template>
        <template v-else>
            <div v-if="$store.getters.os === 'ios'">
                <div
                    @click.native="$emit('click', $event)"
                    class="flex center"
                    style="
                        padding: 6px 7px;
                        color: #ffffff;
                        background-color: #588cf3;
                        border-radius: 10px;
                    "
                >
                    <i :class="iconClass"></i>
                </div>
            </div>
            <div v-else>
                <div
                    @click.native="$emit('click', $event)"
                    class="flex center"
                    style="
                        padding: 6px 6px;
                        color: #ffffff;
                        background-color: #588cf3;
                        border-radius: 10px;
                    "
                >
                    <i :class="iconClass"></i>
                </div>
            </div>
        </template>
    </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Clickable from '@/views/components/Clickable.vue';

@Component({
    components: { Clickable },
})
export default class SolidIconComponent extends Vue {
    @Prop({ required: true, default: '' }) iconClass!: string;
    @Prop({ required: false, default: true }) clickable!: boolean;
}
</script>
<style lang="scss" scoped>
@import '~@/style/common-style.scss';
</style>
