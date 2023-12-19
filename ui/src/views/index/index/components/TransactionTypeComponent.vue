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

            <div class="action-sheet-title">{{ $t('add_record_type.title') }}</div>
            <div class="action-sheet-body">

                <panel>
                    <van-field v-model="name" placeholder="name" label="name"></van-field>
                </panel>

                icon:
                <div id="icon-panel" class="flex" style="justify-content: start; flex-wrap: wrap;">
                    <div v-for="icon in icons" class=" type-icon-cell ">
                        <div class="flex flex-center column type-icon">
                            <i :class="icon.className" style="font-size: inherit"></i>
                        </div>
                    </div>
                </div>
            </div>
        </van-action-sheet>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import Client from "@/request/client";
import Clickable from "@/views/components/Clickable.vue";
import Panel from "@/components/Panel.vue";

@Component({
    components: {Panel, Clickable}
})
export default class TransTypeComponent extends Vue {

    name: string = ""
    metrics: any[] = []

    icons:any[] = [

    ]



    transactionCategories: any[] = []
    addTypeShow = false

    onclickAddRecordTypeBtn() {
        this.addTypeShow = true
    }

    created() {
        this.initTransactionTypes()
        this.icons.push(
            {
                className: 'ali-international-icon-home1'
            },
            {
                className: 'ali-international-icon-home1'
            }
        )
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

.type-icon-cell {
    width: 25%;
    box-sizing: border-box;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 15px;
}

.type-icon {
    font-size: 2em;
    padding: 12px;
    border-radius: 100px;
    background-color: $icon-bgc;
}
</style>
