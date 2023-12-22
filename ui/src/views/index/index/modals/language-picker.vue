<template>
    <div class="">
        <modal-presentation
            z-index="110"
            @on-open="modalLifeCycleHooks.onOpen"
            @on-close="modalLifeCycleHooks.onClose"
            @closed="closed"
            @opened="modalLifeCycleHooks.opened"
            @before-swipe="modalLifeCycleHooks.beforeSwipe"
            @swiping="modalLifeCycleHooks.swiping"
            @after-swipe="modalLifeCycleHooks.afterSwipe"
        >
            <div class="page">
                <div class="modal-title">
                    {{ $t('language') }}
                </div>

                <gap-component :value="'55px'"></gap-component>

                <panel>
                    <van-cell-group>
                        <van-cell
                            clickable
                            :value="'中文(简体)'"
                            @click="changeLocale('chinese')"
                        />
                        <van-cell
                            clickable
                            :value="'English'"
                            @click="changeLocale('english')"
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
import Panel from '@/views/components/Panel.vue';
import { stripType, unmountComponent } from '@/ts/utils';
import GapComponent from '@/views/components/GapComponent.vue';
import { Locale, Notify } from 'vant';
import { setLanguage } from '@/ts/lang';

@Component({
    components: { GapComponent, Panel, ModalPresentation },
})
export default class LanguagePickerComponent extends Vue {
    modalLifeCycleHooks: any;
    changeLocale(name: string) {
        if (name === 'chinese') {
            let value = 'zh-CN';
            // Vant basic
            Locale.use(value, this.$i18n.messages[value]);
            // Business component
            this.$i18n.locale = value;
            // Cookie
            setLanguage(value);
        } else if (name === 'english') {
            let value = 'en-US';
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
            duration: 1000,
        });
    }

    created() {
        this.modalLifeCycleHooks = stripType(
            this.$options,
        ).$mountProp.modalLifeCycleHooks;
    }

    closed() {
        this.modalLifeCycleHooks.closed();
        unmountComponent(this, 0);
    }
}
</script>
<style lang="scss" scoped></style>
