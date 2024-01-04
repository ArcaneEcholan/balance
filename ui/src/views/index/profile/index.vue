<template>
    <div class="page">
        <div name="page-title" class="modal-title">
            {{ $t('settings.title') }}
        </div>

        <gap-component value="55px"></gap-component>

        <panel>
            <van-cell-group>
                <van-cell
                    :title="$t('settings.profile.title')"
                    clickable
                    @click="accountSettingShow = true"
                ></van-cell>
                <van-cell
                    :title="$t('settings.language.title')"
                    clickable
                    @click="languagePickerShow = true"
                ></van-cell>
            </van-cell-group>
        </panel>

        <div name="sheets">
            <van-action-sheet
                style="height: 80%"
                name="language-picker-sheet"
                v-model="languagePickerShow"
                :closeable="false"
            >
                <div class="action-sheet-title">
                    {{ $t('settings.language.title') }}
                </div>
                <div class="action-sheet-body">
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
            </van-action-sheet>

            <van-action-sheet
                style="height: 80%"
                name="account-setting-sheet"
                v-model="accountSettingShow"
                :closeable="false"
            >
                <div class="action-sheet-title">
                    {{ $t('settings.profile.title') }}
                </div>
                <div class="action-sheet-body">
                    <panel>
                        <van-cell-group>
                            <van-cell
                                :title="
                                    $t('settings.profile.reset_password.title')
                                "
                                clickable
                                @click="changePasswordShow = true"
                            ></van-cell>
                            <van-cell
                                :title="$t('settings.profile.logout.title')"
                                clickable
                                @click="onClickLogout"
                            ></van-cell>
                        </van-cell-group>
                    </panel>
                </div>
            </van-action-sheet>

            <van-action-sheet
                style="height: 80%"
                name="change-password-sheet"
                v-model="changePasswordShow"
                :closeable="false"
            >
                <div name="change-password-title" class="action-sheet-title">
                    <div style="width: 100%" class="flex">
                        <div style="width: 20%"></div>
                        <div style="width: 60%" class="flex center">
                            {{ $t('settings.profile.reset_password.title') }}
                        </div>
                        <div style="width: 20%">
                            <custom-button
                                :disabled="changePasswordButtonDisabled"
                                @click="changePassword"
                                type="inline"
                            >
                                {{ $t('save') }}
                            </custom-button>
                        </div>
                    </div>
                </div>
                <div class="action-sheet-body">
                    <div class="record-header">
                        {{ $t('settings.profile.reset_password.password') }}
                    </div>

                    <panel name="first-password">
                        <van-form>
                            <van-cell-group>
                                <van-field
                                    ref="password"
                                    :rules="[
                                        {
                                            validator: passwordValidator,
                                            message: $t(
                                                'settings.profile.reset_password.password_format_error_prompt',
                                            ),
                                        },
                                    ]"
                                    v-model="password"
                                ></van-field>
                            </van-cell-group>
                        </van-form>
                    </panel>

                    <div class="record-header">
                        {{ $t('settings.profile.reset_password.repeat') }}
                    </div>
                    <panel name="repeat-password">
                        <van-form>
                            <van-cell-group>
                                <van-field
                                    ref="password_repeat"
                                    :rules="[
                                        {
                                            validator: repeatPasswordValidator,
                                            message: $t(
                                                'settings.profile.reset_password.password_repeat_error_prompt',
                                            ),
                                        },
                                    ]"
                                    v-model="password_repeat"
                                ></van-field>
                            </van-cell-group>
                        </van-form>
                    </panel>
                </div>
            </van-action-sheet>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Panel from '@/views/components/Panel.vue';
import CustomButton from '@/views/components/CustomButton.vue';
import GapComponent from '@/views/components/GapComponent.vue';
import { Dialog, Locale, Notify } from 'vant';
import { setLanguage } from '@/ts/lang';

@Component({
    components: { GapComponent, CustomButton, Panel },
})
export default class ProfileIndexView extends Vue {
    changePasswordShow = false;
    accountSettingShow = false;
    password = '';
    password_repeat = '';
    languagePickerShow = false;

    onClickLogout() {
        Dialog.confirm({
            title: this.$t('settings.profile.logout.title') as string,
            message: this.$t('settings.profile.logout.prompt') as string,
            confirmButtonText: this.$t('confirm') as string,
            cancelButtonText: this.$t('cancel') as string,
        }).then(() => {
            this.$store.commit('user/LOGOUT');
            this.$router.push('/login');
        });
    }

    get changePasswordButtonDisabled() {
        return (
            !this.passwordValidator(this.password) ||
            !this.repeatPasswordValidator()
        );
    }

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

    repeatPasswordValidator() {
        return this.password === this.password_repeat;
    }

    passwordValidator(password: string) {
        // must larger than 6 and only can use digits and letters
        let pattern = /^[0-9a-zA-Z]{6,}$/;
        return pattern.test(password);
    }

    changePassword() {
        // @ts-ignore
        this.$refs.password_repeat.validate().then((validatorArg: any) => {
            // TODO: may be we need to write a abstract layer for better compatibility
            let vantIsValidAdaptor = (valid: any) => {
                let isValid = false;
                if (valid == null) {
                    isValid = true;
                }
                return isValid;
            };

            let isValid = vantIsValidAdaptor(validatorArg);

            if (!isValid) {
                return;
            }

            this.changePasswordShow = false;
        });
    }
}
</script>
<style lang="scss" scoped></style>
