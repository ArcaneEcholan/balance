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

                    <van-action-sheet v-model="show" title="Title">
                        <div class="page">
                            <div class="pdb16 pdt16"></div>
                            <div class="record-header">Edit Fields</div>
                            <van-cell-group class="shadow overflow-hidden br8 ">
                                <van-field v-model="editLedgerName" type="text" label="name"/>
                            </van-cell-group>
                            <div class="pdb16 pdt16"></div>
                            <div class=" ">
                                <el-button round plain type="primary" style="width: 100%"
                                           @click="submitEditLedger"
                                           :disabled="editLedgerLoading"
                                >Submit
                                </el-button>
                            </div>
                        </div>

                    </van-action-sheet>

                    <div class="pdb16 pdt16"></div>
                    <div class="record-header">Ledgers</div>
                    <van-cell-group v-loading="ledgersLoading" class="shadow overflow-hidden br8">
                        <van-swipe-cell v-for="ledger in ledgers">
                            <van-cell :border="false" :title="ledger.name"/>
                            <template #right>
                                <van-button square type="primary" text="Edit"
                                            @click="onClickEdit(ledger.id, ledger.name)"/>
                                <van-button square type="danger" text="Delete"
                                            @click="onClickDelete(ledger.id, ledger.name)"/>
                            </template>
                        </van-swipe-cell>
                    </van-cell-group>
                </div>

            </template>
        </modal-presentation>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import ModalPresentationView from "@/components/ModalPresentation.vue";
import {gotoPage} from "@/ts/pageStack";
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
export default class ManageLedgerView extends Vue {
    ledgers: any = []
    varTable: any = {}
    show = false
    categoryValue: string | null = null
    name = ""
    editLedgerId: number | null = null
    editLedgerName: string | null = ""

    onClickEdit(ledgerId: number, ledgerName: string) {
        this.editLedgerId = ledgerId
        this.editLedgerName = ledgerName
        this.show = true
    }

    onClickDelete(ledgerId: number, ledgeName: string) {
        this.ledgers = this.ledgers.filter((item: any) => item.id != ledgerId)
    }
    editLedgerLoading =false
    submitEditLedger() {
        this.editLedgerLoading = true
        setTimeout(() => {
            this.editLedgerLoading = false
            Notify({
                type: "success",
                message: "Update success"
            })

            this.show=false
            this.ledgers.filter((item: any) =>  {
                if(item.id === this.editLedgerId) {
                    item.name = this.editLedgerName
                }
            })
        }, 500)
    }


    recordId: number | string | null = null

    submitEnable: boolean = true

    amount: string | null = null
    datetime: string | null = null
    count: string | null = null
    description: string | null = null


    modal!: ModalPresentationView

    ledgersLoading = false
    created() {
        this.ledgersLoading = true
        setTimeout(() => {
            this.ledgersLoading = false
            this.ledgers = [
                {
                    id: 1,
                    name: "Ledger 1"
                }, {
                    id: 2,
                    name: "Ledger 2"
                }, {
                    id: 3,
                    name: "Ledger 3"
                }, {
                    id: 4,
                    name: "Ledger 4"
                },
            ]
        }, 500)
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
        gotoPage(true, "home", {})

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
