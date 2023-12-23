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
                :clickable="true"
                icon-class="cw-icon-add-fat"
                @click.native="onclickAddRecordTypeBtn"
            ></solid-icon>
        </div>

        <div name="sheets">
            <!--add new types sheet-->
            <van-action-sheet
                :style="{ height: '80%' }"
                get-container="#vant-sheet-mount-point"
                ref="sheet"
                v-model="addTypeShow"
            >
                <div class="action-sheet-title">
                    <div style="width: 100%" class="flex">
                        <div style="width: 20%"></div>
                        <div style="width: 60%" class="flex center">
                            {{ $t('add_record_type.title') }}
                        </div>
                        <div style="width: 20%">
                            <custom-button>
                                {{ $t('save') }}
                            </custom-button>
                        </div>
                    </div>
                </div>

                <div class="action-sheet-body flex column">
                    <div>
                        <panel>
                            <!--type name-->
                            <van-field
                                v-model="name"
                                placeholder="name"
                                label="name"
                            ></van-field>
                        </panel>
                        <gap-component></gap-component>
                    </div>

                    <div style="overflow: auto">
                        <!--preset type icons-->
                        <div
                            id="icon-panel"
                            class="flex"
                            style="justify-content: start; flex-wrap: wrap"
                        >
                            <div v-for="icon in icons" class="type-icon-cell">
                                <div class="flex flex-center column type-icon">
                                    <i
                                        :class="icon.className"
                                        style="font-size: inherit"
                                    ></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </van-action-sheet>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Client from '@/ts/request/client';
import Clickable from '@/views/components/Clickable.vue';
import Panel from '@/views/components/Panel.vue';
import SolidIcon from '@/views/components/SolidIcon.vue';
import GapComponent from '@/views/components/GapComponent.vue';
import CustomButton from '@/views/components/CustomButton.vue';

@Component({
    components: { CustomButton, GapComponent, SolidIcon, Panel, Clickable },
})
export default class TransTypeComponent extends Vue {
    name = '';
    metrics: any[] = [];

    icons: any[] = [];

    transactionCategories: any[] = [];
    addTypeShow = false;

    onclickAddRecordTypeBtn() {
        this.addTypeShow = true;
    }

    created() {
        this.initTransactionTypes();
        for (let i = 1; i < 30; i++) {
            this.icons.push({
                className: 'ali-international-icon-beer',
            });
        }

        this.icons.push(
            {
                className: 'ali-international-icon-entertainment',
            },
            {
                className: 'ali-international-icon-mobile-phone',
            },
            {
                className: 'ali-international-icon-medical-care',
            },
            {
                className: 'ali-international-icon-furniture',
            },
            {
                className: 'ali-international-icon-tools-hardware',
            },
            {
                className: 'ali-international-icon-ice-cream',
            },
            {
                className: 'ali-international-icon-water_cup',
            },
            {
                className: 'ali-international-icon-others',
            },
            {
                className: 'ali-international-icon-beer',
            },
            {
                className: 'ali-international-icon-drinks',
            },
            {
                className: 'ali-international-icon-fruit',
            },
            {
                className: 'ali-international-icon-food-dish',
            },
            {
                className: 'ali-international-icon-transport',
            },
        );
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

.type-icon-cell {
    width: 20%;
    box-sizing: border-box;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 15px;

    .type-icon {
        font-size: 1.2em;
        padding: 12px;
        border-radius: 100px;
        background-color: $icon-bgc;
    }
}
</style>
