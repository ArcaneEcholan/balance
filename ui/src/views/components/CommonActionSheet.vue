<template>
    <div class="">
        <!--
            - expose open and close
            - value and click-overlay work together as one abstract prop, name it "show"
        -->

        <van-action-sheet
            :get-container="''"
            :style="fitContent ? `` : `height: 80%;`"
            @open="$emit('open')"
            @close="$emit('close')"
            :closeable="false"
            :value="visible"
            @click-overlay="$emit('update:visible', $event)"
        >
            <template v-if="planeContent">
                <slot name="default"></slot>
            </template>
            <template v-else>
                <div style="height: 100%">
                    <div class="action-sheet-title">
                        <slot name="header"></slot>
                    </div>

                    <div class="action-sheet-body">
                        <slot name="body"></slot>
                    </div>
                </div>
            </template>
        </van-action-sheet>
    </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import GapComponent from '@/views/components/GapComponent.vue';
import CustomButton from '@/views/components/CustomButton.vue';

@Component({
    components: { CustomButton, GapComponent },
})
export default class CommonActionSheetComponent extends Vue {
    @Prop({ default: false })
    visible!: boolean;

    @Prop({ default: false })
    planeContent!: boolean;

    @Prop({ default: false })
    fitContent!: boolean;

    @Prop({ default: null })
    mountPoint!: string | null;

    created() {
        console.log(this.planeContent);
    }
}
</script>
<style lang="scss" scoped></style>
