<template>
    <div>
        <div class="flex column">
            <div v-for="row in metrics">
                <div class="flex">
                    <clickable v-for="type in row"
                         class="flex center shadow br8 flexg1 mg8 pd8"
                         style="line-height: 24px; word-break: break-word;
                                background-color: white;
                        "
                         @click="onClickOneType(type.value)">
                        {{ type.value }}
                    </clickable>
                </div>

            </div>
        </div>


        <div class="flex">
            <clickable class="flex center"
                       style="padding: 6px;
            color: #ffffff;background-color: #588CF3; border-radius: 10px;"
                       @click="onclickAddRecordTypeBtn">

                <i class="ali-international-icon-add-1 fs14 bold"></i>
            </clickable>
        </div>

        <van-action-sheet v-model="addTypeShow">
            <div class="pd20 textcenter">Add record type</div>
        </van-action-sheet>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import Client from "@/request/client";
import Clickable from "@/views/components/Clickable.vue";

@Component({
    components: {Clickable}
})
export default class TransTypeComponent extends Vue {

    metrics: any[] = []

    transactionCategories: any[] = []
    addTypeShow = false

    onclickAddRecordTypeBtn() {
        this.addTypeShow = true
    }

    created() {
        this.initTransactionTypes()
    }

    initTransactionTypes() {
        Client.getTransactionCategories().then(resp => {
            this.transactionCategories = resp.data
            let countInOneRowOfMetrics = 4;
            let metrics = []
            for (let i = 0; i < this.transactionCategories.length; i++) {
                if (i % countInOneRowOfMetrics === 0) {
                    metrics.push([])
                }
                // @ts-ignore
                metrics[metrics.length - 1].push(this.transactionCategories[i])
            }
            console.log(metrics)
            this.metrics = metrics
        })
    }

    onClickOneType(type: string) {
        this.$emit("on-click-one-type", type)
    }

}
</script>
<style lang='scss' scoped>
@import "~@/style/common-style.scss";
@import "~@/style/style-specification";
</style>
