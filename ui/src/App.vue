<template>
    <div id="app" class="tran">
        <div id="header-area" class="fixed-header">
            <!--<van-nav-bar :title="title" left-text="Back" left-arrow>-->
            <!--    <template #right>-->
            <!--        <van-icon name="search"/>-->
            <!--    </template>-->
            <!--</van-nav-bar>-->
        </div>

        <div id="padding-area"></div>

        <div id="main-app-area">
            <router-view/>
        </div>
        <div id="pad" class="pad"></div>
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


<script lang="ts">
import {Component, Vue} from 'vue-property-decorator';
import pageConfig from "@/ts/pageConfig";

let that: any
@Component
export default class AppView extends Vue {

    touchStartPositionX = 0

    fingerPositionX = 0

    activeTabBar = "home"
    // can be reflected in the view
    pc = pageConfig

    get title() {
        return this.pc.getTitle()
    }

    created() {
        that = this;
    }

    onClickHome() {
        this.activeTabBar = "home"
        this.$router.push({name: "home"})
    }

    onClickStatistics() {
        this.activeTabBar = "statistics"
        this.$router.push({name: "statistics"})
    }

    // this listener should be put here instead of in the touchstart listener

    mounted() {
        this.registerListenersForHandlingEdgeSwipe()

        // document.body.style.overflow = "hidden"
        // can not be 100%, because there will be a little white gap in the bottom
        // document.body.style.height = document.documentElement.clientHeight + "px"
        // let b = document
        let header = document.getElementById("header-area")!
        let headerPaddingArea = document.getElementById("padding-area")!
        let mainAppArea = document.getElementById("main-app-area")!

        // let vh = document.body.clientHeight

        let headerHeight = header.clientHeight
        headerPaddingArea.style.height = headerHeight + "px"
        // mainAppArea.style.position = "relative"
        // m.style.height = (vh - headerHeight - t.clientHeight) + "px"
        mainAppArea.style.backgroundColor = "#f7f8fa"
        // m.style.overflowY = "auto"

        header.style.zIndex = "3"
        // mainAppArea.style.zIndex = "2"


        let tabBarArea = document.getElementById("tabbar-area")!
        let tabBarStyle = tabBarArea.style
        tabBarStyle.position = "fixed"
        tabBarStyle.bottom = "0";
        tabBarStyle.right = "0.1px"
        tabBarStyle.width = "100%";
        // tabBarStyle.zIndex = "3";
        tabBarStyle.backgroundColor = "white"

        let pad = document.getElementById("pad")!
        pad.style.height = tabBarArea.clientHeight + "px"

    }

    registerListenersForHandlingEdgeSwipe() {

        let app = document.getElementById("app")!

        app.addEventListener('touchstart', (e) => {
            this.touchStartPositionX = e.changedTouches[0].clientX

            // don't prevent default here, this will prevent the click event from being triggered
            // more information should be collected as the user move finger
            app.removeEventListener('touchmove', this.touchMoveListener);
            app.addEventListener('touchmove', this.touchMoveListener);
        });
    }

    // make sure pageConfig is in vue reactive system, so the change of it

    // to avoid the listener being created every time touchstart is triggered
    touchMoveListener = (e: any) => {
        // use that instead of this, because "this" is not the vue instance,
        // but an object of class AppView
        that.fingerPositionX = e.changedTouches[0].clientX;
        that.handleEdgeSwipe(e)
    }

    handleEdgeSwipe(e: any) {
        if (this.touchStartPositionX <= 30) {
            if (this.fingerPositionX - this.touchStartPositionX > 0) {
                console.debug("prevent default swipe gesture")
                e.preventDefault();
            }
        }

        if (this.touchStartPositionX >= window.innerWidth - 30) {
            if (this.fingerPositionX - this.touchStartPositionX < 0) {
                console.debug("prevent default swipe gesture")
                e.preventDefault();
            }
        }
    }
}

</script>

<style lang="scss">
@import "~@/assets/custom-icon.css";
@import "~@/style/common-style.scss";

body {
    padding: 0;
    margin: 0;
}

.fixed-header {
    position: fixed;
    top: 0;
    width: 100%;
}

#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;


    position: relative;
    right: 0.1px;
    width: 100%;
    height: 100%;
}

#app.tran {
    transition: right 0.5s cubic-bezier(0, 1, 0, 1);
}
#tabbar-area.tran {
    transition: right 0.5s cubic-bezier(0, 1, 0, 1);
}

nav {
    padding: 30px;

    a {
        font-weight: bold;
        color: #2c3e50;

        &.router-link-exact-active {
            color: #42b983;
        }
    }
}

#tabbar-area {
    border-top: 1px solid #EBECF0;
    padding: 8px 0;
}

.icon {
    font-size: 20px !important;
}
</style>
