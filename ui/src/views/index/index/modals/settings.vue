<template>
<div class=''>
    <modal-presentation
        z-index="109"
        ref="settings-panel"
        @on-open="modalLifeCycleHooks.onOpen"
        @on-close="modalLifeCycleHooks.onClose"
        @closed="closed"
        @before-swipe="modalLifeCycleHooks.beforeSwipe"
        @swiping="modalLifeCycleHooks.swiping"
        @after-swipe="modalLifeCycleHooks.afterSwipe"
    >
        <div :id="mountPointUid"></div>
        <div class="page">

            <div class="modal-title">
                {{$t('settings')}}
            </div>

            <gap-component :value="'55px'"></gap-component>

            <panel>
                <van-cell-group>
                    <van-cell :title="this.$t('language')" is-link @click="onclickLanguageEntry"/>
                </van-cell-group>
            </panel>
        </div>
    </modal-presentation>
</div>
</template>

<script lang='ts'>
import { Component, Vue } from 'vue-property-decorator';
import ModalPresentation from "@/components/ModalPresentation.vue";
import {disableBodyScroll, enableBodyScroll, generateMountPointUid, mountComponent, unmountComponent} from "@/ts/utils";
import Panel from "@/components/Panel.vue";
import EditRecordView from "@/views/index/index/modals/edit_record.vue";
import LanguagePickerComponent from "@/views/index/index/modals/language-picker.vue";
import GapComponent from "@/views/components/GapComponent.vue";
@Component({
    components: {GapComponent, Panel, ModalPresentation}
})
export default class SettingsView extends Vue {
    mountPointUid = generateMountPointUid();
    onclickLanguageEntry() {


        let arg = {}
        arg.modalLifeCycleHooks = this.getModalLifeCycleHooks()
        mountComponent(this.mountPointUid, LanguagePickerComponent, arg);


        this.mountLanguagePickerComponent({})

    }
    getModalLifeCycleHooks = () => {
        return {
            onOpen: () => {
                {  let elem  =this.$refs['settings-panel'].$el
                    let right = elem.style.right;
                    right = right.replace('px', '');
                    elem.style.right = 100 + 'px';
                }
            },
            onClose: () => {
                // enableBodyScroll()
                let elem  =this.$refs['settings-panel'].$el
                elem.style.right = 0 + 'px';
            },
            beforeSwipe: () => {

                let elem  =this.$refs['settings-panel'].$el
                elem.classList.remove('tran')
            },
            swiping: (args: any) => {
                let swipingPathPercent = args.swipingPathPercent
                let r = 1 - swipingPathPercent
                let elem  =this.$refs['settings-panel'].$el
                elem.style.right = r * 100 + 'px';
            },
            afterSwipe: () => {
                let elem  =this.$refs['settings-panel'].$el
                elem.classList.add('tran')
            },
        };
    }
    mountLanguagePickerComponent(arg: any) {

    }
    modalLifeCycleHooks: any
    created() {
        this.modalLifeCycleHooks = {// @ts-ignore
            onOpen: this.$options.$mountProp.modalLifeCycleHooks.onOpen,// @ts-ignore
            beforeSwipe: this.$options.$mountProp.modalLifeCycleHooks.beforeSwipe,// @ts-ignore
            swiping: this.$options.$mountProp.modalLifeCycleHooks.swiping,// @ts-ignore
            afterSwipe: this.$options.$mountProp.modalLifeCycleHooks.afterSwipe,// @ts-ignore
            onClose: this.$options.$mountProp.modalLifeCycleHooks.onClose
        }
    }
    closed() {
        unmountComponent(this, 0);
    }
}
</script>
<style lang='scss' scoped>
@import '~@/style/common-style';
@import '~@/style/style-specification';

#settings-panel {
    position: relative;
    right: 0.1px;
    width: 100%;
    height: 100%;
}

#settings-panel.tran{
    transition: right 0.5s cubic-bezier(0, 1, 0, 1);
}
</style>
