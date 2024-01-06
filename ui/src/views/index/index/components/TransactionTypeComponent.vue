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
            <common-action-sheet
                :mount-point="`#vant-sheet-mount-point`"
                :visible.sync="addTypeShow"
            >
                <template #header>
                    <div style="width: 100%" class="flex">
                        <div style="width: 20%"></div>
                        <div style="width: 60%" class="flex center">
                            {{ $t('add_record_type.title') }}
                        </div>
                        <div style="width: 20%">
                            <custom-button type="inline">
                                {{ $t('save') }}
                            </custom-button>
                        </div>
                    </div>
                </template>

                <template #body>
                    <div class="flex column">
                        <div class="flex">
                            <!--icon picked by user-->
                            <div name="picked-icon" class="type-icon-cell">
                                <div
                                    class="flex flex-center column type-icon picked"
                                >
                                    <i :class="pickedIcon.className"></i>
                                </div>
                            </div>

                            <!--name of new icon-->
                            <panel class="flexg1">
                                <!--type name-->
                                <van-field
                                    v-model="name"
                                    label="name"
                                ></van-field>
                            </panel>
                            <gap-component></gap-component>
                        </div>

                        <gap-component></gap-component>

                        <!--preset type icons-->
                        <div style="overflow: auto">
                            <div
                                id="icon-panel"
                                class="flex"
                                style="
                                    justify-content: start;
                                    flex-wrap: wrap;
                                    row-gap: 15px;
                                "
                            >
                                <div
                                    :key="icon.id"
                                    v-for="icon in icons"
                                    :class="`type-icon-cell`"
                                    @click="pickNewIcon(icon)"
                                >
                                    <div
                                        :class="`flex flex-center column type-icon  ${
                                            icon.active ? 'picked' : ''
                                        }`"
                                    >
                                        <i
                                            :class="icon.className"
                                            style="font-size: inherit"
                                        ></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
            </common-action-sheet>
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
import CommonActionSheet from '@/views/components/CommonActionSheet.vue';

@Component({
    components: {
        CommonActionSheet,
        CustomButton,
        GapComponent,
        SolidIcon,
        Panel,
        Clickable,
    },
})
export default class TransTypeComponent extends Vue {
    name = '';
    metrics: any[] = [];

    icons: any[] = [];

    transactionCategories: any[] = [];
    addTypeShow = false;
    pickedIcon: any = {};

    pickNewIcon(icon: any) {
        this.pickedIcon = icon;
        this.icons.forEach((it) => (it.active = false));
        this.icons.filter((it) => it.id === icon.id)[0].active = true;
    }

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

        this.icons[0].active = true;
        this.icons.forEach((it) => (it.id = this.iconIdCounter++));
        console.log(this.icons);
        this.pickedIcon = this.icons[0];
    }

    iconIdCounter = 0;

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

    & .type-icon {
        font-size: 1.2em;
        padding: 12px;
        border-radius: 100px;
        background-color: $icon-bgc;

        &.picked {
            background-color: red;
        }
    }
}
</style>
