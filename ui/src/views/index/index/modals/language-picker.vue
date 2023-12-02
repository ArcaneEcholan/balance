<template>
    <div class=''>
        <modal-presentation
            z-index="110"
            @on-open="modalLifeCycleHooks.onOpen"
            @on-close="modalLifeCycleHooks.onClose"
            @closed="closed"
            @before-swipe="modalLifeCycleHooks.beforeSwipe"
            @swiping="modalLifeCycleHooks.swiping"
            @after-swipe="modalLifeCycleHooks.afterSwipe"
        >
            <div class="page">
                <div class="modal-title">
                    {{$t('language')}}
                </div>

                <gap-component :value="'55px'"></gap-component>

                <panel>
                    <van-cell-group>
                        <van-cell clickable :value="'Chinese'" @click="changeLocale('chinese')"/>
                        <van-cell clickable :value="'English'" @click="changeLocale('english')"/>
                    </van-cell-group>
                </panel>
            </div>
        </modal-presentation>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import ModalPresentation from "@/components/ModalPresentation.vue";
import Panel from "@/components/Panel.vue";
import {unmountComponent} from "@/ts/utils";
import GapComponent from "@/views/components/GapComponent.vue";
import {Locale, Notify} from "vant";
import {setLanguage} from "@/ts/lang";

@Component({
    components: {GapComponent, Panel, ModalPresentation}
})
export default class LanguagePickerComponent extends Vue {
    modalLifeCycleHooks: any
    changeLocale(name: string) {
        if (name === "chinese") {
            let value = "zh-CN";
            // Vant basic
            Locale.use(value, this.$i18n.messages[value]);
            // Business component
            this.$i18n.locale = value;
            // Cookie
            setLanguage(value);
        } else if (name === "english") {
            let value = "en-US";
            // Vant basic
            Locale.use(value, this.$i18n.messages[value]);
            // Business component
            this.$i18n.locale = value;
            // Cookie
            setLanguage(value);
        }

        Notify({
            type: 'success',
            message: this.$t('change_language_successfully') as string,
            duration: 1000
        })
    }

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
</style>
