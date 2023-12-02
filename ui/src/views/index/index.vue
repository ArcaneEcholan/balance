<template>
    <div class=''>
        <div id="page-main-area" class="tran">
            <router-view></router-view>
        </div>


        <div id="tabbar-area" class="tran">
            <div class="flex">
                <div :style="`${activeTabBar == 'home'?'color: #1989fa;': ''}`"
                     class="flex column flex-center flexg1"
                     @click="onClickHome"
                >
                    <div>
                        <span>
                            <i class="icon ali-international-icon-home1"></i>
                        </span>
                    </div>
                    <div>
                        <span class="fs12" style="line-height: 1">home</span>
                    </div>
                </div>
                <div :style="`${activeTabBar == 'statistics'?'color: #1989fa;': ''}`"
                     class="flex column flex-center flexg1"
                     @click="onClickStatistics"
                >
                    <div>
                       <span>
                            <i class="icon ali-international-icon-pie-chart"></i>
                        </span>
                    </div>
                    <div>
                        <span class="fs12" style="line-height: 1">statistics</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';

@Component({})
export default class IndexIndexView extends Vue {


    activeTabBar = "home"

    mounted() {
        let tabBarArea = document.getElementById("tabbar-area")!
        let tabBarStyle = tabBarArea.style
        tabBarStyle.position = "fixed"
        tabBarStyle.bottom = "0";
        tabBarStyle.right = "0.1px"
        tabBarStyle.width = "100%";
        tabBarStyle.backgroundColor = "white"
    }
    onClickHome() {
        this.activeTabBar = "home"
        this.$router.push({name: "home"})
    }

    onClickStatistics() {
        this.activeTabBar = "statistics"
        this.$router.push({name: "statistics"})
    }

}
</script>
<style lang='scss' scoped>
#page-main-area {
    position: relative;
    right: 0.1px;
    width: 100%;
    height: 100%;
}
#page-main-area.tran {
    transition: right 0.5s cubic-bezier(0, 1, 0, 1);
}

#tabbar-area.tran {
    transition: right 0.5s cubic-bezier(0, 1, 0, 1);
}
</style>
