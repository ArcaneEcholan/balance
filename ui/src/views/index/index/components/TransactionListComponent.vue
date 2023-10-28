<template>
    <div style="" class="shadow br8 overflow-hidden">
        <div class="record-header" v-show="transactionList.length > 0">This month</div>

        <!--list-->
        <template v-for="(form, index) in transactionList">
            <!--list item-->
            <div class="flex pdt10 pdb10 pdl16 pdr16"
                 @click="toEditTransactionPage(form.id)"
                 :style="
                     `border-bottom: 1px solid #f5f5f5;
                     background-color: white;
                     `
                ">
                <!--card left-->
                <div class="flexg5">
                    <div>
                        <span class="pdr10">{{ form.categoryValue }}</span>
                        <span class="fs14 google-gray-400">test location</span>
                    </div>
                    <div class="fs14">
                        <span class="google-gray-400">{{ form.datetime | formatTimeForRecordItem }}</span>
                        <span class="pd5 google-gray-300">|</span>
                        <span class="google-gray-400">{{ form.description }}</span>
                    </div>
                </div>
                <!--card right-->
                <div class="flexg1">
                            <span class="bold">{{ form.amount }}  {{
                                    `${form.count > 1 ? 'x ' + form.count : ''}`
                                }}</span>
                </div>
            </div>
        </template>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import {convertToShortDateTime} from "@/ts/utils";
import {pushPage} from "@/ts/pageStack";
import Client from "@/request/client";
import {getCurrentYearAndMonth} from "@/ts/time";
import eventBus from "@/ts/EventBus";
import {Notify} from "vant";
import {provideListeners} from "@/page-eventbus-registration-mixin";

@Component({
    filters: {
        formatTimeForRecordItem: function (timeString: string | null) {
            if (!timeString) {
                return 'unknown datetime'
            }
            return convertToShortDateTime(timeString);
        }
    }
})
export default class TransactionListComponent extends Vue {

    transactionList: any[] = []

    created() {
        provideListeners(this, [
            {
                eventName: 'on-transaction-updated',
                handler: (newTransaction: any) => this.updateTransaction(newTransaction)

            },
            {
                eventName: 'on-cur-ledger-changed',
                handler: () => this.onRefreshTransactionList()
            },
            {
                eventName: 'on-cur-date-changed',
                handler: () => this.onRefreshTransactionList()
            },
            {
                eventName: 'refresh-transaction-list',
                handler: () => this.onRefreshTransactionList()
            }
        ])
        this.initTransactions()
    }

    onRefreshTransactionList() {
        try {
            this.doRefreshTransactionList()
        } catch (e) {
            console.log(e)
        }
    }

    doRefreshTransactionList() {
        let currentLedgerName = this.getCurrentLedgerName()
        let currentDate = this.getCurrentDate()
        this.getAndUpdateTransListByLedgerNameAndMonth(currentLedgerName, currentDate)
    }

    getCurrentDate() {
        let currentDate = eventBus.$emitWithReturnValue('on-get-main-page-cur-date', null)
        if (currentDate == null) {
            Notify({type: 'danger', message: 'current date is null'})
            throw new Error('current date is null')
        }
        return currentDate
    }

    getAndUpdateTransListByLedgerNameAndMonth(ledgerName: any, yearHyphenMonth: string) {
        Client.getTransactionListByLedgerName(ledgerName, yearHyphenMonth).then((res) => {
            this.transactionList = res.data
        }).catch((err) => {
            console.log(err)
        })
    }


    updateTransListByLedger(ledger: any) {
        Client.getTransactionListByLedgerName(ledger.name, this.nowadays()).then((res) => {
            this.transactionList = res.data
        }).catch((err) => {
            console.log(err)
        })
    }

    updateTransaction(newTransaction: any) {
        let found = this.transactionList.find((item) => {
            return item.id === newTransaction.id
        })
        if (found) {
            found.amount = newTransaction.amount
            found.categoryValue = newTransaction.categoryValue
            found.count = newTransaction.count
            found.datetime = newTransaction.datetime
            found.description = newTransaction.description
        }
    }

    initTransactions() {
        Client.getTransactionListByLedgerName("default", this.nowadays())
            .then(resp => {
                this.transactionList = resp.data
            })
            .catch((err: any) => {
                console.error(err)
            })
    }

    nowadays(): string {
        return getCurrentYearAndMonth()
    }

    toEditTransactionPage(recordId: string | number) {
        // find the record
        let foundTrans = this.transactionList.find((item) => {
            return item.id === recordId
        })

        if (!foundTrans) {
            return
        }

        // this.present(`/index/edit?recordId=${recordId}`)
        this.present(`edit_transaction`, {
            id: foundTrans.id,
            amount: foundTrans.amount,
            datetime: foundTrans.datetime,
            count: foundTrans.count,
            categoryValue: foundTrans.categoryValue,
            description: foundTrans.description
        })
    }

    present(viewName: string, data: any) {
        pushPage(viewName, data)
        // pushPageWithName(viewName, data)
    }

    getCurrentLedgerName(): string {
        let currentLedgerName =
            eventBus.$emitWithReturnValue('on-get-current-ledger-name', null)
        if (currentLedgerName == null) {
            Notify({type: 'danger', message: 'current ledger is null'})
            throw new Error('current ledger is null')
        }
        return currentLedgerName;
    }


}
</script>
<style lang='scss' scoped>
@import "~@/style/common-style.scss";

.record-header {
    padding: 16px 16px 8px;
    color: #969799;
    font-size: 14px;
    line-height: 16px;
}
</style>
