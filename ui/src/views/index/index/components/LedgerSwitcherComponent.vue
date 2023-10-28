<template>
    <div>
        <div>
            <van-button plain type="info" @click="show=true">{{currentLedger.name}}</van-button>
        </div>
        <div class="shadow br8 overflow-hidden">
            <van-action-sheet :closeable="false" v-model="show" title="">
                <van-cell-group>
                    <van-cell @click="onClickSwitchLedger(ledger)" v-for="ledger in ledgerList"
                              :title="ledger.name">
                    </van-cell>
                </van-cell-group>
                <div style="padding: 5px 16px;">
                    <common-button @click="onClickManageLedgerList">
                        <template #default>ledgers</template>
                    </common-button>
                </div>
            </van-action-sheet>
        </div>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import {getRef} from "@/ts/vueUtils";
import {pushPage} from "@/ts/pageStack";
import Client from "@/request/client";
import eventBus from "@/ts/EventBus";
import CommonButton from "@/views/components/CommonButton.vue";

@Component({
    components: {CommonButton}
})
export default class LedgerSwitcherComponent extends Vue {
    show = false

    ledgersLoading = false

    created() {
        eventBus.$on("on-get-current-ledger-name", (args) => {
            return this.currentLedger.name
        })

        eventBus.$on('ledges-changes', (list) => {
            this.ledgerList = list
        })

        eventBus.$on('ledger-deleted', (id) => {
            this.ledgerList = this.ledgerList.filter((item: any) => {
                return item.id !== id
            })
            if (this.currentLedger.id == id) {
                if (this.ledgerList.length != 0) {
                    this.currentLedger = this.ledgerList[0]
                } else {
                    // this is undefined behavior
                    this.currentLedger = {id: null, name: "unknown"}
                }
            }
        })

        this.ledgersLoading = true
        Client.getLedgerList().then((resp: any) => {
            this.ledgersLoading = false
            this.ledgerList = resp.data
            eventBus.$emit('ledges-changes', this.ledgerList)
        }).catch((err: any) => {
            this.ledgersLoading = false
            console.error(err)
        })
    }

    currentLedger: any = {
        name: 'default'
    }

    ledgerList: any[] = []

    onClickSwitchLedger(ledger: any) {

        this.currentLedger = ledger

        this.toggleLedgerSelection()

        eventBus.$emit("on-cur-ledger-changed", ledger)

        this.$emit("on-switch-ledger", ledger)

        this.$emit("update:currentLedger", ledger)

    }

    onClickManageLedgerList() {
        this.toggleLedgerSelection()
        this.present(`manage_ledger`, {})
    }

    toggleLedgerSelection() {
        if(this.show == true) {
            this.show = false
        } else {
            this.show = false
        }
    }

    present(viewName: string, data: any) {
        pushPage(viewName, data)
        // pushPageWithName(viewName, data)
    }
}
</script>
<style lang='scss' scoped>
@import "~@/style/common-style.scss";
</style>
