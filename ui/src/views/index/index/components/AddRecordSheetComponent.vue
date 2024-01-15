<template>
    <div>
        <common-action-sheet :visible.sync="chooseTypePanelShow">
            <template #header>
                {{ $t('add_record.choose_type.title') }}
            </template>
            <template #body>
                <transaction-type-component
                    @on-click-one-type="onClickOneType"
                ></transaction-type-component>
            </template>
        </common-action-sheet>
        <div name="new-records-area" class="flex column">
            <div name="new-records-area-header" class="flex mgb15">
                <div class="flexg1 flex center">
                    {{ $t('add_record.type') }}
                </div>
                <div class="flexg2 flex center">
                    {{ $t('add_record.amount') }}
                </div>
                <div class="flexg2 flex center">
                    {{ $t('add_record.count') }}
                </div>
                <div class="flexg2 flex center">
                    {{ $t('add_record.comment') }}
                </div>
            </div>

            <div id="new-records-area-body" class="shadow br8 overflow-hidden">
                <div
                    :key="recordRow.id"
                    :id="formNewRecordRowId(recordRow)"
                    v-for="recordRow in newRecordRows"
                    class="new-record-row"
                >
                    <van-swipe-cell style="height: 100%">
                        <div class="flex" style="height: 100%">
                            <!--type picker-->
                            <clickable
                                class="cell flexg1"
                                style="
                                    height: 100%;
                                    word-break: break-word;
                                    background-color: white;
                                "
                                @click="onclickPickTypeBtn(recordRow)"
                            >
                                <div
                                    class="flex center"
                                    style="height: 100%"
                                    v-if="
                                        recordRow.type == null ||
                                        recordRow.type === ''
                                    "
                                >
                                    <van-icon class="fs24" name="question-o" />
                                </div>
                                <div
                                    class="flex center"
                                    style="
                                        height: 100%;
                                        font-size: 14px;
                                        text-overflow: ellipsis;
                                        -webkit-line-clamp: 2;
                                        overflow: hidden;
                                        -webkit-box-orient: vertical;
                                    "
                                    v-else
                                >
                                    {{ recordRow.type }}
                                </div>
                            </clickable>

                            <!--amount input-->
                            <div
                                @click="onTouchCell(recordRow, 'amount')"
                                :ref="`${recordRow.id}-amount`"
                                class="flexg2 cell"
                            >
                                <van-field
                                    readonly
                                    v-model="recordRow.amount"
                                ></van-field>
                            </div>

                            <!--count input-->
                            <div
                                @click="onTouchCell(recordRow, 'count')"
                                :ref="`${recordRow.id}-count`"
                                class="flexg2 cell"
                            >
                                <van-field
                                    readonly
                                    v-model="recordRow.count"
                                ></van-field>
                            </div>

                            <!--desc input-->
                            <div
                                @click="onTouchCell(recordRow, 'desc')"
                                :ref="`${recordRow.id}-desc`"
                                class="flexg2 cell last"
                            >
                                <van-field v-model="recordRow.desc"></van-field>
                            </div>
                        </div>

                        <template #right>
                            <van-button
                                @click="deleteNewRecord(recordRow)"
                                square
                                text="Delete"
                                type="danger"
                            />
                        </template>
                    </van-swipe-cell>
                </div>
            </div>
        </div>
        <gap-component></gap-component>
        <div class="flex pd5" style="border-top: 1px solid #ebecf0">
            <solid-icon
                :clickable="true"
                icon-class="cw-icon-add-fat"
                @click="addRecordRow"
            ></solid-icon>
        </div>
        <key-board-component
            @key-touched="keyBoardTouched"
        ></key-board-component>
        <gap-component :value="'8px'"></gap-component>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import VanCursorEditorComponent from '@/views/components/VanCursorEditor.vue';
import { timeout } from '@/ts/utils';
import GapComponent from '@/views/components/GapComponent.vue';
import { Notify, Toast } from 'vant';
import Client from '@/ts/request/client';
import eventBus from '@/ts/EventBus';
import CommonButton from '@/views/components/CommonButton.vue';
import { provideListeners } from '@/page-eventbus-registration-mixin';
import TransactionTypeComponent from '@/views/index/index/components/TransactionTypeComponent.vue';
import Clickable from '@/views/components/Clickable.vue';
import SolidIcon from '@/views/components/SolidIcon.vue';
import KeyBoardComponent from '@/views/index/index/components/KeyBoardComponent.vue';
import settings from '@/settings';
import CommonActionSheet from '@/views/components/CommonActionSheet.vue';
import request from '@/ts/request';

class FormItemField {
    value: string | null = null;
    key: string | null = null;
    isValid: string | null = null;
}

class FormItem {
    categoryValue: FormItemField | null = null;
    amount: FormItemField | null = null;
    count: FormItemField | null = null;
    description: FormItemField | null = null;
}

@Component({
    components: {
        CommonActionSheet,
        KeyBoardComponent,
        SolidIcon,
        Clickable,
        CommonButton,
        VanCursorEditorComponent,
        GapComponent,
        TransactionTypeComponent,
    },
})
export default class AddTransactionEditorComponent extends Vue {
    addRecordsLoading = false;
    onTouchCell(recordRow: any, attrName: string) {
        let oldRecordRef = this.cursor.recordRef;
        let oldAttrName = this.cursor.attrName;
        this.cursor.recordRef = recordRow;
        this.cursor.attrName = attrName;
        this.$nextTick(() => {
            this.focusOnNewRecordCell(recordRow, attrName);
        });
    }

    chooseTypePanelShow = false;

    onClickOneType(type: string) {
        this.chooseTypeRecordRow['type'] = type;
        this.chooseTypePanelShow = false;
    }

    formNewRecordRowId(record: any) {
        return `new-record-row-${record.id}`;
    }

    getANewRecordRowDiv(record: any) {
        let div = document.getElementById(this.formNewRecordRowId(record));
        if (div == null) {
            throw new Error('div == null');
        }
        return div;
    }

    getNewRecordsAreaDiv() {
        let area = document.getElementById('new-records-area-body');
        if (area == null) {
            throw new Error('area == null');
        }
        return area;
    }

    increaseElemHeightImmediately(element: HTMLElement, heightDelt: number) {
        let height = element.style.height
            ? Number(element.style.height.replace('px', ''))
            : 0;
        element.style.height = height + heightDelt + 'px';
    }

    setElemHeight(element: HTMLElement, height: number) {
        element.style.height = height + 'px';
    }

    squashDivToExtremelyShort(element: HTMLElement) {
        this.setElemHeight(element, 0.01);
    }

    cursor: any = {
        recordRef: null,
        attrName: '',
    };
    attrOrder: any[] = ['amount', 'count', 'desc'];

    focusPrevious() {
        if (this.newRecordRows.length === 0) {
            return;
        }
        let curattr = this.cursor.attrName;
        let curRecordRef = this.cursor.recordRef;
        let curRecordIndex = this.newRecordRows.findIndex(
            (it) => it === curRecordRef,
        );

        let nextattrIndex = 0;
        let nextattrName = '';
        {
            let curattrIndex = this.attrOrder.indexOf(curattr);
            {
                if (curattrIndex == -1) {
                    curattrIndex = 0;
                }
            }

            if (curattrIndex == 0) {
                if (curRecordIndex === 0) {
                    nextattrIndex = 0;
                } else {
                    nextattrIndex = this.attrOrder.length - 1;
                }
            } else {
                nextattrIndex = curattrIndex - 1;
            }
            nextattrName = this.attrOrder[nextattrIndex];
            this.cursor.attrName = nextattrName;
        }
        if (nextattrIndex === this.attrOrder.length - 1) {
            if (curRecordIndex == -1) {
                throw new Error('curRecordIndex == -1');
            }
            let nextRecordRef;
            if (curRecordIndex == 0) {
                nextRecordRef = curRecordRef;
            } else {
                nextRecordRef = this.newRecordRows[curRecordIndex - 1];
            }

            this.cursor.recordRef = nextRecordRef;
        } else {
            this.cursor.recordRef = curRecordRef;
        }

        let nextRecordRef = this.cursor.recordRef;

        this.focusOnNewRecordCell(nextRecordRef, nextattrName);
    }

    focusOnNewRecordCell(recordRef: any, attrName: string) {
        this.newRecordRows.forEach((it) => {
            this.attrOrder.forEach((attr) => {
                // @ts-ignore
                let attrElem = this.$refs[`${it.id}-${attr}`][0];
                attrElem.classList.remove('active-new-record-cell');
            });
        });
        if (recordRef != null) {
            // @ts-ignore
            let nextattrElem = this.$refs[`${recordRef.id}-${attrName}`][0];
            nextattrElem.classList.add('active-new-record-cell');
        }
    }

    focusNext() {
        if (this.newRecordRows.length === 0) {
            return;
        }

        let curattr = this.cursor.attrName;
        let curRecordRef = this.cursor.recordRef;
        let curRecordIndex = this.newRecordRows.findIndex(
            (it) => it === curRecordRef,
        );

        let nextattrIndex = 0;
        let nextattrName = '';
        {
            let curattrIndex = this.attrOrder.indexOf(curattr);
            {
                if (curattrIndex == -1) {
                    curattrIndex = 0;
                }
            }

            if (curattrIndex == this.attrOrder.length - 1) {
                if (curRecordIndex === this.newRecordRows.length - 1) {
                    nextattrIndex = this.attrOrder.length - 1;
                } else {
                    nextattrIndex = 0;
                }
            } else {
                nextattrIndex = curattrIndex + 1;
            }
            nextattrName = this.attrOrder[nextattrIndex];
            this.cursor.attrName = nextattrName;
        }
        if (nextattrIndex === 0) {
            if (curRecordIndex == -1) {
                throw new Error('curRecordIndex == -1');
            }
            let nextRecordRef;
            if (curRecordIndex == this.newRecordRows.length - 1) {
                nextRecordRef = curRecordRef;
            } else {
                nextRecordRef = this.newRecordRows[curRecordIndex + 1];
            }

            this.cursor.recordRef = nextRecordRef;
        } else {
            this.cursor.recordRef = curRecordRef;
        }

        let nextRecordRef = this.cursor.recordRef;
        this.focusOnNewRecordCell(nextRecordRef, nextattrName);
    }

    focusOnNextRow() {
        if (this.newRecordRows.length === 0) {
            return;
        }
        let curRecordRef = this.cursor.recordRef;
        let curAttrName = this.cursor.attrName;
        let curRecordIndex = this.newRecordRows.findIndex(
            (it) => it === curRecordRef,
        );
        if (curRecordIndex === this.newRecordRows.length - 1) {
            return;
        }
        let nextRecordRef = this.newRecordRows[curRecordIndex + 1];
        this.cursor.recordRef = nextRecordRef;
        if (this.cursor.attrName == null || this.cursor.attrName == '') {
            this.cursor.attrName = this.attrOrder[0];
        }
        this.focusOnNewRecordCell(this.cursor.recordRef, this.cursor.attrName);
    }

    focusOnPreviousRow() {
        if (this.newRecordRows.length === 0) {
            return;
        }
        let curRecordRef = this.cursor.recordRef;
        let curAttrName = this.cursor.attrName;
        let curRecordIndex = this.newRecordRows.findIndex(
            (it) => it === curRecordRef,
        );
        if (curRecordIndex === 0) {
            return;
        }
        let nextRecordRef = this.newRecordRows[curRecordIndex - 1];
        this.cursor.recordRef = nextRecordRef;
        if (this.cursor.attrName == null || this.cursor.attrName == '') {
            this.cursor.attrName = this.attrOrder[0];
        }
        this.focusOnNewRecordCell(this.cursor.recordRef, this.cursor.attrName);
    }

    newRecordRows: any[] = [];
    chooseTypeRecordRow: any = {};

    onclickPickTypeBtn(recordRow: any) {
        this.chooseTypePanelShow = true;
        this.chooseTypeRecordRow = recordRow;
    }

    counter = 0;

    clearingAllRecords = false;

    addRecordRow() {
        if (this.clearingAllRecords) {
            return;
        }

        // add one new record (note: this will take effect in the next tick)
        {
            let newRecord = {
                id: this.counter++,
                type: '',
                amount: '',
                count: 1,
                desc: '',
            };
            this.newRecordRows.push(newRecord);
            if (this.newRecordRows.length === 1) {
                this.cursor.recordRef = newRecord;
                this.cursor.attrName = 'amount';
                this.$nextTick(() => {
                    this.focusOnNewRecordCell(newRecord, 'amount');
                });
            }
        }

        // spare some space for the new record, take effect immediately
        {
            let area = document.getElementById('new-records-area-body');
            if (area == null) {
                throw new Error('area == null');
            }
            this.increaseElemHeightImmediately(area, 46);
        }
    }

    clearAllRecords() {
        this.clearingAllRecords = true;

        // shrink add new records area to 0
        {
            let area = document.getElementById('new-records-area-body');
            if (area == null) {
                throw new Error('area == null');
            }
            area.style.height = 0 + 'px';
        }

        // async do some operations after the shrink animation
        setTimeout(() => {
            this.newRecordRows = [];
            this.clearingAllRecords = false;
            this.addRecordRow();
            this.addRecordsLoading = false;
            Toast.clear();
        }, settings.animation.duration);
    }

    deleteNewRecord(record: any) {
        let div = this.getANewRecordRowDiv(record);
        this.squashDivToExtremelyShort(div);

        let area = this.getNewRecordsAreaDiv();
        this.increaseElemHeightImmediately(area, -46);

        timeout(settings.animation.duration).then(() => {
            let curIndex = this.newRecordRows.findIndex(
                (it) => it === this.cursor.recordRef,
            );
            let index = this.newRecordRows.findIndex((it) => it === record);
            if (index === -1) {
                throw new Error('index === -1');
            }
            this.newRecordRows.splice(index, 1);

            if (this.newRecordRows.length === 0) {
                this.cursor.recordRef = null;
                this.cursor.attrName = '';
                return;
            }

            if (curIndex === index) {
                if (index === this.newRecordRows.length) {
                    this.cursor.recordRef = this.newRecordRows[index - 1];
                } else {
                    this.cursor.recordRef = this.newRecordRows[index];
                }
                this.$nextTick(() => {
                    this.focusOnNewRecordCell(
                        this.cursor.recordRef,
                        this.cursor.attrName,
                    );
                });
            }
        });
    }

    geoLocation: any = {};

    rawFormatString: string | null = null;

    parsedForms: FormItem[] = [];

    curLedger: any = { name: 'default' };

    created() {
        provideListeners(this, [
            {
                eventName: 'on-cur-ledger-changed',
                handler: (ledger: any) => {
                    this.curLedger = ledger;
                },
            },
        ]);
        let ledgerName = eventBus.$emitWithReturnValue(
            'on-get-current-ledger-name',
            null,
        );
        this.curLedger = { name: ledgerName };
    }

    mounted() {
        this.addRecordRow();
    }

    keyBoardTouched(keyObj: any) {
        let cursor = this.cursor;
        let recordRef = cursor.recordRef;
        let attrName = cursor.attrName;
        if (recordRef == null) {
            return;
        }

        let keyType = keyObj.label;
        switch (keyType) {
            case 'number': {
                let curValue = recordRef[attrName];
                if (curValue == null) {
                    curValue = '';
                }
                curValue += keyObj.value;
                recordRef[attrName] = curValue;
                return;
            }

            case 'dot': {
                let curValue = recordRef[attrName];
                if (curValue == null) {
                    curValue = '';
                }
                if (curValue.indexOf('.') !== -1) {
                    return;
                }
                curValue += keyObj.value;
                recordRef[attrName] = curValue;
                return;
            }
            case 'delete': {
                let curValue = recordRef[attrName];
                if (curValue == null) {
                    curValue = '';
                }
                if (curValue.length === 0) {
                    return;
                }
                curValue = curValue.substring(0, curValue.length - 1);
                recordRef[attrName] = curValue;
                return;
            }
            case 'arrow-up':
                this.focusOnPreviousRow();
                return;
            case 'arrow-down':
                this.focusOnNextRow();
                return;
            case 'arrow-left':
                this.focusPrevious();
                return;
            case 'arrow-right':
                this.focusNext();
                return;
            case 'confirm':
                this.onAddTrans();
                return;
        }
    }

    onAddTrans() {
        try {
            this.checkAddTransData();
            this.addTransactionsByLedgerName(
                this.curLedger.name,
                this.newRecordRows,
            );
        } catch (e: any) {
            Notify({
                message: e.message,
                type: 'danger',
            });
        }
    }

    checkAddTransData() {
        if (this.newRecordRows.length == 0) {
            throw new Error('No data to save');
        }
        let parseForms = this.newRecordRows;
        parseForms.forEach((item: any) => {
            let categoryValue = item.type;
            let amount = item.amount;
            let count = item.count;

            if (
                amount == null ||
                amount == '' ||
                count == null ||
                count == '' ||
                categoryValue == null ||
                categoryValue == ''
            ) {
                throw new Error('Information not complete');
            }

            function isPositiveInteger(number: number): boolean {
                return Number.isInteger(number) && number > 0;
            }

            function isFloat(number: number) {
                return (
                    (Number(number) === number && !Number.isInteger(number)) ||
                    Number.isInteger(number)
                );
            }

            function countDecimalPlaces(number: string) {
                if (number === '') {
                    return 0; // Not a valid float string
                }

                const parts = number.split('.');

                if (parts.length === 1) {
                    return 0; // No decimal point found
                }

                return parts[1].length; // Return the length of the decimal part
            }

            let countValid = isPositiveInteger(Number(count));
            let amountValid = isFloat(Number(amount));
            let amountDecimalPlaces = countDecimalPlaces(amount);
            if (amountDecimalPlaces > 2) {
                amountValid = false;
            }

            if (!countValid || !amountValid) {
                throw new Error('Information format invalid');
            }
        });
    }

    // assemble Add Transaction Request
    addTransactionsByLedgerName(ledgerName: string, trans: any[]) {
        this.addRecordsLoading = true;
        Toast.loading({
            message: 'Loading...',
            forbidClick: true,
            duration: 0,
        });
        let loc = eventBus.$emitWithReturnValue('on-get-cur-location', null);
        if (loc == null) {
            loc = {
                latitude: null,
                longitude: null,
                formattedName: null,
            };
        }
        let requestTranList = trans.map((tran) => {
            return {
                categoryValue: tran.type,
                amount: tran.amount,
                count: tran.count,
                description: tran.desc,
                location: loc,
            };
        });

        request({
            url: `/transactions`,
            method: 'post',
            data: {
                transactionList: requestTranList,
                ledger_name: ledgerName,
            },
        })
            .then((resp) => {
                Notify({
                    message: 'save successfully',
                    type: 'success',
                });
                eventBus.$emit('refresh-transaction-list', null);

                this.clearAllRecords();
            })
            .catch((resp) => {
                Toast.clear();
                this.addRecordsLoading = false;
                console.log(resp);
            });
    }
}
</script>
<style lang="scss">
@import '~@/style/common-style.scss';
@import '~@/style/style-specification.scss';

.record-header {
    padding: 16px 16px 8px;
    color: #969799;
    font-size: 14px;
    line-height: 16px;
}

@keyframes upDown {
    0% {
        transform: translateY(-100%);
    }

    100% {
        transform: translateY(0%);
    }
}

#new-records-area-body {
    height: 0px;
    transition: all var(--transition-duration) var(--transition-easing);

    .new-record-row {
        animation: upDown var(--transition-duration) var(--transition-easing);
        height: 46px;
        transition: height var(--transition-duration) var(--transition-easing);
        overflow: hidden;

        .cell {
            border-bottom: 3px solid #ffffff;
            margin: -0.5px;
            border-right: 2px solid $separator-color;

            &.last {
                border-right: none;
            }

            &.active-new-record-cell {
                border-bottom: 3px solid #3369d6;
            }
        }
    }
}

.van-swipe-cell__right {
    button {
        height: 100%;
    }
}

.van-swipe-cell__wrapper {
    height: 100%;
}
</style>
