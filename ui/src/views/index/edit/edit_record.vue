<template>
    <div>
        <modal-presentation
            :background-color="'#f7f8fa'"
            @close="closed"
            ref="modal"
        >
            <template #default>
                <div>
                    <el-input type="textarea" v-model="variableVisibleString">
                    </el-input>
                </div>
                <div class="header">
                    <div style="z-index: 10000000">{{ stackSize }}</div>
                    <div class="flex flex-center" style="background-color: #fff;
height: 56px;">
                        <div class="fs20 bold"
                             style="
                         text-transform: capitalize;">
                            Edit Record
                        </div>
                    </div>
                </div>
                <div class="pdl16 pdr16">
                    <div class="google-gray-400 capitalize">
                        <!--test-->
                    </div>
                </div>
                <div class="pdb16 pdt16"></div>
                {{
                    amount
                }}
                {{ datetime }}
                {{ count }}
                {{ description }}

                <van-cell-group inset>
                    <van-field v-model="amount" type="number" label="amount"/>
                    <van-field v-model="datetime" type="text" label="datetime"/>
                    <van-field v-model="count" type="digit" label="count"/>
                    <van-field v-model="description" type="text" label="description"/>
                </van-cell-group>
                <div class="pdb16 pdt16"></div>
                <div class="pdl16 pdr16">
                    <el-button round plain type="primary" style="width: 100%"
                               @click="submit"

                    :disabled="!submitEnable"
                    >Submit
                    </el-button>
                </div>
            </template>
        </modal-presentation>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import ModalPresentationView from "@/components/ModalPresentation.vue";
import {gotoPage, popPage} from "@/ts/pageStack";
import pageStack from "@/ts/pageStack";
import {Notify} from "vant";
import Client from "@/request/client";
import {countDecimalPlaces, isFloat, isPositiveInteger} from '@/ts/utils';
import eventBus from "@/ts/EventBus";

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
        ModalPresentation: ModalPresentationView
    }
})
export default class EditRecordView extends Vue {
    get stackSize() {
        return pageStack.getStackSize()
    }

    varTable: any = {}

    visibleVariable(varName: string, varValue: any) {
        this.varTable[varName] = varValue
        this.renderToString()
    }

    submit() {
        this.submitEnable = false

        let error = false;

        let datetime = this.datetime

        if(datetime != null) {
            datetime = datetime.trim()
            const datetimeRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/;

            if (!datetimeRegex.test(datetime)) {
                Notify({
                    type: "danger",
                    message: "Information format invalid"
                })
                this.submitEnable = true
                return
            }
        }
        let description = this.description

        let amount = this.amount
        let count = this.count

        // ensure no null
        if (amount == null || count == null) {
            Notify({
                type: "danger",
                message: "Information incomplete"
            })
            this.submitEnable = true
            return
        }



        let countValid = isPositiveInteger(Number(count));
        let amountValid = isFloat(Number(amount));
        let amountDecimalPlaces = countDecimalPlaces(amount);
        if (amountDecimalPlaces > 2) {
            amountValid = false
        }

        if (!countValid || !amountValid) {
            Notify({
                type: "danger",
                message: "Information format invalid"
            })
            this.submitEnable = true
            return
        }


        // format check pass
        Client.updateTransaction(this.recordId,
            amount, datetime, count, description)
            .then((resp:any) => {
                let newTrans = resp.data
                eventBus.$emit("afterTransactionChanged", newTrans)
                Notify({
                    type: "success",
                    message: "Update success"
                })
                this.submitEnable = true
            })
            .catch((err: any) => {
                this.submitEnable = true
            })
    }

    renderToString() {
        let keys = Object.keys(this.varTable)
        let result = ""
        for (let i = 0; i < keys.length; i++) {
            let key = keys[i]
            let value = this.varTable[key]
            result += `${key} = ${value}\n`
        }
        this.variableVisibleString = result
    }


    recordId: number | string | null = null

    submitEnable: boolean = true
    variableVisibleString: string | null = null

    amount: string | null = null
    datetime: string | null = null
    count: string | null = null
    description: string | null = null


    modal!: ModalPresentationView

    created() {
    }

    mounted() {
        this.modal = this.$refs.modal as ModalPresentationView;
        setTimeout(() => {
            this.modal.openModal()
        }, 1)

        setTimeout(() => {
            let recordId: string | number | null = this.$route.query.recordId as number | string | null
            this.recordId = recordId
            this.visibleVariable("recordId", recordId)
            if (recordId != null) {
                Client.getTransaction(recordId as number).then(resp => {
                    this.amount = resp.data.amount
                    this.datetime = resp.data.datetime
                    this.count = resp.data.count
                    this.description = resp.data.description
                })
                return
            }

            Notify("page status invalid")
            setTimeout(() => {
                Notify.clear()
                this.$router.push("/")
            }, 1000)
        }, 500)

    }

    closed() {
        gotoPage(true, "/", (routeOpiton: any) => {
            routeOpiton.params =  {
                transactionId: this.recordId,
                amount: this.amount,
                datetime: this.datetime,
                count: this.count,
                description: this.description
            }
        })
    }
}
</script>
<style lang='scss' scoped>
@import "~@/style/common-style.scss";

.page-background {
    background-color: $google-gray-400;
}
</style>
