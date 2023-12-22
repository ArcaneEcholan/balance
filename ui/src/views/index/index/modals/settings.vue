<template>
    <modal-presentation
        z-index="109"
        ref="settings-panel"
        :hooks="modalLifeCycleHooks"
        @closed="closed"
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
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import ModalPresentation from '@/views/components/ModalPresentation.vue';
import {
    generateMountPointUid,
    mountComponent,
    stripType,
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
        mountComponent(this.mountPointUid, LanguagePickerComponent, {
            modalLifeCycleHooks: {
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
            },
        });
    }

    modalLifeCycleHooks: any;

    created() {
        this.modalLifeCycleHooks =
            stripType(this).$options.$mountProp.modalLifeCycleHooks;
    }

    closed() {
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
