<template>
    <div class="page" style="padding-bottom: 66px; overflow: auto">
        <div id="statistics-tab-title" class="tab-title">
            <div class="flex column w100p">
                <div class="w100p flex center">Statistics</div>
                <div class="flex fs14">
                    <div
                        style="border-radius: 100px"
                        class="button-bgc white-color pd4 mgr8"
                        @click="show = true"
                    >
                        {{ $t('statistics.date_picker') }}
                    </div>
                    <div
                        style="border-radius: 100px"
                        class="button-bgc white-color pd4 pdr8"
                        @click="onClickPickLedger"
                    >
                        {{ $t('statistics.ledger_picker.title') }}
                    </div>
                </div>
            </div>
        </div>

        <common-action-sheet
            :visible.sync="show"
            :fit-content="true"
            :plane-content="true"
        >
            <template #default>
                <van-datetime-picker
                    @cancel="onCancel"
                    @confirm="onPickDate"
                    v-model="currentDate"
                    title="Choose Year-Month"
                    :min-date="minDate"
                    :max-date="maxDate"
                    :type="columnsType"
                >
                    <template #default></template>
                </van-datetime-picker>
            </template>
        </common-action-sheet>

        <common-action-sheet :visible.sync="ledgerPickerShow">
            <template #header>
                <div style="width: 100%" class="flex">
                    <div style="width: 20%"></div>
                    <div style="width: 60%" class="flex center">
                        {{ $t('statistics.ledger_picker.title') }}
                    </div>
                    <div style="width: 20%">
                        <custom-button
                            @click="init(pickedLedger.id)"
                            type="inline"
                        >
                            {{ $t('save') }}
                        </custom-button>
                    </div>
                </div>
            </template>
            <template #body>
                <van-radio-group v-model="pickedLedgerName">
                    <van-cell-group>
                        <van-cell
                            v-for="ledger in ledgerList"
                            clickable
                            :key="ledger.name"
                            :title="`${ledger.name}`"
                            @click="onSelectLedger(ledger)"
                        >
                            <template #right-icon>
                                <van-radio :name="ledger.name" ref="radios" />
                            </template>
                        </van-cell>
                    </van-cell-group>
                </van-radio-group>
            </template>
        </common-action-sheet>

        <gap-component :value="`${headerHeight}px`"></gap-component>

        <div class="record-header">
            {{ $t('statistics.sibling_month_compare.title') }}
        </div>

        <panel>
            <div class="flex bg-white pd8">
                <div class="flexg1" id="compare-total-percentage">
                    <div
                        style="width: fit-content"
                        id="compare-total-percentage-text"
                    >
                        <div class="flex">
                            <i
                                id="percent-arrow"
                                class="arrow-up-svg red-svg-color"
                            ></i>
                            <span>{{ `${percent}` }}</span>
                        </div>
                    </div>
                </div>
                <div class="flexg1" id="compare-detail">
                    <div>
                        {{ $t('statistics.sibling_month_compare.last_month') }}:
                        {{ lastMonthTotal }}
                    </div>
                    <div :style="`width: ${percent1}`">
                        <div
                            style="
                                width: 100%;
                                height: 6px;
                                background-color: #409eff;
                                border-radius: 100px;
                            "
                        ></div>
                    </div>
                    <div>
                        {{ $t('statistics.sibling_month_compare.this_month') }}:
                        {{ thisMonthTotal }}
                    </div>
                    <div :style="`width: ${percent2}`">
                        <div
                            style="
                                width: 100%;
                                height: 6px;
                                background-color: #409eff;
                                border-radius: 100px;
                            "
                        ></div>
                    </div>
                </div>
            </div>
        </panel>

        <div class="record-header">{{ $t('statistics.exp_ranking.type') }}</div>

        <panel>
            <div :key="i.id" v-for="i in typeRankList" class="record-row">
                <div class="left">
                    <div>{{ i.type }}</div>
                    <div class="google-gray-400">
                        {{ i.description }}
                    </div>
                </div>

                <div class="right bold">
                    {{ i.total }}
                </div>
            </div>
        </panel>

        <gap-component></gap-component>

        <div class="record-header">
            {{ $t('statistics.exp_ranking.record') }}
        </div>

        <panel>
            <div :key="i.id" v-for="i in rankList" class="record-row">
                <div class="left">
                    <div>{{ i.type }}</div>
                    <div class="google-gray-400">
                        {{ i.description }}
                    </div>
                </div>

                <div class="right bold">
                    {{ i.total }}
                </div>
            </div>
        </panel>

        <gap-component :value="'58px'"></gap-component>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Client from '@/ts/request/client';
import GapComponent from '@/views/components/GapComponent.vue';
import { shallowMount } from '@vue/test-utils';
import Panel from '@/views/components/Panel.vue';
import AddRecordSheetComponent from '@/views/index/index/components/AddRecordSheetComponent.vue';
import CommonActionSheet from '@/views/components/CommonActionSheet.vue';
import CustomButton from '@/views/components/CustomButton.vue';
import { globalLoadingStart, globalLoadingStop } from '@/ts/view';
import request from '@/ts/request';
import Cache from '@/ts/cache';
import { getDefaultLedger } from '@/ts/common';

@Component({
    methods: { shallowMount },
    components: {
        CustomButton,
        CommonActionSheet,
        AddRecordSheetComponent,
        Panel,
        GapComponent,
    },
})
export default class StatisticIndexView extends Vue {
    afterGetValue(v) {
        let resp = v;
        this.percent = resp.percent;
        this.lastMonthTotal = resp.last_month_total;
        this.thisMonthTotal = resp.this_month_total;

        if (this.lastMonthTotal.indexOf('.') !== -1) {
            this.lastMonthTotal = this.lastMonthTotal.substring(
                0,
                this.lastMonthTotal.indexOf('.') + 3,
            );
        }

        if (this.thisMonthTotal.indexOf('.') !== -1) {
            this.thisMonthTotal = this.thisMonthTotal.substring(
                0,
                this.thisMonthTotal.indexOf('.') + 3,
            );
        }

        this.typeRankList = resp.type_rank_list;
        this.rankList = resp.rank_list;
        this.getPercent();
        this.$nextTick(() => {
            this.centerPercentText();
        });
    }

    show = false;
    minDate = new Date(2020, 0, 1);
    maxDate = new Date(2100, 0, 1);
    currentDate = new Date();

    columnsType = 'year-month';

    percent = '';
    lastMonthTotal = '';
    thisMonthTotal = '';
    typeRankList: any[] = [];
    rankList: any[] = [];

    percent1 = '';
    percent2 = '';

    headerHeight = 0;

    ledgerPickerShow = false;

    ledgerList: any[] = [];
    pickedLedgerName = '';
    pickedLedger: any = null;
    onSelectLedger(ledger) {
        this.pickedLedgerName = ledger.name;
        this.pickedLedger = ledger;
    }
    onClickPickLedger() {
        globalLoadingStart();
        request({
            url: '/ledgers',
            method: 'get',
        })
            .then((resp: any) => {
                this.ledgerPickerShow = true;
                let ledgerList = resp.data;
                this.ledgerList = ledgerList;
                let userconfigs = this.$store.getters.userConfigs;
                let defaultLedgerName = userconfigs.find(
                    (it) => it.key === 'default_ledger',
                );
                this.pickedLedgerName = defaultLedgerName.value;
                this.pickedLedger = ledgerList.find(
                    (i) => i.name === defaultLedgerName.value,
                );
                globalLoadingStop();
            })
            .catch((resp) => {
                globalLoadingStop();
            });
    }

    onPickDate() {
        this.init(null);
        this.show = false;
    }

    onCancel() {
        this.show = false;
    }

    getCurrentDate() {
        return (
            this.currentDate.getFullYear() +
            '-' +
            (this.currentDate.getMonth() + 1)
        );
    }

    getPercent() {
        let i =
            Number(this.lastMonthTotal) > Number(this.thisMonthTotal)
                ? Number(this.lastMonthTotal)
                : Number(this.thisMonthTotal);
        if (i !== 0) {
            this.percent1 =
                ((Number(this.lastMonthTotal) / i) * 100).toFixed(2) + '%';
            this.percent2 =
                ((Number(this.thisMonthTotal) / i) * 100).toFixed(2) + '%';
        }
    }

    created() {
        this.init(null);
    }

    init(ledgerId: null | number) {
        globalLoadingStart();
        let cacheKey = (ledgerName) => {
            return `statistics_${ledgerName}_${this.getCurrentDate()}`;
        };
        let ledgerName1: any = null;
        getDefaultLedger()
            .then((ledgerName) => {
                ledgerName1 = ledgerName;
                return Cache.getItem(cacheKey(ledgerName));
            })
            .then((cv) => {
                debugger;
                if (cv) {
                    this.afterGetValue(cv);
                } else {
                    Client.getStatisticsData(
                        this.getCurrentDate(),
                        ledgerId,
                    ).then((resp: any) => {
                        Cache.setItem(cacheKey(ledgerName1), resp.data);
                        this.afterGetValue(resp.data);
                    });
                }
            })
            .catch((err) => {
                console.log(err);
            })
            .finally(() => {
                globalLoadingStop();
            });
    }

    format() {
        return '';
    }

    centerPercentText() {
        let t = document.getElementById('compare-total-percentage');
        let tt = document.getElementById('compare-total-percentage-text');

        let th = t!.clientHeight;
        let tw = t!.clientWidth;
        let tth = tt!.clientHeight;
        let ttw = tt!.clientWidth;
        // center tt in t
        tt!.style.top = (th - tth) / 2 + 'px';
        tt!.style.left = (tw - ttw) / 2 + 'px';
        tt!.style.position = 'relative';
        if (this.percent === '') {
            return;
        }
        let e = document.getElementById(
            'compare-total-percentage-text',
        ) as HTMLElement;

        let i = document.getElementById('percent-arrow') as HTMLElement;
        if (this.percent.startsWith('-')) {
            i.className = '';
            i.className = 'arrow-down-svg green-svg-color';
            e.style.color = 'green';
        } else if (this.percent === 'infinite') {
            this.percent = '';
            i.className = '';
            i.className = 'minus-svg gray-svg-color';
            e.style.color = 'gray';
        } else {
            i.className = '';
            i.className = 'arrow-up-svg red-svg-color';
            e.style.color = 'red';
        }
    }

    mounted() {
        this.centerPercentText();

        let rs = document.getElementsByClassName('rank');
        let rts = document.getElementsByClassName('rank-text');

        for (let i = 0; i < rs.length; i++) {
            let r = rs[i] as HTMLElement;
            let rt = rts[i] as HTMLElement;
            let rh = r.clientHeight;
            let rw = r.clientWidth;
            let rth = rt.clientHeight;
            let rtw = rt.clientWidth;

            rt.style.top = (rh - rth) / 2 + 'px';
            rt.style.left = (rw - rtw) / 2 + 'px';
            rt.style.position = 'relative';
        }

        let cards = document.getElementsByClassName('card');
        for (let i = 0; i < cards.length; i++) {
            let c = cards[i] as HTMLElement;
            c.style.marginBottom = '8px';
            c.style.borderRadius = '8px';
            c.style.backgroundColor = 'white';
            c.style.padding = '8px';
        }

        let header = document.getElementById(
            'statistics-tab-title',
        ) as HTMLElement;
        let headerHeight = header.clientHeight;

        this.headerHeight = headerHeight;
    }
}
</script>
<style lang="scss" scoped>
@import '~@/style/common-style.scss';
@import '~@/style/style-specification';
@import '~@/assets/custom-icon.css';

.tab-title {
    z-index: 100;
    position: fixed;
    left: 0;
    top: 0;
    right: 0;
    background-color: white;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 20px;
    font-weight: 600;
    padding: 18px;
}
</style>
