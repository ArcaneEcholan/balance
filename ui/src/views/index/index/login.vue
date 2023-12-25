<template>
    <div class="login-page">
        <div class="main-area">
            <!--inputs-->
            <panel>
                <van-cell-group>
                    <van-field :label="$t('username')" v-model="username" />
                    <van-field :label="$t('password')" v-model="password" />
                </van-cell-group>
            </panel>

            <gap-component></gap-component>

            <custom-button>
                {{ $t('login') }}
            </custom-button>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Panel from '@/views/components/Panel.vue';
import CustomButton from '@/views/components/CustomButton.vue';
import GapComponent from '@/views/components/GapComponent.vue';
@Component({
    components: { GapComponent, CustomButton, Panel },
})
export default class LoginView extends Vue {
    username = '';
    password = '';
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
    }
}
</style>
