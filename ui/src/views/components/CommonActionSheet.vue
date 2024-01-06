<template>
    <div class="">
        <!--
            - expose open and close
            - value and click-overlay work together as one abstract prop, name it "show"
        -->
        <van-action-sheet
            style="height: 80%"
            @open="$emit('open')"
            @close="$emit('close')"
            :closeable="false"
            :value="visible"
            @click-overlay="$emit('update:visible', $event)"
        >
            <div class="action-sheet-title">
                <slot name="header"></slot>
            </div>

            <div class="action-sheet-body">
                <slot name="body"></slot>
            </div>
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
}
</script>
<style lang="scss" scoped></style>
