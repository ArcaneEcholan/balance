<template>
  <div>
    <van-cursor-editor-component
        ref="recordInput"
        :value.sync="rawFormatString"
        @input="parseInputStringToObjects"
    >
    </van-cursor-editor-component>

    <div class="flex column">
      <div class="flex">
        <div class="flexg1">type</div>
        <div class="flexg2">amount</div>
        <div class="flexg2">count</div>
        <div class="flexg2">desc</div>
      </div>
      <div class="flex new-record-row" v-for="recordRow in newRecordRows" style="border: 1px solid black">
        <div class="flexg1 cell"><div @click="onclickPickTypeBtn">pick</div></div>
        <div class="flexg2 cell"><van-field readonly v-model="recordRow.amount"></van-field></div>
        <div class="flexg2 cell"><van-field readonly v-model="recordRow.count"></van-field></div>
        <div class="flexg2 cell"><van-field placeholder="comment" v-model="recordRow.desc"></van-field></div>
      </div>
      <div @click="addRecordRow">+</div>
    </div>

    <gap-component></gap-component>
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
            <div class="flexg1 flex center cell">
              <van-icon name="cross   "/>
            </div>
          </div>
        </div>
        <div class="flexg1 flex column">
          <div class="flexg1 flex center cell">
            <van-icon name="arrow-up"/>
          </div>
          <div class="flex flexg1">
            <div class="flexg1 flex center cell">
              <van-icon name="arrow-left"/>
            </div>
            <div class="flexg1 flex center cell">
              <van-icon name="arrow"/>
            </div>
          </div>
          <div class="flexg1 flex center cell">
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

@Component({components: {CommonButton, VanCursorEditorComponent, GapComponent}})
export default class AddTransactionEditorComponent extends Vue {
  newRecordRows: any[] = []
  onclickPickTypeBtn() {

  }
  addRecordRow() {
    this.newRecordRows.push({
      type: "",
      amount: "",
      count: 1,
      desc: "",
    })
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
    let cs = document.getElementsByClassName("cell")
    for (let c of cs) {
      c.addEventListener("touchstart", (e: any) => {
        c.classList.add("active")
      })
      c.addEventListener("touchend", (e: any) => {
        c.classList.remove("active")
      })
    }

  }

  onAddTrans() {
    this.checkAddTransData()
    this.addTransactionsByLedgerName(this.curLedger.name, this.parsedForms);
  }

  checkAddTransData() {
    try {
      this.doCheckAddTransData()
    } catch (e: any) {
      Notify({
        message: e.message,
        type: 'danger'
      })
    }
  }

  doCheckAddTransData() {
    let errorType = ""
    if (this.parsedForms.length == 0) {
      errorType = "No data to save"
      throw new Error(errorType)
    }
    let error = false
    let parseForms = this.parsedForms
    parseForms.forEach((item: FormItem) => {
      if (error) {
        throw new Error(errorType)
      }
      let categoryValue = item.categoryValue?.value;
      let amount = item.amount?.value;
      let count = item.count?.value;
      let description = item.description?.value;

      // ensure no null
      if (categoryValue == null || amount == null || count == null || description == null) {
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
    })
  }

  assembleAddTransactionRequest(trans: any[]): any[] {
    let request = trans.map((tran) => {
      let requestTran = {
        categoryValue: tran.categoryValue.value,
        amount: tran.amount.value,
        count: tran.count.value,
        description: tran.description.value,
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
  background-color: #f5f5f5;
}

.clickable {

}
.new-record-row {
  .cell {
    border: 1px solid black;
    margin: -0.5px;
  }
}

</style>

