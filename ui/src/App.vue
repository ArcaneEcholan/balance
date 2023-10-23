<template>
    <div id="app">
        <!--<nav>-->
        <!--    &lt;!&ndash;<router-link to="/test">Test</router-link>&ndash;&gt;-->
        <!--</nav>-->
        <div id="header-area" class="fixed-header">
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

        <div id="tabbar-area">
            <van-tabbar v-model="activeTabBar" @change="onChange">
                <van-tabbar-item name="home" icon="home-o">home</van-tabbar-item>
                <van-tabbar-item name="statistics">
                    <span>statistic</span>
                    <template #icon="props">
                        <span v-if="props.active"><i style="color:#1989fa" class="el-icon-pie-chart"></i></span>
                        <span v-else><i class="el-icon-pie-chart"></i></span>
                    </template>
                </van-tabbar-item>
            </van-tabbar>
        </div>
    </div>
</template>


<script lang="ts">
import {Component, Vue} from 'vue-property-decorator';
import pageConfig from "@/ts/pageConfig";
import pageStack, {pushPage, replacePage} from "@/ts/pageStack";
let that: any
@Component
export default class AppView extends Vue {

    touchStartPositionX = 0

    fingerPositionX = 0

    activeTabBar = "home"

    created() {
        that = this;
    }

    onChange(name:string) {
        replacePage(name, {})
    }

    mounted() {
        this.registerListenersForHandlingEdgeSwipe()

        // document.body.style.overflow = "hidden"
        // can not be 100%, because there will be a little white gap in the bottom
        document.body.style.height = document.documentElement.clientHeight + "px"
        // let b = document
        let h = document.getElementById("header-area")!
        let p = document.getElementById("padding-area")!
        let m = document.getElementById("main-app-area")!
        let t = document.getElementById("tabbar-area")!

        let vh = document.body.clientHeight

        let headerHeight = h.clientHeight
        p.style.height = headerHeight + "px"
        m.style.position = "relative"
        m.style.height = (vh - headerHeight - 50) + "px"
        m.style.backgroundColor = "#f7f8fa"
        m.style.overflowY = "auto"

        h.style.zIndex = "1000"
        m.style.zIndex = "999"

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

body {
    padding: 0;
    margin: 0;
}

.fixed-header {
    position: fixed;
    top: 0;
    z-index: 1901;
    width: 100%;
}

#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    //text-align: center;
    color: #2c3e50;
    height: 660px;
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
