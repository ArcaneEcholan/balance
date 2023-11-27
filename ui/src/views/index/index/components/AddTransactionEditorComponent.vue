<template>
    <div>
        <van-action-sheet :closeable="false" v-model="chooseTypePanelShow" title="choose a type">
            <transaction-type-component
                @on-click-one-type="onClickOneType"></transaction-type-component>
        </van-action-sheet>

        <div class="flex column">
            <div class="flex">
                <div class="flexg1">type</div>
                <div class="flexg2">amount</div>
                <div class="flexg2">count</div>
                <div class="flexg2">desc</div>
            </div>


            <div v-for="recordRow in newRecordRows" class="border">
                <van-swipe-cell>
                    <div class="flex new-record-row">
                        <div class="flexg1 cell flex center" style="word-break: break-word"
                             @click="onclickPickTypeBtn(recordRow)">
                            <div>
                                {{
                                    recordRow.type == null || recordRow.type === '' ? ' choose a type' : recordRow.type
                                }}
                            </div>
                        </div>
                        <div :ref="`${recordRow.id}-amount`" class="flexg2 cell">
                            <van-field readonly v-model="recordRow.amount"></van-field>
                        </div>
                        <div :ref="`${recordRow.id}-count`" class="flexg2 cell">
                            <van-field readonly v-model="recordRow.count"></van-field>
                        </div>
                        <div :ref="`${recordRow.id}-desc`" class="flexg2 cell">
                            <van-field placeholder="comment" v-model="recordRow.desc"></van-field>
                        </div>
                    </div>

                    <template #right>
                        <van-button @click="deleteNewRecord(recordRow)" square text="Delete" type="danger"
                                    class="delete-button"/>
                    </template>
                </van-swipe-cell>
            </div>


        </div>

        <gap-component></gap-component>
        <div class="flex pd5" style="border-top: 1px solid #EBECF0;">
            <div @click="addRecordRow"
                 class="flex center"
                 style="
            padding: 6px;
            color: #ffffff;background-color: #588CF3; border-radius: 10px;">
                <i class="ali-international-icon-add-1 fs14 bold"></i>
            </div>
        </div>
        <div class="keyboard" style="height: 200px; width: 100%">
            <div class="flex" style="height: 100%;">
                <div class="flexg3 flex column">
                    <div class="flex flexg1">
                        <div class="flexg1 flex center cell">1</div>
                        <div class="flexg1 flex center cell">2</div>
                        <div class="flexg1 flex center cell">3</div>
                    </div>
                    <div class="flex flexg1">
                        <div class="flexg1 flex center cell">4</div>
                        <div class="flexg1 flex center cell">5</div>
                        <div class="flexg1 flex center cell">6</div>
                    </div>
                    <div class="flex flexg1">
                        <div class="flexg1 flex center cell">7</div>
                        <div class="flexg1 flex center cell">8</div>
                        <div class="flexg1 flex center cell">9</div>
                    </div>
                    <div class="flex flexg1">
                        <div class="flexg1 flex center cell">.</div>
                        <div class="flexg1 flex center cell">0</div>
                        <div class="flexg1 flex center cell delete">
                            <van-icon name="cross"/>
                        </div>
                    </div>
                </div>
                <div class="flexg1 flex column">
                    <div class="flexg1 flex center cell" @click="focusOnPreviousRow">
                        <van-icon name="arrow-up"/>
                    </div>
                    <div class="flex flexg1">
                        <div class="flexg1 flex center cell " @click="focusPrevious()">
                            <van-icon name="arrow-left"/>
                        </div>
                        <div class="flexg1 flex center cell" @click="focusNext()">
                            <van-icon name="arrow"/>
                        </div>
                    </div>
                    <div class="flexg1 flex center cell" @click="focusOnNextRow">
                        <van-icon name="arrow-down"/>
                    </div>
                    <div class="flexg1 flex center cell" @click="onAddTrans">
                        OK
                    </div>
                </div>
            </div>
        </div>

        <div v-show="parsedForms.length > 0" class="record-header">Preview</div>
        <div class="shadow br8 overflow-hidden" style="">
            <template v-for="(form, index) in parsedForms">
                <div :style="
                                 `border-bottom: 1px solid #f5f5f5;
                                 ${index === parsedForms.length - 1 ? 'border-bottom: none; border-bottom-left-radius: 5px;border-bottom-right-radius: 5px;' : ''};
                                 background-color: white;
                                 `"
                     class="flex pd10">
                    <div class="flexg5">
                        <div>
                            <span>{{ form.categoryValue ? form.categoryValue.value : "" }}</span>
                        </div>
                        <div class="fs14">
                            <span class="google-gray-400">{{ form.count ? form.count.value : "" }}</span>
                            <span class="pd5 google-gray-300">|</span>
                            <span class="google-gray-400">{{
                                    form.description ? form.description.value : ""
                                }}</span>
                        </div>
                    </div>
                    <div class="flexg1">
                        <span class="bold">{{ form.amount ? form.amount.value : "" }}</span>
                    </div>
                </div>
            </template>
        </div>

        <gap-component :value="'8px'"></gap-component>

        <!--<common-button @click="onAddTrans">-->
        <!--  <template #default>-->
        <!--    <van-icon name="plus"/>-->
        <!--  </template>-->
        <!--</common-button>-->
    </div>

</template>

<script lang='ts'>

import {Component, Vue} from 'vue-property-decorator';
import VanCursorEditorComponent from "@/views/components/VanCursorEditor.vue";
import {findWordInLine, replace} from "@/ts/utils";
import GapComponent from "@/views/components/GapComponent.vue";
import {Notify} from "vant";
import Client from "@/request/client";
import eventBus from "@/ts/EventBus";
import CommonButton from "@/views/components/CommonButton.vue";
import {provideListeners} from "@/page-eventbus-registration-mixin";
import TransactionTypeComponent from "@/views/index/index/components/TransactionTypeComponent.vue";

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

@Component({components: {CommonButton, VanCursorEditorComponent, GapComponent, TransactionTypeComponent}})
export default class AddTransactionEditorComponent extends Vue {
    chooseTypePanelShow = false

    onClickOneType(type: string) {
        this.chooseTypeRecordRow["type"] = type
        this.chooseTypePanelShow = false
    }

    deleteNewRecord(record: any) {
        let index = this.newRecordRows.findIndex(it => it === record)
        if (index === -1) {
            throw new Error("index === -1")
        }
        this.newRecordRows.splice(index, 1)
        if (this.newRecordRows.length === 0) {
            this.cursor.recordRef = null
            this.cursor.attrName = ""
        } else {
            if (index === this.newRecordRows.length) {
                this.cursor.recordRef = this.newRecordRows[index - 1]
            } else {
                this.cursor.recordRef = this.newRecordRows[index]
            }

            this.$nextTick(() => {
                this.focusOnNewRecordCell(null, "", this.cursor.recordRef, this.cursor.attrName)
            })
        }
    }

    addRecordsCoordinates: any[] = []
    cursor: any = {
        recordRef: null,
        attrName: "",
    }
    attrOrder: any[] = [
        "amount", "count", "desc",
    ]

    focusPrevious() {
        if (this.newRecordRows.length === 0) {
            return;
        }
        let curattr = this.cursor.attrName
        let curRecordRef = this.cursor.recordRef
        let curRecordIndex = this.newRecordRows.findIndex(it => it === curRecordRef)

        let nextattrIndex = 0
        let nextattrName = ""
        {
            let curattrIndex = this.attrOrder.indexOf(curattr)
            {
                if (curattrIndex == -1) {
                    curattrIndex = 0;
                }
            }

            if (curattrIndex == 0) {
                if (curRecordIndex === 0) {
                    nextattrIndex = 0
                } else {
                    nextattrIndex = this.attrOrder.length - 1
                }
            } else {
                nextattrIndex = curattrIndex - 1
            }
            nextattrName = this.attrOrder[nextattrIndex]
            this.cursor.attrName = nextattrName
        }
        if (nextattrIndex === this.attrOrder.length - 1) {
            if (curRecordIndex == -1) {
                throw new Error("curRecordIndex == -1")
            }
            let nextRecordRef
            if (curRecordIndex == 0) {
                nextRecordRef = curRecordRef
            } else {
                nextRecordRef = this.newRecordRows[curRecordIndex - 1]
            }

            this.cursor.recordRef = nextRecordRef
        } else {
            this.cursor.recordRef = curRecordRef
        }

        let nextRecordRef = this.cursor.recordRef

        this.focusOnNewRecordCell(curRecordRef, curattr, nextRecordRef, nextattrName)
    }

    focusOnNewRecordCell(previousRef: any, previousAttrName: string, recordRef: any, attrName: string) {

        if (previousRef != null) {
            // @ts-ignore
            let curattrElem = this.$refs[`${previousRef.id}-${previousAttrName}`][0]
            curattrElem.classList.remove("active-new-record-cell")
        }
        if (recordRef != null) {
            // @ts-ignore
            let nextattrElem = this.$refs[`${recordRef.id}-${attrName}`][0]
            let nextattrInput = (nextattrElem as any).querySelector('input')
            nextattrElem.classList.add("active-new-record-cell")
        }

    }


    focusNext() {
        if (this.newRecordRows.length === 0) {
            return;
        }

        let curattr = this.cursor.attrName
        let curRecordRef = this.cursor.recordRef
        let curRecordIndex = this.newRecordRows.findIndex(it => it === curRecordRef)

        let nextattrIndex = 0
        let nextattrName = ""
        {
            let curattrIndex = this.attrOrder.indexOf(curattr)
            {
                if (curattrIndex == -1) {
                    curattrIndex = 0;
                }
            }

            if (curattrIndex == this.attrOrder.length - 1) {
                if (curRecordIndex === this.newRecordRows.length - 1) {
                    nextattrIndex = this.attrOrder.length - 1
                } else {
                    nextattrIndex = 0
                }
            } else {
                nextattrIndex = curattrIndex + 1
            }
            nextattrName = this.attrOrder[nextattrIndex]
            this.cursor.attrName = nextattrName
        }
        if (nextattrIndex === 0) {
            if (curRecordIndex == -1) {
                throw new Error("curRecordIndex == -1")
            }
            let nextRecordRef
            if (curRecordIndex == this.newRecordRows.length - 1) {
                nextRecordRef = curRecordRef
            } else {
                nextRecordRef = this.newRecordRows[curRecordIndex + 1]
            }

            this.cursor.recordRef = nextRecordRef
        } else {
            this.cursor.recordRef = curRecordRef
        }

        let nextRecordRef = this.cursor.recordRef
        this.focusOnNewRecordCell(curRecordRef, curattr, nextRecordRef, nextattrName)
    }

    focusOnNextRow() {
        if (this.newRecordRows.length === 0) {
            return;
        }
        let curRecordRef = this.cursor.recordRef
        let curAttrName = this.cursor.attrName
        let curRecordIndex = this.newRecordRows.findIndex(it => it === curRecordRef)
        if (curRecordIndex === this.newRecordRows.length - 1) {
            return;
        }
        let nextRecordRef = this.newRecordRows[curRecordIndex + 1]
        this.cursor.recordRef = nextRecordRef
        if (this.cursor.attrName == null || this.cursor.attrName == "") {
            this.cursor.attrName = this.attrOrder[0]
        }
        this.focusOnNewRecordCell(curRecordRef, curAttrName, this.cursor.recordRef, this.cursor.attrName)
    }

    focusOnPreviousRow() {
        if (this.newRecordRows.length === 0) {
            return;
        }
        let curRecordRef = this.cursor.recordRef
        let curAttrName = this.cursor.attrName
        let curRecordIndex = this.newRecordRows.findIndex(it => it === curRecordRef)
        if (curRecordIndex === 0) {
            return;
        }
        let nextRecordRef = this.newRecordRows[curRecordIndex - 1]
        this.cursor.recordRef = nextRecordRef
        if (this.cursor.attrName == null || this.cursor.attrName == "") {
            this.cursor.attrName = this.attrOrder[0]
        }
        this.focusOnNewRecordCell(curRecordRef, curAttrName, this.cursor.recordRef, this.cursor.attrName)
    }

    newRecordRows: any[] = []
    chooseTypeRecordRow: any = {}

    onclickPickTypeBtn(recordRow: any) {
        this.chooseTypePanelShow = true
        this.chooseTypeRecordRow = recordRow
    }

    counter = 0

    addRecordRow() {
        let a = {
            id: this.counter++,
            type: "",
            amount: "",
            count: 1,
            desc: "",
        }
        this.newRecordRows.push(a)
        if (this.newRecordRows.length === 1) {
            this.cursor.recordRef = a
            this.cursor.attrName = "amount"
            this.$nextTick(() => {
                this.focusOnNewRecordCell(null, "", a, "amount")
            })
        }
    }

    geoLocation: any = {}

    rawFormatString: string | null = null;

    parsedForms: FormItem[] = []

    curLedger: any = {name: "default"}

    created() {
        provideListeners(this, [
            {
                eventName: "on-click-one-type",
                handler: (type: string) => {
                    this.replaceFirstWord(type)
                }
            },
            {
                eventName: "on-cur-ledger-changed",
                handler: (ledger: any) => {
                    this.curLedger = ledger
                }
            },
            {
                eventName: "on-cur-location-changed",
                handler: (loc: any) => {
                    this.geoLocation = loc
                }
            }
        ])
        let ledgerName = eventBus.$emitWithReturnValue("on-get-current-ledger-name", null)
        this.curLedger = {name: ledgerName}
    }

    mounted() {
        let cs = document.querySelectorAll(".keyboard .cell")
        for (let c of cs) {
            c.addEventListener("touchstart", (e: any) => {
                c.classList.add("active")

            })
            c.addEventListener("touchend", (e: any) => {
                c.classList.remove("active")

                let cursor = this.cursor
                let recordRef = cursor.recordRef
                let attrName = cursor.attrName
                if (recordRef == null) {
                    return;
                }

                // @ts-ignore
                let key = c.innerText
                if (key === "0" || key === "1" || key === "2" || key === "3" || key === "4" || key === "5" || key === "6" || key === "7" || key === "8" || key === "9") {
                    let curValue = recordRef[attrName]
                    if (curValue == null) {
                        curValue = ""
                    }
                    curValue += key
                    recordRef[attrName] = curValue
                } else if (key === ".") {
                    let curValue = recordRef[attrName]
                    if (curValue == null) {
                        curValue = ""
                    }
                    if (curValue.indexOf(".") !== -1) {
                        return;
                    }
                    curValue += key
                    recordRef[attrName] = curValue
                } else if (c.classList.contains("delete")) {
                    let curValue = recordRef[attrName]
                    if (curValue == null) {
                        curValue = ""
                    }
                    if (curValue.length === 0) {
                        return;
                    }
                    curValue = curValue.substring(0, curValue.length - 1)
                    recordRef[attrName] = curValue
                }

            })
        }

    }

    onAddTrans() {
        try {
            this.checkAddTransData()
            this.addTransactionsByLedgerName(this.curLedger.name, this.newRecordRows);
        } catch (e: any) {
            Notify({
                message: e.message,
                type: 'danger'
            })
        }


    }

    checkAddTransData() {
        this.doCheckAddTransData()
    }

    doCheckAddTransData() {
        let errorType = ""
        if (this.newRecordRows.length == 0) {
            errorType = "No data to save"
            throw new Error(errorType)
        }
        let error = false
        let parseForms = this.newRecordRows
        parseForms.forEach((item: any) => {
            if (error) {
                throw new Error(errorType)
            }
            let categoryValue = item.type ?? ""
            let amount = item.amount
            let count = item.count
            let description = item.desc

            // ensure no null
            if (amount == null || amount == "" || count == null || count == "") {
                error = true
                errorType = "Information not complete"
                throw new Error(errorType)
            }

            let countValid = this.isPositiveInteger(Number(count));
            let amountValid = this.isFloat(Number(amount));
            let amountDecimalPlaces = this.countDecimalPlaces(amount);
            if (amountDecimalPlaces > 2) {
                amountValid = false
            }

            if (!countValid || !amountValid) {
                error = true
                errorType = "Information format invalid"
                throw new Error(errorType)
            }
        })
        // @ts-ignore
        if (error === true) {
            Notify(
                {
                    message: errorType,
                    type: 'danger'
                }
            )
            throw new Error(errorType)
        }
    }


    isPositiveInteger(number: number): boolean {
        return Number.isInteger(number) && number > 0;
    }

    isFloat(number: number) {
        return Number(number) === number && !Number.isInteger(number) || Number.isInteger(number);
    }

    countDecimalPlaces(number: string) {
        if (number === '') {
            return 0; // Not a valid float string
        }

        const parts = number.split('.');

        if (parts.length === 1) {
            return 0; // No decimal point found
        }

        return parts[1].length; // Return the length of the decimal part
    }

    addTransactionsByLedgerName(ledgerName: string, trans: any[]) {
        let request = this.assembleAddTransactionRequest(trans)

        Client.saveTransactionsByLedgerName(ledgerName, request).then((resp) => {
            Notify({
                message: 'save successfully',
                type: 'success'
            })
            eventBus.$emit("refresh-transaction-list", null)
            this.newRecordRows = []
        }).catch(resp => {
            console.log(resp)
        })
    }

    assembleAddTransactionRequest(trans: any[]): any[] {
        let request = trans.map((tran) => {
            let requestTran = {
                categoryValue: tran.type,
                amount: tran.amount,
                count: tran.count,
                description: tran.desc,
                location: this.geoLocation,
            };

            return requestTran;
        });
        return request
    }

    parseInputStringToObjects() {
        if (this.rawFormatString == null) {
            return;
        }
        // food 44.00 kfc
        // fruit 33.00 watermalon
        let wordsOrder = ['categoryValue', 'amount', 'count', 'description'];

        var lines = this.rawFormatString.split('\n');

        let objs = lines
            .filter((line) => !/^\s*$/.test(line))
            .map((line) => line.trim())
            .map((line) => {
                let words = line.split(' ').filter((word) => word !== '');

                let obj = wordsOrder.reduce(
                    (obj, cur, index) =>
                        Object.assign(obj, {[cur]: words[index]}),
                    {},
                );

                return obj;
            }).map((obj: any) => {
                let objWithViewStatus = {};
                const keys = Object.keys(obj);
                return keys.reduce((objWithViewStatus, key) => {
                    return Object.assign(objWithViewStatus, {
                        [key]: {
                            value: obj[key],
                            isValid: false,
                        },
                    });
                }, objWithViewStatus);
            });
        this.parsedForms = objs as FormItem[];
    }

    replaceFirstWord(type: string) {
        let curStringAtInput = this.rawFormatString ?? ""
        let a = this.$refs.recordInput as VanCursorEditorComponent
        let rowNumber = a.cursorPosition.cursorRow
        let wordRange = findWordInLine(curStringAtInput, rowNumber, 1)

        if (wordRange == null) {
            return;
        }

        curStringAtInput = replace(curStringAtInput, wordRange.start, wordRange.end, type)
        this.rawFormatString = curStringAtInput

        this.parseInputStringToObjects()

        this.updateTextAreaInputCursorPosition(wordRange.start)
    }


    updateTextAreaInputCursorPosition(start: number) {
        let vantinput = (this.$refs.recordInput as VanCursorEditorComponent).$el

        let input = vantinput.querySelector('textarea')
        input!.selectionStart = start
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

.keyboard {
    border-collapse: collapse;

    .cell {
        font-size: 20px;
        border: 1px solid #EBECF0;
        margin: -0.5px;
    }
}

.active {
    background-color: #A8ADBD;
}

.clickable {

}

.new-record-row {
    .cell {
        border: 1px solid black;
        margin: -0.5px;
    }

    .cell.active-new-record-cell {
        border: 2px solid red;
    }
}

</style>

