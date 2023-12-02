<template>
    <div id="app" class="tran">

        <div id="main-app-area">
            <router-view/>
        </div>


    </div>
</template>


<script lang="ts">
import {Component, Vue} from 'vue-property-decorator';
import pageConfig from "@/ts/pageConfig";

let that: any
@Component
export default class AppView extends Vue {


    created() {
        that = this;
    }

    touchStartPositionX = 0

    fingerPositionX = 0

    // this listener should be put here instead of in the touchstart listener
    mounted() {



        // app.removeChild(app)

        {
            // make sure pageConfig is in vue reactive system, so the change of it
            // to avoid the listener being created every time touchstart is triggered
            let touchMoveListener = (e: any) => {
                // use that instead of this, because "this" is not the vue instance,
                // but an object of class AppView
                that.fingerPositionX = e.changedTouches[0].clientX;

                //  handleEdgeSwipe
                {
                    if (that.touchStartPositionX <= 30) {
                        if (that.fingerPositionX - that.touchStartPositionX > 0) {
                            console.debug("prevent default swipe gesture")
                            e.preventDefault();
                        }
                    }

                    if (that.touchStartPositionX >= window.innerWidth - 30) {
                        if (that.fingerPositionX - that.touchStartPositionX < 0) {
                            console.debug("prevent default swipe gesture")
                            e.preventDefault();
                        }
                    }
                }
            }

            {
                let app = document.getElementById("app")!

                app.addEventListener('touchstart', (e) => {
                    this.touchStartPositionX = e.changedTouches[0].clientX

                    // don't prevent default here, this will prevent the click event from being triggered
                    // more information should be collected as the user move finger
                    app.removeEventListener('touchmove', touchMoveListener);
                    app.addEventListener('touchmove', touchMoveListener);
                });
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
