<template>
    <div>
        <div
            class="flex"
            style="justify-content: start; flex-wrap: wrap; row-gap: 15px"
        >
            <div
                :key="icon.value"
                v-for="icon in metrics"
                class="type-icon-cell"
                @click="onClickOneType(icon)"
            >
                <div class="flex column center w100p">
                    <div :class="`flex flex-center type-icon`">
                        <i :class="icon.icon" style="font-size: inherit"></i>
                    </div>
                    <div style="height: 4px"></div>
                    <div
                        class="w100p textcenter"
                        style="
                            font-size: 14px;
                            text-overflow: ellipsis;
                            -webkit-line-clamp: 1;
                            overflow: hidden;
                            -webkit-box-orient: vertical;
                        "
                    >
                        {{ icon.value }}
                    </div>
                </div>
            </div>
        </div>

        <gap-component></gap-component>

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
                            <custom-button
                                type="inline"
                                @click="addNewRecordType"
                            >
                                {{ $t('save') }}
                            </custom-button>
                        </div>
                    </div>
                </template>

                <template #body>
                    <div
                        name="type-picker-main"
                        style="height: 100%"
                        class="flex column"
                    >
                        <div name="type-picker-head" class="flex">
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
                        <!--
                        scroll must be put at the most outer cell
                        of in a flex box, or undefined behaviour
                        could happen
                        -->
                        <div
                            name="type-picker-icons"
                            class="flexg1"
                            style="overflow-y: auto"
                        >
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
import request from '@/ts/request';
import { Notify } from 'vant';
import store from '@/ts/store';

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

        const iconMap: any = {
            daily: 'ali-international-icon-daily',
            food: 'ali-international-icon-food_dish',
            fruit: 'ali-international-icon-fruit',
            drinks: 'ali-international-icon-drinks',
            alcohol: 'ali-international-icon-beer',
            transportation: 'ali-international-icon-transport',
            entertainment: 'ali-international-icon-entertainment',
            others: 'ali-international-icon-others',
            water: 'ali-international-icon-water_cup',
            snack: 'ali-international-icon-icecream',
            electronic: 'ali-international-icon-mobile_phone',
            med: 'ali-international-icon-medical_care',
            maintenance: 'ali-international-icon-tools_hardware',
            productivity: 'ali-international-icon-productivity',
            furniture: 'ali-international-icon-furniture',
        };

        for (let iconMapKey in iconMap) {
            this.icons.push({
                id: this.iconIdCounter++,
                value: iconMapKey,
                className: iconMap[iconMapKey],
            });
        }

        this.icons[0].active = true;
        this.pickedIcon = this.icons[0];
    }

    iconIdCounter = 0;

    initTransactionTypes() {
        Client.getTransactionCategories().then((resp) => {
            this.metrics = resp.data;
        });
    }

    onClickOneType(type: any) {
        this.$emit('on-click-one-type', type);
    }

    addNewRecordType() {
        request({
            method: 'post',
            url: '/transaction/category',
            data: {
                name: this.name,
                icon: this.pickedIcon.value,
            },
            headers: {
                'entity-token': store.getters.token,
            },
        })
            .then((resp) => {
                Notify({
                    type: 'success',
                    message: this.$t('success') as any,
                });
                this.initTransactionTypes();
            })
            .catch((err) => {
                debugger;
                Notify({
                    type: 'danger',
                    message: err.response.data.message,
                });
            });
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
        //font-size: 1.2em;
        padding: 12px;
        border-radius: 100px;
        background-color: $icon-bgc;

        &.picked {
            background-color: red;
        }
    }
}
</style>
