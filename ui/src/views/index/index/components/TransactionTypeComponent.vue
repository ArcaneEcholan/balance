<template>
    <div>
        <div class="flex column">
            <div v-for="row in metrics">
                <div class="flex">
                    <clickable
                        v-for="type in row"
                        class="flex center shadow br8 flexg1 mg8 pd8"
                        style="
                            line-height: 24px;
                            word-break: break-word;
                            background-color: white;
                        "
                        @click="onClickOneType(type.value)"
                    >
                        {{ type.value }}
                    </clickable>
                </div>
            </div>
        </div>

        <div class="flex">
            <solid-icon
                icon-class="cw-icon-add-fat"
                @click="onclickAddRecordTypeBtn"
            ></solid-icon>
        </div>

        <van-action-sheet v-model="addTypeShow">
            <div class="action-sheet-title">
                <div style="height: 100%; width: 100%" class="flex">
                    <div class="flexg1"></div>
                    <div class="flexg1 flex center">Add record type</div>
                    <div
                        class="flexg1"
                        style="text-align: right; padding-right: 10px"
                    >
                        {{ $t('save') }}
                    </div>
                </div>
            </div>
            <div class="action-sheet-body">
                <panel>
                    <van-field
                        v-model="name"
                        placeholder="name"
                        label="name"
                    ></van-field>
                </panel>

                icon:
                <div
                    class="flex"
                    style="justify-content: start; flex-wrap: wrap"
                >
                    <div v-for="i in 10" class="type-icon">
                        <div
                            class="flex center overflow-hidden"
                            style="
                                border-radius: 100px;
                                width: 100%;
                                height: 100%;
                            "
                        >
                            <i
                                class="ali-international-icon-account"
                                style="font-size: 30px"
                            ></i>
                        </div>
                    </div>
                </div>
            </div>
        </van-action-sheet>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Client from '@/request/client';
import Clickable from '@/views/components/Clickable.vue';
import Panel from '@/components/Panel.vue';
import SolidIcon from '@/views/components/SolidIcon.vue';
import CustomButton from '@/components/CustomButton.vue';

@Component({
    components: { CustomButton, SolidIcon, Panel, Clickable },
})
export default class TransTypeComponent extends Vue {
    name = '';
    metrics: any[] = [];

    transactionCategories: any[] = [];
    addTypeShow = false;

    onclickAddRecordTypeBtn() {
        this.addTypeShow = true;
    }

    created() {
        this.initTransactionTypes();
    }

    initTransactionTypes() {
        Client.getTransactionCategories().then((resp) => {
            this.transactionCategories = resp.data;
            let countInOneRowOfMetrics = 4;
            let metrics = [];
            for (let i = 0; i < this.transactionCategories.length; i++) {
                if (i % countInOneRowOfMetrics === 0) {
                    metrics.push([]);
                }
                // @ts-ignore
                metrics[metrics.length - 1].push(this.transactionCategories[i]);
            }
            console.log(metrics);
            this.metrics = metrics;
        });
    }

    onClickOneType(type: string) {
        this.$emit('on-click-one-type', type);
    }
}
</script>
<style lang="scss" scoped>
@import '~@/style/common-style.scss';
@import '~@/style/style-specification';

.type-icon {
    width: 71.67px;
    height: 71.67px;
    box-sizing: border-box;
    margin: 10px 10px;
    background-color: white;
    border-radius: 100px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
</style>
