<template>
    <!--list-->
    <div
        class=""
        id="records-list"
        style="position: relative; padding-top: 16px"
    >
        <div v-if="recordsListLoading" class="flex center">
            <van-loading></van-loading>
        </div>
        <div v-else>
            <div
                name="all-records-by-day"
                :key="oneDayRecords.date"
                v-for="oneDayRecords in recordsListByDay"
            >
                <div name="records-header" class="record-header">
                    {{ oneDayRecords.date }}
                </div>
                <panel name="records-body">
                    <div
                        name="records-in-one-day"
                        :key="record.id"
                        v-for="record in oneDayRecords.records"
                    >
                        <div
                            name="one-record"
                            class="record-row"
                            @click="toEditTransactionPage(record.id)"
                        >
                            <!--card left-->
                            <div class="flexg5">
                                <div>
                                    <span class="pdr10">
                                        {{
                                            record.categoryValue == null
                                                ? $t('unknown_record_type')
                                                : record.categoryValue
                                        }}
                                    </span>
                                    <span class="fs14 google-gray-400">
                                        {{
                                            record.location == null
                                                ? $t('unset')
                                                : $t(record.location)
                                        }}
                                    </span>
                                </div>
                                <div class="fs14">
                                    <span class="google-gray-400">
                                        {{
                                            record.datetime |
                                                formatTimeForRecordItem
                                        }}
                                    </span>
                                    <span class="pd5 google-gray-300">|</span>
                                    <span class="google-gray-400">
                                        {{ record.description }}
                                    </span>
                                </div>
                            </div>
                            <!--card right-->
                            <div class="flexg1 flex center">
                                <span class="bold fs18">
                                    {{ record.amount }}
                                    {{
                                        `${
                                            record.count > 1
                                                ? 'x ' + record.count
                                                : ''
                                        }`
                                    }}
                                </span>
                            </div>
                        </div>
                    </div>
                </panel>
                <gap-component></gap-component>
            </div>
            <gap-component value="58px"></gap-component>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getTimeOnly } from '@/ts/utils';
import Client from '@/ts/request/client';
import eventBus from '@/ts/EventBus';
import { Notify } from 'vant';
import { provideListeners } from '@/page-eventbus-registration-mixin';
import Panel from '@/views/components/Panel.vue';
import GapComponent from '@/views/components/GapComponent.vue';

@Component({
    components: { GapComponent, Panel },
    filters: {
        formatTimeForRecordItem: function (timeString: string | null) {
            if (!timeString) {
                return 'unknown datetime';
            }
            return getTimeOnly(timeString);
        },
    },
})
export default class TransactionListComponent extends Vue {
    transactionList: any[] = [];
    recordsListByDay: any[] = [];
    recordsListLoading = false;
    created() {
        this.prepareListeners();
        this.onRefreshTransactionList();
    }

    mounted() {
        let height = $('#records-index-header').innerHeight();
        $('#records-list').css('top', height + 'px');
    }

    prepareListeners() {
        provideListeners(this, [
            {
                eventName: 'on-transaction-updated',
                handler: (newTransaction: any) =>
                    this.updateTransaction(newTransaction),
            },
            {
                eventName: 'on-cur-ledger-changed',
                handler: () => this.onRefreshTransactionList(),
            },
            {
                eventName: 'on-cur-date-changed',
                handler: () => this.onRefreshTransactionList(),
            },
            {
                eventName: 'refresh-transaction-list',
                handler: () => this.onRefreshTransactionList(),
            },
        ]);
    }

    flatMapRecordsForSearching() {
        return this.recordsListByDay.flatMap((it: any) => it.records);
    }

    updateTransaction(newTransaction: any) {
        let found = this.flatMapRecordsForSearching().find((item) => {
            return item.id === newTransaction.id;
        });
        if (found) {
            found.amount = newTransaction.amount;
            found.categoryValue = newTransaction.categoryValue;
            found.count = newTransaction.count;
            found.datetime = newTransaction.datetime;
            found.description = newTransaction.description;
        }
    }

    onRefreshTransactionList() {
        try {
            let currentLedgerName = this.getCurrentLedgerName();
            let currentDate = this.getCurrentDate();
            this.recordsListLoading = true;
            Client.getTransactionListByLedgerName(
                currentLedgerName,
                currentDate,
            )
                .then((res) => {
                    this.recordsListLoading = false;
                    console.debug(res);
                    // sort records by day
                    let recordsByDay: any = {};
                    res.data.forEach((it: any) => {
                        console.debug(it.datetime);
                        let date = it.datetime.split(' ')[0];
                        if (!recordsByDay[date]) {
                            recordsByDay[date] = [];
                        }
                        recordsByDay[date].push(it);
                    });

                    console.debug(recordsByDay);

                    // convert map to list for view rendering
                    let resultList: any[] = [];
                    Object.keys(recordsByDay).forEach((it) => {
                        resultList.push({
                            date: it,
                            records: recordsByDay[it],
                        });
                    });
                    console.debug(resultList);

                    this.recordsListByDay = resultList;
                })
                .catch((err) => {
                    this.recordsListLoading = false;
                    console.log(err);
                });
        } catch (e) {
            console.log(e);
        }
    }

    getCurrentLedgerName(): string {
        let currentLedgerName = eventBus.$emitWithReturnValue(
            'on-get-current-ledger-name',
            null,
        );
        if (currentLedgerName == null) {
            Notify({ type: 'danger', message: 'current ledger is null' });
            throw new Error('current ledger is null');
        }
        return currentLedgerName;
    }

    getCurrentDate() {
        let currentDate = eventBus.$emitWithReturnValue(
            'on-get-main-page-cur-date',
            null,
        );
        if (currentDate == null) {
            Notify({ type: 'danger', message: 'current date is null' });
            throw new Error('current date is null');
        }
        return currentDate;
    }

    getAndUpdateTransListByLedgerNameAndMonth(
        ledgerName: any,
        yearHyphenMonth: string,
    ) {}

    toEditTransactionPage(recordId: string | number) {
        // find the record
        let foundTrans = this.flatMapRecordsForSearching().find((item) => {
            return item.id === recordId;
        });

        if (!foundTrans) {
            return;
        }

        eventBus.$emit('on-click-record-item', {
            id: foundTrans.id,
            amount: foundTrans.amount,
            datetime: foundTrans.datetime,
            count: foundTrans.count,
            categoryValue: foundTrans.categoryValue,
            description: foundTrans.description,
        });
    }
}
</script>
<style lang="scss" scoped>
@import '~@/style/common-style.scss';
@import '~@/style/style-specification';
</style>
