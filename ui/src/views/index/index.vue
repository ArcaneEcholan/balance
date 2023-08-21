<template>
<div class=''>
    <keep-alive :include="['IndexView']">
        <router-view></router-view>
    </keep-alive>
</div>
</template>

<script lang='ts'>
import { Component, Vue } from 'vue-property-decorator';
@Component({})
export default class IndexIndexView extends Vue {
}
</script>
<style lang='scss' scoped>
</style>
