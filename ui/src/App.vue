<template>
    <div id="app">
        <!--<nav>-->
        <!--    &lt;!&ndash;<router-link to="/test">Test</router-link>&ndash;&gt;-->
        <!--</nav>-->


        <div class="fixed-header">
            <van-nav-bar :title="title" left-text="Back" left-arrow>
                <template #right>
                    <van-icon name="search"/>
                </template>
            </van-nav-bar>
        </div>

        <div style="margin-top: 46px"></div>

        <router-view/>
    </div>
</template>


<script type="ts">
import {Component, Vue} from 'vue-property-decorator';
import pageConfig from "@/ts/pageConfig";
import {PageConfig} from "@/ts/pageConfig";

@Component
export default class AppView extends Vue {
    mounted() {
        let app = document.getElementById("app")

        app.addEventListener('touchstart', (e) => {
            // debugger
            const touch = e.changedTouches[0];
            // this is where the user's finger is, in this case, represents the swipe start point
            const touchClientX = touch.clientX;
            // is not near edge of view, exit
            if (touchClientX <= 20 || touchClientX >= window.innerWidth - 20) {
                console.debug("prevent default swipe gesture")
                e.preventDefault();
            }
        });
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
