<template>
    <div>
        <span @click="show=true">{{ this.getCurrentDate() }}</span>
        <van-action-sheet :closeable="false" v-model="show" title="">
            <van-datetime-picker
                @cancel="onCancel"
                @confirm="onPickDate"
                v-model="currentDate"
                title="Choose Year-Month"
                :min-date="minDate"
                :max-date="maxDate"
                :type="columnsType"
            >
                <template #default></template>
            </van-datetime-picker>
        </van-action-sheet>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import eventBus from "@/ts/EventBus";
import {provideListeners} from "@/page-eventbus-registration-mixin";
import CommonButton from "@/views/components/CommonButton.vue";

@Component({
    components: {CommonButton}
})
export default class MainPageCurrentDatePickerComponent extends Vue {
    show = false
    minDate = new Date(2020, 0, 1)
    maxDate = new Date(2100, 0, 1)
    currentDate = new Date()

    columnsType = "year-month"

    created() {
        provideListeners(this, [
            {
                eventName: "on-get-main-page-cur-date",
                handler: () => {
                    return this.getCurrentDate()
                }
            }
        ])
    }

    onPickDate() {
        eventBus.$emit("on-cur-date-changed", this.getCurrentDate())
        this.show = false
    }

    onCancel() {
        this.show = false
    }

    getCurrentDate() {
        return this.currentDate.getFullYear() + "-" + (this.currentDate.getMonth() + 1)
    }
}
</script>
<style lang='scss' scoped>
</style>
