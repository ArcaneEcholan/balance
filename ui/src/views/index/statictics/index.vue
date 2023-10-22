<template>
    <div class='page'>
        <div class="card shadow">
            <div>
                compare
            </div>
            <div class="flex" style="width: 100%">
                <div class="flexg1" id="compare-total-percentage">
                    <div style="width: fit-content" id="compare-total-percentage-text">
                        <i class="el-icon-top"
                           style="color: red">
                            {{ `${(percent * 100).toFixed(2)}%` }}
                        </i>
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
                                <div>{{i.type}}</div>
                                <div>{{i.total}}</div>
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
                                <div>{{i.description}}</div>
                                <div>{{i.total}}</div>
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
import {getCurrentMonth, getCurrentYearAndMonth} from "@/ts/time";

@Component({})
export default class StatisticIndexView extends Vue {
    percent: string = ""
    lastMonthTotal: string = ""
    thisMonthTotal: string = ""
    typeRankList: any[] = []
    rankList: any[] = []

    percent1: string = ""
    percent2: string = ""

    getPercent() {
        let i = Number(this.lastMonthTotal) > Number(this.thisMonthTotal) ? Number(this.lastMonthTotal) : Number(this.thisMonthTotal)
        if (i !== 0) {
            this.percent1 = (Number(this.lastMonthTotal) / i * 100).toFixed(2) + "%"
            this.percent2 = (Number(this.thisMonthTotal) / i * 100).toFixed(2) + "%"
        }
    }

    created() {
        Client.getStatisticsData(getCurrentYearAndMonth()).then((resp: any) => {
            resp = resp.data
            this.percent = resp.percent
            this.lastMonthTotal = resp.last_month_total
            this.thisMonthTotal = resp.this_month_total
            this.typeRankList = resp.type_rank_list
            this.rankList = resp.rank_list
            this.getPercent()
        })
    }

    format() {
        return ""
    }

    mounted() {
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

.page {
    box-sizing: border-box;
    //padding: 0 8px 0 8px;
    padding: 8px;
    background-color: #f7f8fa;
}
</style>
