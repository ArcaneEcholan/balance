<template>
    <div class="">
        <modal-presentation
            z-index="109"
            ref="settings-panel"
            @on-open="modalLifeCycleHooks.onOpen"
            @on-close="modalLifeCycleHooks.onClose"
            @closed="closed"
            @opened="modalLifeCycleHooks.opened"
            @before-swipe="modalLifeCycleHooks.beforeSwipe"
            @swiping="modalLifeCycleHooks.swiping"
            @after-swipe="modalLifeCycleHooks.afterSwipe"
        >
            <div :id="mountPointUid"></div>
            <div class="page">
                <div class="modal-title">
                    {{ $t('settings') }}
                </div>

                <gap-component :value="'55px'"></gap-component>

                <panel>
                    <van-cell-group>
                        <van-cell
                            :title="this.$t('language')"
                            clickable
                            @click="onclickLanguageEntry"
                        />
                    </van-cell-group>
                </panel>
            </div>
        </modal-presentation>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import ModalPresentation from '@/views/components/ModalPresentation.vue';
import {
    generateMountPointUid,
    mountComponent,
    unmountComponent,
} from '@/ts/utils';
import Panel from '@/views/components/Panel.vue';
import LanguagePickerComponent from '@/views/index/index/modals/language-picker.vue';
import GapComponent from '@/views/components/GapComponent.vue';
import { getVueEl } from '@/ts/vueUtils';

@Component({
    components: { GapComponent, Panel, ModalPresentation },
})
export default class SettingsView extends Vue {
    mountPointUid = generateMountPointUid();

    onclickLanguageEntry() {
        console.log('hlasjdlfjasldjf');
        let arg: any = {};
        arg.modalLifeCycleHooks = this.getModalLifeCycleHooks();
        mountComponent(this.mountPointUid, LanguagePickerComponent, arg);
    }

    getModalLifeCycleHooks = () => {
        return {
            onOpen: () => {
                let elem = getVueEl(this, 'settings-panel');
                elem.style.right = 100 + 'px';
            },
            onClose: () => {
                let elem = getVueEl(this, 'settings-panel');
                elem.style.right = 0 + 'px';
            },
            beforeSwipe: () => {
                let elem = getVueEl(this, 'settings-panel');
                elem.classList.remove('tran');
            },
            swiping: (args: any) => {
                let swipingPathPercent = args.swipingPathPercent;
                let r = 1 - swipingPathPercent;

                let elem = getVueEl(this, 'settings-panel');
                elem.style.right = r * 100 + 'px';
            },
            afterSwipe: () => {
                let elem = getVueEl(this, 'settings-panel');
                elem.classList.add('tran');
            },
            closed: () => {
                // window.removeEventListener('touchstart', banTouch)
            },
            opened: () => {
                // window.removeEventListener('touchstart', banTouch)
            },
        };
    };

    modalLifeCycleHooks: any;

    created() {
        // @ts-ignore
        let mountProp = this.$options.$mountProp;
        this.modalLifeCycleHooks = {
            onOpen: mountProp.modalLifeCycleHooks.onOpen,
            beforeSwipe: mountProp.modalLifeCycleHooks.beforeSwipe,
            swiping: mountProp.modalLifeCycleHooks.swiping,
            afterSwipe: mountProp.modalLifeCycleHooks.afterSwipe,
            onClose: mountProp.modalLifeCycleHooks.onClose,
            closed: mountProp.modalLifeCycleHooks.closed,
            opened: mountProp.modalLifeCycleHooks.opened,
        };
    }

    closed() {
        this.modalLifeCycleHooks.closed();
        unmountComponent(this, 0);
    }
}
</script>
<style lang="scss" scoped>
@import '~@/style/common-style';
@import '~@/style/style-specification';

#settings-panel {
    position: relative;
    right: 0.1px;
    width: 100%;
    height: 100%;
}

#settings-panel.tran {
    transition: right var(--transition-duration) var(--transition-easing);
}
</style>
