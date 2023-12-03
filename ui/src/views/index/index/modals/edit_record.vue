<template>
    <modal-presentation
        ref="modal"
        @on-open="modalLifeCycleHooks.onOpen"
        @on-close="modalLifeCycleHooks.onClose"
        @closed="closed"
        @before-swipe="modalLifeCycleHooks.beforeSwipe"
        @swiping="modalLifeCycleHooks.swiping"
        @after-swipe="modalLifeCycleHooks.afterSwipe"
    >
        <div class="page" id="aaa" style="overflow: auto;">
            <div class="modal-title">Edit Record</div>
            <gap-component :value="'55px'"></gap-component>
            <div class="record-header">Edit Fields</div>
            <panel>
                <van-cell-group>
                    <van-field v-model="categoryValue" type="string" label="category"/>
                    <van-field v-model="amount" type="number" label="amount"/>
                    <van-field v-model="datetime" type="text" label="datetime"/>
                    <van-field v-model="count" type="digit" label="count"/>
                    <van-field v-model="description" type="text" label="description"/>
                </van-cell-group>
            </panel>
            <gap-component value="30px"></gap-component>
            <custom-button :disabled="!submitEnable" @click="submit">
                <template #default>Submit</template>
            </custom-button>
        </div>
    </modal-presentation>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import ModalPresentationView from "@/components/ModalPresentation.vue";
import {Notify} from "vant";
import Client from "@/request/client";
import {
    countDecimalPlaces,
    disableBodyScroll,
    enableBodyScroll,
    isFloat,
    isPositiveInteger,
    unmountComponent
} from '@/ts/utils';
import eventBus from "@/ts/EventBus";
import CustomButton from "@/components/CustomButton.vue";
import GapComponent from "@/views/components/GapComponent.vue";
import Panel from "@/components/Panel.vue";
import {getVueEl} from "@/ts/vueUtils";

let that: any;
@Component({
    components: {
        Panel,
        GapComponent,
        CustomButton,
        ModalPresentation: ModalPresentationView
    }
})
export default class EditRecordView extends Vue {
    modalLifeCycleHooks: any


    beforeDestroy() {
        console.log("destroyed")
    }

    closed() {
        unmountComponent(this, 0);
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
        // {
        //     let touchStartPositionX = 0
        //     let fingerPositionX  = 0
        //     let swipeAreaWidth = 30;
        //     // make sure pageConfig is in vue reactive system, so the change of it
        //     // to avoid the listener being created every time touchstart is triggered
        //     let touchMoveListener = (e: any) => {
        //         // use that instead of this, because "this" is not the vue instance,
        //         // but an object of class AppView
        //         fingerPositionX = e.changedTouches[0].clientX;
        //
        //         //  handleEdgeSwipe
        //         {
        //             if (touchStartPositionX <= swipeAreaWidth) {
        //                 if (fingerPositionX - touchStartPositionX > 0) {
        //                     console.debug("prevent default swipe gesture")
        //                     e.preventDefault();
        //                 }
        //             }
        //
        //             if (touchStartPositionX >= window.innerWidth - swipeAreaWidth) {
        //                 if (fingerPositionX - touchStartPositionX < 0) {
        //                     console.debug("prevent default swipe gesture")
        //                     e.preventDefault();
        //                 }
        //             }
        //         }
        //     }
        //
        //     {
        //         let app = getVueEl(this, "modal")
        //
        //         app.addEventListener('touchstart', (e) => {
        //             touchStartPositionX = e.changedTouches[0].clientX
        //
        //             // don't prevent default here, this will prevent the click event from being triggered
        //             // more information should be collected as the user move finger
        //             app.removeEventListener('touchmove', touchMoveListener);
        //             app.addEventListener('touchmove', touchMoveListener);
        //         });
        //     }
        // }

    }
    created() {


        // @ts-ignore
        this.recordId = this.$options.$mountProp.id// @ts-ignore
        this.amount = this.$options.$mountProp.amount// @ts-ignore
        this.datetime = this.$options.$mountProp.datetime// @ts-ignore
        this.count = this.$options.$mountProp.count// @ts-ignore
        this.categoryValue = this.$options.$mountProp.categoryValue// @ts-ignore
        this.description = this.$options.$mountProp.description


        this.modalLifeCycleHooks = {// @ts-ignore
            onOpen: this.$options.$mountProp.modalLifeCycleHooks.onOpen,// @ts-ignore
            beforeSwipe: this.$options.$mountProp.modalLifeCycleHooks.beforeSwipe,// @ts-ignore
            swiping: this.$options.$mountProp.modalLifeCycleHooks.swiping,// @ts-ignore
            afterSwipe: this.$options.$mountProp.modalLifeCycleHooks.afterSwipe,// @ts-ignore
            onClose: this.$options.$mountProp.modalLifeCycleHooks.onClose
        }
    }
}
</script>
<style lang='scss' scoped>
@import "~@/style/common-style.scss";
@import "~@/style/style-specification";


.record-header {
    padding: 16px 16px 8px;
    color: #969799;
    font-size: 14px;
    line-height: 16px;
}
</style>
