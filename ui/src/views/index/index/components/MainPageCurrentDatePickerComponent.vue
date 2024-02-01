<template>
    <div>
        <span @click="show = true" class="fs16">
            {{ this.getCurrentDate() }}
        </span>
        <common-action-sheet
            :fit-content="true"
            :plane-content="true"
            :visible.sync="show"
        >
            <template #default>
                <van-datetime-picker
                    @cancel="onCancel"
                    @confirm="onPickDate"
                    v-model="currentDate"
                    :title="$t('date_time_picker_title')"
                    :min-date="minDate"
                    :max-date="maxDate"
                    :type="columnsType"
                >
                    <template #default></template>
                </van-datetime-picker>
            </template>
        </common-action-sheet>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import eventBus from '@/ts/EventBus';
import { provideListeners } from '@/page-eventbus-registration-mixin';
import CommonButton from '@/views/components/CommonButton.vue';
import AddRecordSheetComponent from '@/views/index/index/components/AddRecordSheetComponent.vue';
import CommonActionSheet from '@/views/components/CommonActionSheet.vue';
import { getYearAndMonthAsString } from '@/ts/time';

@Component({
    components: { CommonActionSheet, AddRecordSheetComponent, CommonButton },
})
export default class MainPageCurrentDatePickerComponent extends Vue {
    show = false;
    minDate = new Date(2020, 0, 1);
    maxDate = new Date(2100, 0, 1);
    currentDate = new Date();

    columnsType = 'year-month';

    created() {
        provideListeners(this, [
            {
                eventName: 'on-get-main-page-cur-date',
                handler: () => {
                    return this.getCurrentDate();
                },
            },
        ]);
    }

    onPickDate() {
        eventBus.$emit('on-cur-date-changed', this.getCurrentDate());
        this.show = false;
    }

    onCancel() {
        this.show = false;
    }

    getCurrentDate() {
        return getYearAndMonthAsString(this.currentDate);
    }
}
</script>
<style lang="scss" scoped></style>
