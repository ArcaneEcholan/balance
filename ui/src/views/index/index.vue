<template>
    <div class=''>
        <router-view></router-view>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';

@Component({})
export default class IndexIndexView extends Vue {
    created() {
        this.$router.push({name: "home"})
    }
}
</script>
<style lang='scss' scoped>
</style>
