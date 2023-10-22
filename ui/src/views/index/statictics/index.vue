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
                            50%
                        </i>
                    </div>
                </div>
                <div class="flexg1" id="compare-detail">
                    <div>last month: 3000</div>
                    <div style="width: 50%">
                        <div style="width: 100%; height: 6px; background-color: #409eff; border-radius: 100px"></div>
                    </div>
                    <div>this month: 6000</div>
                    <div>
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
                <div v-for="i in [1, 0.6, 0.5, 0.1]">
                    <div class="flex">
                        <div class="flexg1 rank">
                            <div class="rank-text" style="width: fit-content">1</div>
                        </div>
                        <div class="flexg9">
                            <div class="flex justify-between">
                                <div>alcohol</div>
                                <div>1000</div>
                            </div>

                            <div :style="`width: ${i*100}%`">
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
                <div v-for="i in [1, 0.6, 0.5, 0.1]">
                    <div class="flex">
                        <div class="flexg1 rank">
                            <div class="rank-text" style="width: fit-content">1</div>
                        </div>
                        <div class="flexg9">
                            <div class="flex justify-between">
                                <div>alcohol</div>
                                <div>1000</div>
                            </div>

                            <div :style="`width: ${i*100}%`">
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

@Component({})
export default class StatisticIndexView extends Vue {
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
