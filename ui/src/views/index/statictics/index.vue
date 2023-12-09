<template>
    <div class='page'>
        <div>
            <van-button plain type="info " @click="show=true">month</van-button>
        </div>
        <div class="shadow br8 overflow-hidden">
            <van-action-sheet :closeable="false" v-model="show" title="">
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
            </van-action-sheet>
        </div>

        <gap-component></gap-component>
        <div class="card shadow">
            <div>
                compare
            </div>
            <div class="flex" style="width: 100%">
                <div class="flexg1" id="compare-total-percentage">
                    <div style="width: fit-content" id="compare-total-percentage-text">
                        <div class="flex">
                            <i id="percent-arrow" class="arrow-up-svg red-svg-color">
                            </i>
                            <span>{{ `${percent}` }}</span>
                        </div>
                    </div>
                </div>
                <div class="flexg1" id="compare-detail">
                    <div>last month: {{ lastMonthTotal }}</div>
                    <div :style="`width: ${percent1}`">
                        <div style="width: 100%; height: 6px; background-color: #409eff; border-radius: 100px"></div>
                    </div>
                    <div>this month: {{ thisMonthTotal }}</div>
                    <div :style="`width: ${percent2}`">
                        <div style="width: 100%; height: 6px; background-color: #409eff; border-radius: 100px"></div>
                    </div>
                </div>
            </div>
        </div>


        <div class="card shadow">
            <div>
                type ranking
            </div>
            <div>
                <div v-for="i in typeRankList">
                    <div class="flex">
                        <div class="flexg1 rank">
                            <div class="rank-text" style="width: fit-content">1</div>
                        </div>
                        <div class="flexg9">
                            <div class="flex justify-between">
                                <div>{{ i.type }}</div>
                                <div>{{ i.total }}</div>
                            </div>

                            <div :style="`width: ${i.percent*100}%`">
                                <div
                                    style="width: 100%; height: 6px; background-color: #409eff; border-radius: 100px">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
        <div class="card shadow">
            <div>
                single ranking
            </div>
            <div>
                <div v-for="i in rankList">
                    <div class="flex">
                        <div class="flexg1 rank">
                            <div class="rank-text" style="width: fit-content">1</div>
                        </div>
                        <div class="flexg9">
                            <div class="flex justify-between">
                                <div>{{ i.description }}</div>
                                <div>{{ i.total }}</div>
                            </div>

                            <div :style="`width: ${i.percent*100}%`">
                                <div
                                    style="width: 100%; height: 6px; background-color: #409eff; border-radius: 100px">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import Client from "@/request/client";
import {getCurrentYearAndMonth} from "@/ts/time";
import GapComponent from "@/views/components/GapComponent.vue";
import {shallowMount} from "@vue/test-utils";

@Component({
    methods: {shallowMount},
    components: {GapComponent}
})
export default class StatisticIndexView extends Vue {
    show = false
    minDate = new Date(2020, 0, 1)
    maxDate = new Date(2100, 0, 1)
    currentDate = new Date()

    columnsType = "year-month"

    percent: string = ""
    lastMonthTotal: string = ""
    thisMonthTotal: string = ""
    typeRankList: any[] = []
    rankList: any[] = []

    percent1: string = ""
    percent2: string = ""

    onPickDate() {
        this.init()
        this.show = false
    }

    onCancel() {
        this.show = false
    }

    getCurrentDate() {
        return this.currentDate.getFullYear() + "-" + (this.currentDate.getMonth() + 1)
    }

    getPercent() {
        let i = Number(this.lastMonthTotal) > Number(this.thisMonthTotal) ? Number(this.lastMonthTotal) : Number(this.thisMonthTotal)
        if (i !== 0) {
            this.percent1 = (Number(this.lastMonthTotal) / i * 100).toFixed(2) + "%"
            this.percent2 = (Number(this.thisMonthTotal) / i * 100).toFixed(2) + "%"
        }
    }

    created() {
      this.init()
    }

    init() {
        Client.getStatisticsData(this.getCurrentDate()).then((resp: any) => {
            resp = resp.data
            this.percent = resp.percent
            this.lastMonthTotal = resp.last_month_total
            this.thisMonthTotal = resp.this_month_total
            this.typeRankList = resp.type_rank_list
            this.rankList = resp.rank_list
            this.getPercent()
            this.$nextTick(() => {
                this.centerPercentText()
            })
        })
    }
    format() {
        return ""
    }

    centerPercentText() {
        let t = document.getElementById("compare-total-percentage")
        let tt = document.getElementById("compare-total-percentage-text")
        let d = document.getElementById("compare-detail")

        let th = t!.clientHeight
        let tw = t!.clientWidth
        let tth = tt!.clientHeight
        let ttw = tt!.clientWidth
        // center tt in t
        tt!.style.top = ((th - tth) / 2) + "px"
        tt!.style.left = ((tw - ttw) / 2) + "px"
        tt!.style.position = "relative"
        if (this.percent === "") {
            return
        }
        let e = document.getElementById("compare-total-percentage-text") as HTMLElement

        let i = document.getElementById("percent-arrow") as HTMLElement
        if (this.percent.startsWith('-')) {
            i.className = ""
            i.className = "arrow-down-svg green-svg-color"
            e.style.color = "green"
        } else if (this.percent === "infinite") {
            i.className = ""
            i.className = "minus-svg gray-svg-color"
            e.style.color = "gray"
        } else {
            i.className = ""
            i.className = "arrow-up-svg red-svg-color"
            e.style.color = "red"
        }


    }

    mounted() {
        this.centerPercentText()

        let rs = document.getElementsByClassName("rank")
        let rts = document.getElementsByClassName("rank-text")

        for (let i = 0; i < rs.length; i++) {
            let r = rs[i] as HTMLElement
            let rt = rts[i] as HTMLElement
            let rh = r.clientHeight
            let rw = r.clientWidth
            let rth = rt.clientHeight
            let rtw = rt.clientWidth

            rt.style.top = ((rh - rth) / 2) + "px"
            rt.style.left = ((rw - rtw) / 2) + "px"
            rt.style.position = "relative"
        }

        let cards = document.getElementsByClassName("card")
        for (let i = 0; i < cards.length; i++) {
            let c = cards[i] as HTMLElement
            c.style.marginBottom = "8px"
            c.style.borderRadius = "8px"
            c.style.backgroundColor = "white"
            c.style.padding = "8px"
        }
    }
}
</script>
<style lang='scss' scoped>
@import "~@/style/common-style.scss";
@import "~@/assets/custom-icon.css";

</style>
