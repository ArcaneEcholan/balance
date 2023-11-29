<template>
    <modal-presentation @close-300="onclose">
        <div class="page">
            <gap-component :value="'32px'"></gap-component>
            <div class="record-header">Edit Fields</div>
            <van-cell-group class="shadow overflow-hidden br8 ">
                <van-field v-model="categoryValue" type="string" label="category"/>
                <van-field v-model="amount" type="number" label="amount"/>
                <van-field v-model="datetime" type="text" label="datetime"/>
                <van-field v-model="count" type="digit" label="count"/>
                <van-field v-model="description" type="text" label="description"/>
            </van-cell-group>
            <gap-component></gap-component>
            <common-button :disabled="!submitEnable" @click="submit">
                <template #default>Submit</template>
            </common-button>
        </div>
    </modal-presentation>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import ModalPresentationView from "@/components/ModalPresentation.vue";
import {Notify} from "vant";
import Client from "@/request/client";
import {countDecimalPlaces, isFloat, isPositiveInteger, unmountComponent} from '@/ts/utils';
import eventBus from "@/ts/EventBus";
import pageConfig from "@/ts/pageConfig";
import CommonButton from "@/views/components/CommonButton.vue";
import GapComponent from "@/views/components/GapComponent.vue";

class FormItem {
    categoryValue: string | null = null;
    amount: number | null = null;
    count: number | null = null;
    description: string | null = null;
}

@Component({
    components: {
        GapComponent,
        CommonButton,
        ModalPresentation: ModalPresentationView
    }
})
export default class EditRecordView extends Vue {

    beforeDestroy() {
        console.log("destroyed")
    }
    onclose() {
        unmountComponent(this, 500)
    }

    categoryValue: string | null = null

    amountFractionNumberValid() {
        let i = isFloat(Number(this.amount));
        let amountValid = true
        if (this.amount == null) {
            return false;
        }
        let amountDecimalPlaces = countDecimalPlaces(this.amount);
        if (amountDecimalPlaces > 2 || !i) {
            amountValid = false
        }
        return amountValid
    }

    typeValid() {
        let categoryValid = true
        let categoryValue = this.categoryValue
        if (categoryValue == null || categoryValue.trim() == "") {
            categoryValid = false
        }
        return categoryValue
    }

    checkValidationOfData() {

        if (this.amount == null || this.count == null) {
            Notify({
                type: "danger",
                message: "Information incomplete"
            })
            this.submitEnable = true
            throw new Error("Information format invalid")
        }


        let countValid = isPositiveInteger(Number(this.count));
        let amountValid = this.amountFractionNumberValid()
        let categoryValid = this.typeValid()

        if (!countValid ||
            !amountValid ||
            !categoryValid) {
            this.submitEnable = true
            throw new Error("Information format invalid")
        }

    }

    normalizeDateTime() {
        let datetime = this.datetime

        if (datetime != null) {
            datetime = datetime.trim()
            const datetimeRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/;

            if (!datetimeRegex.test(datetime)) {
                Notify({
                    type: "danger",
                    message: "Information format invalid"
                })
                this.submitEnable = true
                return
            }
        }
        this.datetime = datetime

    }

    submit() {
        this.submitEnable = false

        this.normalizeDateTime()
        let description = this.description

        try {
            this.checkValidationOfData()
        } catch (e) {
            Notify({
                type: "danger",
                message: "Information format invalid"
            })
        }


        // format check pass
        Client.updateTransaction(this.recordId,
            this.categoryValue!,
            this.amount!, this.datetime, this.count!, description)
            .then((resp: any) => {
                let newTrans = resp.data
                this.categoryValue = newTrans.categoryValue
                this.amount = newTrans.amount
                this.datetime = newTrans.datetime
                this.count = newTrans.count
                this.description = newTrans.description
                eventBus.$emit("on-transaction-updated", newTrans)
                Notify({
                    type: "success",
                    message: "Update success"
                })
                this.submitEnable = true
            })
            .catch((err: any) => {
                this.submitEnable = true
            })
    }


    recordId: number | string | null = null

    submitEnable: boolean = true

    amount: string | null = null
    datetime: string | null = null
    count: string | null = null
    description: string | null = null


    modal!: ModalPresentationView

    mounted() {
        // @ts-ignore
        this.recordId = this.$mountProp.id// @ts-ignore
        this.amount = this.$mountProp.amount// @ts-ignore
        this.datetime = this.$mountProp.datetime// @ts-ignore
        this.count = this.$mountProp.count// @ts-ignore
        this.categoryValue = this.$mountProp.categoryValue// @ts-ignore
        this.description = this.$mountProp.description
    }
}
</script>
<style lang='scss' scoped>
@import "~@/style/common-style.scss";

.page {
    padding: 8px;
    background-color: #f7f8fa;
    height: 100%;
}

.record-header {
    padding: 16px 16px 8px;
    color: #969799;
    font-size: 14px;
    line-height: 16px;
}
</style>
