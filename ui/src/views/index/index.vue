<template>
    <div class=''>
        <router-view></router-view>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import {pushPage} from "@/ts/pageStack";

@Component({})
export default class IndexIndexView extends Vue {
    created() {
        pushPage("home", null)
    }
}
</script>
<style lang='scss' scoped>
</style>
