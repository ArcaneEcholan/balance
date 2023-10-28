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
                <div ref="page-main-frame" style="position: relative; height: 100%;">

                    <div class="page" style="overflow-y: scroll;" ref="page-main-area">
                        <van-action-sheet v-model="show" title="Title">
                            <div class="page">
                                <gap-component :value="'32px'"></gap-component>
                                <div class="record-header">Edit Fields</div>
                                <van-cell-group class="shadow overflow-hidden br8 ">
                                    <van-field v-model="editLedgerName" type="text" label="name"/>
                                </van-cell-group>
                                <gap-component></gap-component>
                                <common-button @click="submitEditLedger"
                                               :disabled="editLedgerLoading">
                                    <template #default>Submit</template>
                                </common-button>
                            </div>
                        </van-action-sheet>

                        <van-action-sheet v-model="addLedgerShow" title="Title">
                            <div class="page">
                                <gap-component :value="'32px'"></gap-component>
                                <div class="record-header">Add Fields</div>
                                <van-cell-group class="shadow overflow-hidden br8 ">
                                    <van-field v-model="addLedgerName" type="text" label="name"/>
                                </van-cell-group>
                                <gap-component></gap-component>
                                <common-button @click="submitAddLedger"
                                               :disabled="addLedgerLoading">
                                    <template #default>Submit</template>
                                </common-button>
                            </div>
                        </van-action-sheet>

                        <div class="pdb16 pdt16"></div>
                        <div class="record-header">Ledgers</div>
                        <van-cell-group class="shadow overflow-hidden br8">
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


                    <div
                        ref="bottom-tool-bar"
                        style="position: absolute; bottom: 0;left: 0; right: 0; background-color: white; border-top: 1px solid #ebedf0; width: 100%">


                        <van-button style="
                        background-color: #1989fa;
                        color: white; border: 1px solid #1989fa; border-radius: 5px;"
                                    plain
                                    @click="submitEditLedger"
                                    :disabled="editLedgerLoading"
                                    ref="add-ledger-btn"
                        >
                            <van-icon name="plus"></van-icon>
                        </van-button>
                    </div>
                </div>
            </template>
        </modal-presentation>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import ModalPresentationView from "@/components/ModalPresentation.vue";
import {popPage} from "@/ts/pageStack";
import {Notify} from "vant";
import pageConfig from "@/ts/pageConfig";
import {getHtmlElem, getRef, getVueEl} from "@/ts/vueUtils";
import eventBus from "@/ts/EventBus";
import Client from "@/request/client";
import CommonButton from "@/views/components/CommonButton.vue";
import GapComponent from "@/views/components/GapComponent.vue";

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
        GapComponent,
        CommonButton,
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


    addLedgerLoading = false
    addLedgerShow = false
    addLedgerName: string | null = ""
    countxxx = 100;

    submitAddLedger() {
        let name = this.addLedgerName
        if (name == null || name === "") {
            Notify({
                type: "danger",
                message: "name is empty"
            })
            return
        }
        this.addLedgerLoading = true
        Client.addLedger(name).then((resp) => {
            Notify({
                type: "success",
                message: "Add success"
            })

            this.addLedgerShow = false
            this.ledgers.push({
                id: resp.data.id,
                name: resp.data.name,
                ctime: resp.data.ctime
            })
            // we don't recover the loading status to prevent the user click submit button twice
        }).catch(() => {
            this.addLedgerLoading = false
            Notify({
                type: "danger",
                message: "add failed"
            })
        })

    }

    onClickEdit(ledgerId: number, ledgerName: string) {
        this.editLedgerId = ledgerId
        this.editLedgerName = ledgerName
        this.show = true
    }

    onClickDelete(ledgerId: number, ledgeName: string) {
        Client.deleteLedger(ledgerId).then(() => {
            Notify({
                type: "success",
                message: "Delete success"
            })
            this.ledgers = this.ledgers.filter((item: any) => item.id != ledgerId)
            eventBus.$emit("ledger-deleted", ledgerId)
        }).catch(() => {
            Notify({
                type: "danger",
                message: "Delete failed"
            })
        })
    }

    editLedgerLoading = false

    submitEditLedger() {
        this.editLedgerLoading = true
        setTimeout(() => {
            this.editLedgerLoading = false
            Notify({
                type: "success",
                message: "Update success"
            })

            this.show = false
            this.ledgers.filter((item: any) => {
                if (item.id === this.editLedgerId) {
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
        this.recordId = this.$route.params.id
        this.amount = this.$route.params.amount
        this.datetime = this.$route.params.datetime
        this.count = this.$route.params.count
        this.categoryValue = this.$route.params.categoryValue
        this.description = this.$route.params.description


        this.ledgersLoading = true

        Client.getLedgerList().then((resp) => {
            this.ledgersLoading = false
            this.ledgers = resp.data
            eventBus.$emit('ledges-changes', this.ledgers)
        }).catch(() => {
            this.ledgersLoading = false
        })

    }


    pageRatio = 95

    mounted() {
        this.modal = getRef(this, "modal") as ModalPresentationView;
        setTimeout(() => {
            this.modal.openModal()
        }, 1)

        this.adjustPageHeight()
    }

    adjustPageHeight() {
        let mainframe = getHtmlElem(this, "page-main-frame")

        let pageMainArea = getHtmlElem(this, "page-main-area")
        let bottomToolBar = getHtmlElem(this, "bottom-tool-bar")

        let mainframeHeight = mainframe.clientHeight;
        let pageMainAreaHeight = mainframeHeight * this.pageRatio / 100
        let bottomToolBarHeight = mainframeHeight - pageMainAreaHeight

        pageMainArea.style.height = pageMainAreaHeight + "px"
        bottomToolBar.style.height = bottomToolBarHeight + "px"

        let e = getRef(this, "add-ledger-btn")

        e.style.height = "auto"
        e.style.padding = `${5}px`
        e.style.borderRadius = `${8}px`
        e.style.position = "relative"

        console.log(e.offsetHeight)

        // center the e to the  bottomToolBar
        let bottomToolBarHeight2 = bottomToolBar.offsetHeight
        let eHeight = e.offsetHeight
        let eTop = (bottomToolBarHeight2 - eHeight) / 2

        e.style.top = `${eTop}px`

        bottomToolBar.style.paddingLeft = `${8}px`

        e.onclick = () => {
            this.addLedgerName = ""
            this.addLedgerLoading = false;
            this.addLedgerShow = true
        }
    }


    closed() {

    }

    close100() {
        popPage()
    }

    onclose() {
        pageConfig.setTitle("Balance")
    }
}
</script>
<style lang='scss' scoped>
@import "~@/style/common-style.scss";

.page {
    padding: 0 8px 0 8px;
    background-color: #f7f8fa;

}

.record-header {
    padding: 16px 16px 8px;
    color: #969799;
    font-size: 14px;
    line-height: 16px;
}
</style>
