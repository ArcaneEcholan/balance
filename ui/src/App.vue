<template>
    <div id="app">
        <!--<nav>-->
        <!--    &lt;!&ndash;<router-link to="/test">Test</router-link>&ndash;&gt;-->
        <!--</nav>-->
        <div id="header-area" class="fixed-header" style="">
            <van-nav-bar :title="title" left-text="Back" left-arrow>
                <template #right>
                    <van-icon name="search"/>
                </template>
            </van-nav-bar>
        </div>

        <div id="padding-area"></div>

        <div id="main-app-area">
            <router-view/>
        </div>
        <div id="pad" class="pad"></div>
        <div id="tabbar-area">
            <div class="flex">
                <div @click="onClickHome" class="flex column flex-center flexg1">
                    <div>
                        <span v-if="activeTabBar == 'home'" class="van-blue-svg-color">
                           <i style="width: 22px; height: 22px;" class="home-svg"></i>
                        </span>
                        <span v-else>
                            <i style="width: 22px; height: 22px;" class="home-svg"></i>
                        </span>
                    </div>
                    <div v-if="activeTabBar == 'home'" style="color: #1989fa;">
                        <span class="fs12" style="line-height: 1">home</span>
                    </div>
                    <div v-else>
                        <span class="fs12" style="line-height: 1">home</span>
                    </div>
                </div>
                <div @click="onClickStatistics" class="flex column flex-center flexg1">
                    <div>
                          <span v-if="activeTabBar == 'statistics'" class="van-blue-svg-color">
                            <i style="width: 22px; height: 22px;" class="pie-chart-svg"></i>
                        </span>
                        <span v-else>
                            <i style="width: 22px; height: 22px;" class="pie-chart-svg"></i>
                        </span>
                    </div>
                    <div v-if="activeTabBar == 'statistics'" style="color: #1989fa;">
                        <span class="fs12" style="line-height: 1">statistics</span>
                    </div>
                    <div v-else>
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
import {replacePage} from "@/ts/pageStack";

let that: any
@Component
export default class AppView extends Vue {

    touchStartPositionX = 0

    fingerPositionX = 0

    activeTabBar = "home"

    created() {
        that = this;
    }

    onChange(name: string) {
        replacePage(name, {})
    }

    onClickHome() {
        this.activeTabBar = "home"
        replacePage("home", {})
    }

    onClickStatistics() {
        this.activeTabBar = "statistics"
        replacePage("statistics", {})
    }

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
        tabBarStyle.left = "0";
        tabBarStyle.bottom = "0";
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

    // this listener should be put here instead of in the touchstart listener
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

    // make sure pageConfig is in vue reactive system, so the change of it
    // can be reflected in the view
    pc = pageConfig

    get title() {
        return this.pc.getTitle()
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

.body-ns {
    position: fixed;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
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
</style>
