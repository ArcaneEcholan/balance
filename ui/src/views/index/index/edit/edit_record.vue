<template>
    <div>
        <modal-presentation
            style="background-color: #f7f8fa"
            @closed="closed"
            @close-100="close100"
            @on-close="onclose"
            ref="modal"
        >
            <template #default>
                <div class="page">
                    <div class=" ">
                        <!--<div style="z-index: 10000000">{{ stackSize }}</div>-->
                        <div class="google-gray-400 capitalize">
                            <!--test-->
                        </div>
                    </div>
                    <div class="pdb16 pdt16"></div>
                    <div class="record-header">Edit Fields</div>
                    <van-cell-group class="shadow overflow-hidden br8 ">
                        <van-field v-model="categoryValue" type="string" label="category"/>
                        <van-field v-model="amount" type="number" label="amount"/>
                        <van-field v-model="datetime" type="text" label="datetime"/>
                        <van-field v-model="count" type="digit" label="count"/>
                        <van-field v-model="description" type="text" label="description"/>
                    </van-cell-group>
                    <div class="pdb16 pdt16"></div>
                    <div class=" ">
                        <el-button round plain type="primary" style="width: 100%"
                                   @click="submit"

                                   :disabled="!submitEnable"
                        >Submit
                        </el-button>
                    </div>
                </div>

            </template>
        </modal-presentation>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import ModalPresentationView from "@/components/ModalPresentation.vue";
import {gotoPage, popPage} from "@/ts/pageStack";
import pageStack from "@/ts/pageStack";
import {Notify} from "vant";
import Client from "@/request/client";
import {countDecimalPlaces, isFloat, isPositiveInteger} from '@/ts/utils';
import eventBus from "@/ts/EventBus";
import pageConfig from "@/ts/pageConfig";

class FormItem {
    categoryValue: string | null = null;
    amount: number | null = null;
    count: number | null = null;
    description: string | null = null;
}

// Register the router hooks with their names
// Component.registerHooks([
//     "beforeRouteEnter",
//     "beforeRouteLeave",
//     "beforeRouteUpdate"
// ]);
@Component({
    components: {
        ModalPresentation: ModalPresentationView
    }
})
export default class EditRecordView extends Vue {
    varTable: any = {}

    categoryValue: string | null = null

    submit() {
        this.submitEnable = false

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
        let description = this.description

        let amount = this.amount
        let count = this.count

        // ensure no null
        if (amount == null || count == null) {
            Notify({
                type: "danger",
                message: "Information incomplete"
            })
            this.submitEnable = true
            return
        }


        let categoryValid = true
        let categoryValue = this.categoryValue
        if (categoryValue == null || categoryValue.trim() == "") {
            categoryValid = false
        }

        let countValid = isPositiveInteger(Number(count));
        let amountValid = isFloat(Number(amount));
        let amountDecimalPlaces = countDecimalPlaces(amount);
        if (amountDecimalPlaces > 2) {
            amountValid = false
        }


        if (!countValid || !amountValid ||
            !categoryValid) {
            Notify({
                type: "danger",
                message: "Information format invalid"
            })
            this.submitEnable = true
            return
        }


        // format check pass
        Client.updateTransaction(this.recordId,
            categoryValue!,
            amount, datetime, count, description)
            .then((resp: any) => {
                let newTrans = resp.data
                this.categoryValue = newTrans.categoryValue
                this.amount = newTrans.amount
                this.datetime = newTrans.datetime
                this.count = newTrans.count
                this.description = newTrans.description
                eventBus.$emit("afterTransactionChanged", newTrans)
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

    created() {
    }

    mounted() {
        this.modal = this.$refs.modal as ModalPresentationView;
        setTimeout(() => {
            this.modal.openModal()
        }, 1)

        this.recordId = this.$route.params.id
        this.amount = this.$route.params.amount
        this.datetime = this.$route.params.datetime
        this.count = this.$route.params.count
        this.categoryValue = this.$route.params.categoryValue
        this.description = this.$route.params.description
        //
        // setTimeout(() => {
        //     let recordId: string | number | null = this.$route.query.recordId as number | string | null
        //     this.recordId = recordId
        //     if (recordId != null) {
        //         Client.getTransaction(recordId as number).then(resp => {
        //             this.amount = resp.data.amount
        //             this.datetime = resp.data.datetime
        //             this.count = resp.data.count
        //             this.categoryValue = resp.data.categoryValue
        //             this.description = resp.data.description
        //         })
        //         return
        //     }
        //
        //     Notify("page status invalid")
        //     setTimeout(() => {
        //         Notify.clear()
        //         this.$router.push("/")
        //     }, 1000)
        // }, 500)

    }

    closed() {

    }
    close100() {
        popPage()
    }
    close200() {

    }
    close300() {

    }

    onclose() {
        pageConfig.setTitle("Balance")
    }
}
</script>
<style lang='scss' scoped>
@import "~@/style/common-style.scss";

.page {
    padding: 8px;
    background-color: #f7f8fa;
}

.record-header {
    padding: 16px 16px 8px;
    color: #969799;
    font-size: 14px;
    line-height: 16px;
}
</style>
