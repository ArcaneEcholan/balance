<template>
    <div>
        <template v-for="type in transactionCategories">
            <van-tag
                class="mgr8 mgb8 pdr8 pdl8"
                style="line-height: 24px"
                type="primary"
                round
                @click="onClickOneType(type.value)"
            >
                {{ type.value }}
            </van-tag>
        </template>
    </div>

</template>

<script lang='ts'>
import { Component, Vue } from 'vue-property-decorator';
import Client from "@/request/client";
import eventBus from "@/ts/EventBus";
@Component({})
export default class TransTypeComponent extends Vue {
    transactionCategories: any[] = []

    created() {
        this.initTransactionTypes()
    }

    initTransactionTypes() {
        Client.getTransactionCategories().then(resp => {
            this.transactionCategories = resp.data
        })
    }

    onClickOneType(type: string) {
        this.$emit("on-click-one-type", type)
    }

}
</script>
<style lang='scss' scoped>
@import "~@/style/common-style.scss";
</style>
