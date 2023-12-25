<template>
    <div class="login-page">
        <div class="main-area">
            <!--icon-->
            <van-icon name="bill-o" class="fs24"></van-icon>

            <gap-component></gap-component>

            <!--welcome-->
            <div>
                <span class="fs24 bold">
                    {{ $t('login_greeting') }}
                </span>
            </div>

            <gap-component></gap-component>

            <!--inputs-->
            <div>
                <panel>
                    <van-cell-group>
                        <van-field
                            :disabled="showPasswordInput"
                            :label="$t('username')"
                            v-model="username"
                        />
                        <van-field
                            v-show="showPasswordInput"
                            :label="$t('password')"
                            v-model="password"
                        ></van-field>
                    </van-cell-group>
                </panel>

                <gap-component></gap-component>

                <custom-button
                    v-show="pageState === 'inputting_username'"
                    :disabled="passwordset_checking"
                    @click="loginContinue"
                >
                    <div class="flex center">
                        <div style="position: relative">
                            <van-loading
                                style="position: absolute; left: -16px"
                                size="16px"
                                v-show="passwordset_checking"
                            ></van-loading>
                            {{ $t('continue') }}
                        </div>
                    </div>
                </custom-button>

                <custom-button
                    v-show="pageState === 'inputting_password'"
                    :disabled="login_ing"
                    @click="login"
                >
                    <div class="flex center">
                        <div style="position: relative">
                            <van-loading
                                style="position: absolute; left: -16px"
                                size="16px"
                                v-show="login_ing"
                            ></van-loading>
                            {{ $t('continue') }}
                        </div>
                    </div>
                </custom-button>

                <custom-button
                    v-show="pageState === 'inputting_register_code'"
                    :disabled="login_ing"
                    @click="loginWithEmailCode"
                >
                    <div class="flex center">
                        <div style="position: relative">
                            <van-loading
                                style="position: absolute; left: -16px"
                                size="16px"
                                v-show="login_ing"
                            ></van-loading>
                            {{ $t('continue') }}
                        </div>
                    </div>
                </custom-button>
            </div>

            <div v-show="showRegisterCodeInput">
                <div>We've sent you temp login code to {{ username }}.</div>
                <panel>
                    <van-cell-group>
                        <van-field
                            :label="$t('register_code')"
                            v-model="registerCode"
                        ></van-field>
                    </van-cell-group>

                    <gap-component></gap-component>

                    <custom-button @click="verifyLogin">
                        {{ $t('verify_login') }}
                    </custom-button>
                </panel>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Panel from '@/views/components/Panel.vue';
import CustomButton from '@/views/components/CustomButton.vue';
import GapComponent from '@/views/components/GapComponent.vue';
import request from '@/ts/request';
import { Notify } from 'vant';
@Component({
    components: { GapComponent, CustomButton, Panel },
})
export default class LoginView extends Vue {
    showPasswordInput = false;
    showRegisterCodeInput = false;
    username = '';
    password = '';
    registerCode = '';
    passwordset_checking = false;
    pageState = 'inputting_username';
    login_ing = false;
    loginWithEmailCode() {
        this.login_ing = true;
        request
            .post('/user/auth', {
                type: 'email_code',
                username: this.username,
                credential: this.registerCode,
            })
            .then((resp) => {
                this.login_ing = false;
                this.$store.commit('user/SET_TOKEN', resp.data.token);
                this.$router.push('/home');
            })
            .catch((err) => {
                this.login_ing = false;

                Notify({
                    type: 'danger',
                    message: err.response.data,
                });
            });
    }

    login() {
        this.login_ing = true;
        request
            .post('/user/auth', {
                username: this.username,
                credential: this.password,
            })
            .then((resp) => {
                this.login_ing = false;
                this.$store.commit('user/SET_TOKEN', resp.data.token);
                this.$router.push('/home');
            })
            .catch((err) => {
                this.login_ing = false;

                Notify({
                    type: 'danger',
                    message: err.response.data,
                });
            });
    }
    verifyLogin() {}
    loginContinue() {
        this.passwordset_checking = true;
        request
            .get('/user/password_set')
            .then((resp) => {
                let ex = resp.data.exist;
                if (ex) {
                    this.passwordset_checking = false;
                    this.showPasswordInput = true;
                    this.pageState = 'inputting_password';
                } else {
                    request
                        .post('/mail_delivery/register_code')
                        .then((resp) => {
                            this.pageState = 'inputting_register_code';
                            this.passwordset_checking = false;
                            this.showRegisterCodeInput = true;
                        })
                        .catch((err) => {
                            this.passwordset_checking = false;
                            Notify({
                                type: 'danger',
                                message: err.response.data,
                            });
                        });
                }
            })
            .catch((err) => {
                this.passwordset_checking = false;
            });
    }
}
</script>
<style lang="scss">
.login-page {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100vh;

    .main-area {
        width: 80%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
    }
}

.loading::before {
    content: '';
    display: inline-block;
    position: absolute;
}
</style>
