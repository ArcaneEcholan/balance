<template>
    <div class="">
        <div id="page-main-area" class="tran">
            <router-view></router-view>
        </div>

        <div id="tabbar-area" class="tran">
            <div class="flex">
                <div
                    :style="`${
                        activeTabBar == getI18nValue('home')
                            ? 'color: #1989fa;'
                            : ''
                    }`"
                    class="flex column flex-center flexg1"
                    @click="onClickHome"
                >
                    <div>
                        <span>
                            <i class="icon ali-international-icon-home1"></i>
                        </span>
                    </div>
                    <div>
                        <span class="fs12" style="line-height: 1">
                            {{ getI18nValue('home') }}
                        </span>
                    </div>
                </div>
                <div
                    :style="`${
                        activeTabBar == getI18nValue('statistics')
                            ? 'color: #1989fa;'
                            : ''
                    }`"
                    class="flex column flex-center flexg1"
                    @click="onClickStatistics"
                >
                    <div>
                        <span>
                            <i
                                class="icon ali-international-icon-pie-chart"
                            ></i>
                        </span>
                    </div>
                    <div>
                        <span class="fs12" style="line-height: 1">
                            {{ getI18nValue('statistics') }}
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getI18nValue } from '@/ts/utils';

@Component({
    methods: { getI18nValue },
})
export default class IndexIndexView extends Vue {
    activeTabBar = getI18nValue('home');

    created() {
        this.$router.push({ name: 'home' });
    }

    mounted() {
        let tabBarArea = document.getElementById('tabbar-area')!;
        let tabBarStyle = tabBarArea.style;
        tabBarStyle.position = 'fixed';
        tabBarStyle.bottom = '0';
        tabBarStyle.right = '0.1px';
        tabBarStyle.width = '100%';
        tabBarStyle.backgroundColor = 'white';
    }

    onClickHome() {
        this.activeTabBar = getI18nValue('home');
        this.$router.push({ name: 'home' });
    }

    onClickStatistics() {
        this.activeTabBar = getI18nValue('statistics');
        this.$router.push({ name: 'statistics' });
    }
}
</script>
<style lang="scss" scoped>
#page-main-area {
    position: relative;
    right: 0.1px;
    width: 100%;
    height: 100%;
}

#page-main-area.tran {
    transition: right var(--transition-duration) var(--transition-easing);
}

#tabbar-area.tran {
    transition: right var(--transition-duration) var(--transition-easing);
}
</style>
