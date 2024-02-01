<template>
    <modal-presentation
        ref="modal"
        :hooks="modalLifeCycleHooks"
        @on-open="modalLifeCycleHooks.onOpen"
        @on-close="modalLifeCycleHooks.onClose"
        @closed="closed"
        @before-swipe="modalLifeCycleHooks.beforeSwipe"
        @swiping="modalLifeCycleHooks.swiping"
        @after-swipe="modalLifeCycleHooks.afterSwipe"
    >
        <div class="page" style="overflow: auto">
            <div class="modal-title">{{ $t('edit_record.title') }}</div>
            <gap-component :value="'55px'"></gap-component>

            <div class="record-header">Edit Fields</div>
            <panel>
                <van-cell-group>
                    <van-cell
                        clickable
                        :title="$t('edit_record.ledger')"
                        @click="ledgerPickerShow = true"
                    />

                    <common-action-sheet
                        :fit-content="true"
                        :visible.sync="ledgerPickerShow"
                    >
                        <template #header>
                            <div style="width: 100%" class="flex">
                                <div style="width: 20%"></div>
                                <div style="width: 60%" class="flex center">
                                    {{ $t('statistics.ledger_picker.title') }}
                                </div>
                                <div style="width: 20%">
                                    <custom-button
                                        @click="pickLedger"
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
                                            <van-radio
                                                :name="ledger.name"
                                                ref="radios"
                                            />
                                        </template>
                                    </van-cell>
                                </van-cell-group>
                            </van-radio-group>
                        </template>
                    </common-action-sheet>

                    <van-field
                        v-model="categoryValue"
                        type="string"
                        label="category"
                    />
                    <van-field v-model="amount" type="number" label="amount" />
                    <van-field
                        v-model="datetime"
                        type="text"
                        label="datetime"
                    />
                    <van-field v-model="count" type="digit" label="count" />
                    <van-field
                        v-model="description"
                        type="text"
                        label="description"
                    />
                </van-cell-group>
            </panel>
            <gap-component value="30px"></gap-component>
            <custom-button :disabled="!submitEnable" @click="submit">
                <template #default>Submit</template>
            </custom-button>
        </div>
    </modal-presentation>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import ModalPresentationView from '@/views/components/ModalPresentation.vue';
import { Notify } from 'vant';
import Client from '@/ts/request/client';
import {
    countDecimalPlaces,
    isFloat,
    isPositiveInteger,
    stripType,
    unmountComponent,
} from '@/ts/utils';
import eventBus from '@/ts/EventBus';
import CustomButton from '@/views/components/CustomButton.vue';
import GapComponent from '@/views/components/GapComponent.vue';
import Panel from '@/views/components/Panel.vue';
import CommonActionSheet from '@/views/components/CommonActionSheet.vue';
import request from '@/ts/request';
import { globalLoadingStart, globalLoadingStop } from '@/ts/view';
import Cache from '@/ts/cache';
import storage from '@/ts/storage';
import { getYearAndMonthAsString } from '@/ts/time';
@Component({
    components: {
        CommonActionSheet,
        Panel,
        GapComponent,
        CustomButton,
        ModalPresentation: ModalPresentationView,
    },
})
export default class EditRecordView extends Vue {
    modalLifeCycleHooks: any;
    ledgerList: any[] = [];
    pickedLedgerName = '';
    pickedLedger: any = {};
    pickLedger() {
        globalLoadingStart();
        request({
            url: '/record/ledgers',
            method: 'put',
            data: {
                record_ids: [this.recordId],
                ledger_ids: [this.pickedLedger.id],
            },
        })
            .then((resp) => {
                eventBus.$emit('on-transaction-removed', {
                    id: this.recordId,
                });
                Cache.getAllKeys().then((keys: any[]) => {
                    keys.forEach((it) => {
                        if (it.startsWith('transaction_list_')) {
                            Cache.removeItem(it);
                        }
                    });
                });
                globalLoadingStop();
                Notify({
                    type: 'success',
                    message: this.$t('success') as string,
                });
                this.ledgerPickerShow = false;
            })
            .catch((resp) => {
                globalLoadingStop();
            });
    }

    onSelectLedger(ledger) {
        this.pickedLedgerName = ledger.name;
        this.pickedLedger = ledger;
    }

    ledgerPickerShow = false;

    beforeDestroy() {
        console.log('destroyed');
    }

    closed() {
        unmountComponent(this, 0);
    }

    categoryValue: string | null = null;

    amountFractionNumberValid() {
        let i = isFloat(Number(this.amount));
        let amountValid = true;
        if (this.amount == null) {
            return false;
        }
        let amountDecimalPlaces = countDecimalPlaces(this.amount);
        if (amountDecimalPlaces > 2 || !i) {
            amountValid = false;
        }
        return amountValid;
    }

    typeValid() {
        let categoryValid = true;
        let categoryValue = this.categoryValue;
        if (categoryValue == null || categoryValue.trim() == '') {
            categoryValid = false;
        }
        return categoryValid;
    }

    checkValidationOfData() {
        let datetime = this.datetime;

        if (datetime != null) {
            datetime = datetime.trim();
            const datetimeRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/;

            if (!datetimeRegex.test(datetime)) {
                this.submitEnable = true;
                throw new Error(
                    'Datetime Invalid. Valid format: xxxx-xx-xx xx:xx:xx',
                );
            }
        } else {
            this.submitEnable = true;
            throw new Error(
                'Datetime Invalid. Valid format: xxxx-xx-xx xx:xx:xx',
            );
        }

        if (this.amount == null || this.count == null) {
            Notify({
                type: 'danger',
                message: 'Information incomplete',
            });
            this.submitEnable = true;
            throw new Error('Information format invalid');
        }

        let countValid = isPositiveInteger(Number(this.count));
        let amountValid = this.amountFractionNumberValid();
        let categoryValid = this.typeValid();

        if (!countValid || !amountValid || !categoryValid) {
            this.submitEnable = true;
            throw new Error('Information format invalid');
        }
    }

    submit() {
        this.submitEnable = false;

        try {
            this.checkValidationOfData();
        } catch (e) {
            Notify({
                type: 'danger',
                message: 'Information format invalid',
            });
            return;
        }

        // format check pass
        Client.updateTransaction(
            this.recordId,
            this.categoryValue!,
            this.amount!,
            this.datetime,
            this.count!,
            this.description,
        )
            .then((resp: any) => {
                storage.getDefaultLedger().then((ledgerName) => {
                    let month = getYearAndMonthAsString(this.datetime!);

                    storage.purgeRecordsCacheByLedgerNameAndMonth(
                        ledgerName,
                        month,
                    );

                    storage.purgeStatisticsCacheByLedgerName(ledgerName);
                });

                let newTrans = resp.data;
                this.categoryValue = newTrans.categoryValue;
                this.amount = newTrans.amount;
                this.datetime = newTrans.datetime;
                this.count = newTrans.count;
                this.description = newTrans.description;
                eventBus.$emit('on-transaction-updated', newTrans);
                Notify({
                    type: 'success',
                    message: 'Update success',
                });
                this.submitEnable = true;
            })
            .catch((err: any) => {
                console.log(err);
                this.submitEnable = true;
            });
    }

    recordId: number | string | null = null;

    submitEnable = true;

    amount: string | null = null;
    datetime: string | null = null;
    count: string | null = null;
    description: string | null = null;
    ledgerName: string | null = null;
    created() {
        let mountProp = stripType(this.$options).$mountProp;

        this.modalLifeCycleHooks = mountProp.modalLifeCycleHooks;

        this.recordId = mountProp.id;
        this.amount = mountProp.amount;
        this.ledgerName = mountProp.ledgerName;
        this.datetime = mountProp.datetime;
        this.count = mountProp.count;
        this.categoryValue = mountProp.categoryValue;
        this.description = mountProp.description;

        request({
            url: '/ledgers',
            method: 'get',
            params: {
                record_id: this.recordId,
            },
        }).then((resp: any) => {
            this.ledgerList = [
                {
                    id: 1,
                    name: 'est',
                    related: false,
                },
                {
                    id: 2,
                    name: 'dsest',
                    related: true,
                },
            ];
            console.log(resp.data);
            let ledgerList = resp.data;
            ledgerList.forEach((it: any) => {
                it.checked = it.related;
            });

            this.pickedLedger = ledgerList.find(
                (it) => it.name === this.ledgerName,
            );
            this.pickedLedgerName = this.ledgerName!;

            this.ledgerList = ledgerList;
        });
    }
}
</script>
<style lang="scss" scoped>
@import '~@/style/common-style.scss';
@import '~@/style/style-specification';

.record-header {
    padding: 16px 16px 8px;
    color: #969799;
    font-size: 14px;
    line-height: 16px;
}
</style>
