<template>
    <van-dropdown-menu>
        <van-dropdown-item :title="currentLedger.name" ref="ledgerSelection">
            <van-cell-group>
                <van-cell @click="onClickSwitchLedger(ledger)" v-for="ledger in ledgerList"
                          :title="ledger.name">
                </van-cell>
            </van-cell-group>
            <div style="padding: 5px 16px;">
                <el-button @click="onClickManageLedgerList" round plain type="primary" style="width: 100%">
                    <i class="el-icon-edit">manage ledger</i>
                </el-button>
            </div>
        </van-dropdown-item>
    </van-dropdown-menu>
</template>

<script lang='ts'>
import {Component, Prop, Vue} from 'vue-property-decorator';
import {getRef} from "@/ts/vueUtils";
import {gotoPage, pushPage} from "@/ts/pageStack";
import Client from "@/request/client";
import eventBus from "@/ts/EventBus";
@Component({})
export default class LedgerSwitcherComponent extends Vue {


    ledgersLoading = false
    created() {

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
        getRef(this, "ledgerSelection").toggle()
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
