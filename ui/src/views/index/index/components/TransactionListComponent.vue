<template>
    <!--list-->
    <div
        class=""
        id="records-list"
        style="position: relative; padding-top: 16px"
    >
        <common-action-sheet :visible.sync="ledgerPickerShow">
            <template #header>
                <div style="width: 100%" class="flex">
                    <div style="width: 20%"></div>
                    <div style="width: 60%" class="flex center">
                        {{ $t('edit_record.select_ledger.title') }}
                    </div>
                    <div style="width: 20%">
                        <custom-button
                            @click="onClickEditRecordLedgers"
                            type="inline"
                        >
                            {{ $t('save') }}
                        </custom-button>
                    </div>
                </div>
            </template>
            <template #body>
                <van-radio-group v-model="pickedLedgerName">
                    <van-cell-group>
                        <van-cell
                            v-for="ledger in ledgerList"
                            clickable
                            :key="ledger.name"
                            :title="`${ledger.name}`"
                            @click="onSelectLedger(ledger)"
                        >
                            <template #right-icon>
                                <van-radio :name="ledger.name" ref="radios" />
                            </template>
                        </van-cell>
                    </van-cell-group>
                </van-radio-group>
            </template>
        </common-action-sheet>

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
                            class="flex"
                            @click="toEditTransactionPage(record.id)"
                        >
                            <div
                                v-show="selectMode"
                                class="flex center bg-white"
                                style="width: 15%"
                            >
                                <van-checkbox
                                    v-model="record.selected"
                                ></van-checkbox>
                            </div>
                            <div
                                :ref="`record-${record.id}`"
                                :style="`${
                                    selectMode ? 'width: 85%' : 'width: 100%'
                                };`"
                                name="one-record"
                                class="record-row"
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
                                        <span class="pd5 google-gray-300">
                                            |
                                        </span>
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
                    </div>
                </panel>
                <gap-component></gap-component>
            </div>

            <div v-if="selectMode">
                <gap-component :value="`${29 * 3}px`"></gap-component>
            </div>
            <div v-else>
                <gap-component :value="`${29 * 2}px`"></gap-component>
            </div>
        </div>

        <div v-show="selectMode" class="select-tool-bar">
            <div class="flex justify-between w100p h100p">
                <!--<div @click="highlightSelectedRecords">highlight</div>-->
                <div class="flex align-center h100p">
                    <div
                        @click="onClickUpdateLedgerButton"
                        class="mgl8"
                        :class="[updateLedgerButtonActiveClass]"
                    >
                        <i class="ali-international-icon-log"></i>
                    </div>
                </div>
                <div class="flex align-center h100p">
                    <div @click="closeSelectBar" class="button-color mgr8">
                        {{ $t('cancel') }}
                    </div>
                </div>
            </div>
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
import request from '@/ts/request';
import CustomButton from '@/views/components/CustomButton.vue';
import CommonActionSheet from '@/views/components/CommonActionSheet.vue';
import { globalLoadingStart, globalLoadingStop } from '@/ts/view';

@Component({
    components: { CommonActionSheet, CustomButton, GapComponent, Panel },
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
    ledgerPickerShow = false;
    selectMode = false;
    transactionList: any[] = [];
    recordsListByDay: any[] = [];
    recordsListLoading = false;
    ledgerList: any[] = [];
    pickedLedgerName = '';
    onSelectLedger(ledger) {
        this.pickedLedgerName = ledger.name;
    }

    updateLedgerButtonEnable() {
        return this.getSelectedRecords.length > 0;
    }

    get updateLedgerButtonActiveClass() {
        if (this.updateLedgerButtonEnable()) {
            return 'button-color';
        }

        return 'disable-color';
    }

    get getSelectedRecords() {
        return this.flatMapRecordsForSearching().filter((it) => it.selected);
    }

    toggle(index: any) {
        let ledger = this.ledgerList[index];
        if (ledger.checked) {
            return;
        }
        ledger.checked = !ledger.checked;
    }

    onClickEditRecordLedgers() {
        let recordIds = this.getSelectedRecords.map((it) => it.id);

        let ledgerName = this.pickedLedgerName;
        if (ledgerName === this.getCurrentLedgerName()) {
            Notify({
                type: 'success',
                message: this.$t('success') as string,
            });
            return;
        }
        let ledger = this.ledgerList.find((it) => it.name === ledgerName);
        globalLoadingStart();
        request({
            url: '/record/ledgers',
            method: 'put',
            data: {
                record_ids: recordIds,
                ledger_ids: [ledger.id],
            },
        })
            .then((resp) => {
                this.recordsListByDay.forEach((it) => {
                    it.records = it.records.filter((item) => {
                        return !recordIds.includes(item.id);
                    });
                });

                globalLoadingStop();
                Notify({
                    type: 'success',
                    message: this.$t('success') as string,
                });
                this.ledgerPickerShow = false;
                this.closeSelectBar();
            })
            .catch((resp) => {
                globalLoadingStop();
            });
    }
    onClickUpdateLedgerButton() {
        if (!this.updateLedgerButtonEnable()) {
            return;
        }
        globalLoadingStart();
        request({
            url: '/ledgers',
            method: 'get',
        })
            .then((resp: any) => {
                this.ledgerPickerShow = true;
                let ledgerList = resp.data;

                this.pickedLedgerName = this.getCurrentLedgerName();
                this.ledgerList = ledgerList;
                globalLoadingStop();
            })
            .catch((resp) => {
                globalLoadingStop();
            });
    }
    // this function is for test usage
    highlightSelectedRecords() {
        this.flatMapRecordsForSearching().forEach((it) => {
            // @ts-ignore
            let ref = this.$refs[`record-${it.id}`][0];
            if (it.selected) {
                ref.style.backgroundColor = '#f5f5f5';
            } else {
                ref.style.backgroundColor = 'white';
            }
        });
        setTimeout(() => {
            this.flatMapRecordsForSearching().forEach((it) => {
                // @ts-ignore
                let ref = this.$refs[`record-${it.id}`][0];
                ref.style.backgroundColor = 'white';
            });
        }, 500);
    }
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
            {
                eventName: 'on-record-list-select',
                handler: () => {
                    this.selectMode = true;
                },
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
                    // sort records by day
                    let recordsByDay: any = {};
                    res.data.forEach((it: any) => {
                        it.selected = false;
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

    showSelectBar() {
        this.selectMode = true;
    }

    closeSelectBar() {
        this.selectMode = false;
        this.flatMapRecordsForSearching().forEach((it) => {
            it.selected = false;
        });
        eventBus.$emit('on-record-list-select-cancel', null);
    }

    selectOneRecord(recordId: any) {
        let recordRecord = this.flatMapRecordsForSearching().find((item) => {
            return item.id === recordId;
        });

        if (recordRecord) {
            recordRecord.selected = !recordRecord.selected;
        } else {
            throw new Error('record not found');
        }
    }

    toEditTransactionPage(recordId: string | number) {
        if (this.selectMode) {
            this.selectOneRecord(recordId);
            return;
        }
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

.select-tool-bar {
    position: fixed;
    bottom: 58px;
    left: 0;
    right: 0;
    height: 29px;
    background-color: white;
    border-top: 1px solid $separator-color;
}
</style>
