<template>
    <common-action-sheet
        :visible.sync="visible"
        @open="onLedgerPickerSheetOpen"
    >
        <template #header>
            {{ $t('pick_a_ledger') }}
        </template>
        <template #body>
            <div v-if="ledgersLoading" class="flex center">
                <van-loading></van-loading>
            </div>
            <div v-else>
                <van-cell-group class="shadow br15 overflow-hidden">
                    <van-cell
                        :key="ledger.name"
                        @click="onClickSwitchLedger(ledger)"
                        v-for="ledger in ledgerList"
                        :title="ledger.name"
                    ></van-cell>
                </van-cell-group>
                <gap-component :value="'30px'"></gap-component>
                <div class="flex center">
                    <custom-button @click="onClickManageLedgerList">
                        {{ $t('all_ledgers') }}
                    </custom-button>
                </div>
                <gap-component :value="'30px'"></gap-component>
            </div>
        </template>
    </common-action-sheet>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Client from '@/ts/request/client';
import eventBus from '@/ts/EventBus';
import CommonButton from '@/views/components/CommonButton.vue';
import { provideListeners } from '@/page-eventbus-registration-mixin';
import GapComponent from '@/views/components/GapComponent.vue';
import CustomButton from '@/views/components/CustomButton.vue';
import { getI18nValue } from '../../../../ts/utils';
import store from '@/ts/store';
import request from '@/ts/request';
import { Toast } from 'vant';
import CommonActionSheet from '@/views/components/CommonActionSheet.vue';

@Component({
    methods: { getI18nValue },
    components: { CommonActionSheet, CustomButton, GapComponent, CommonButton },
})
export default class LedgerSwitcherComponent extends Vue {
    ledgersListLoadedFirstTime = true;

    visible = false;

    ledgersLoading = false;

    currentLedger: any = {};

    ledgerList: any[] = [];

    onLedgerPickerSheetOpen() {
        // ensure the loading does not happen when user repeatly close and open the ledger
        if (!this.ledgersListLoadedFirstTime) {
            return;
        }

        this.ledgersLoading = true;
        Client.getLedgerList()
            .then((resp: any) => {
                this.ledgersListLoadedFirstTime = false;
                this.ledgersLoading = false;
                this.ledgerList = resp.data;

                eventBus.$emit('ledges-changes', this.ledgerList);
            })
            .catch((err: any) => {
                this.ledgersLoading = false;
                console.error(err);
            });
    }

    showSheet() {
        this.visible = true;
    }

    created() {
        provideListeners(this, [
            {
                eventName: 'on-get-current-ledger-name',
                handler: () => {
                    return this.currentLedger.name;
                },
            },
            {
                eventName: 'ledges-changes',
                handler: (list: any) => {
                    this.ledgerList = list;
                },
            },
            {
                eventName: 'ledger-deleted',
                handler: (id: any) => {
                    this.ledgerList = this.ledgerList.filter((item: any) => {
                        return item.id !== id;
                    });
                    if (this.currentLedger.id == id) {
                        if (this.ledgerList.length != 0) {
                            this.currentLedger = this.ledgerList[0];
                        } else {
                            // this is undefined behavior
                            this.currentLedger = { id: null, name: 'unknown' };
                        }
                    }
                },
            },
        ]);

        // set default ledger
        {
            let userConfigs = store.getters.userConfigs;
            if (userConfigs == null) {
                this.currentLedger = {
                    name: 'default',
                };
            }
            let defaultLedgerConfig = userConfigs.filter(
                (it: any) => it.key === 'default_ledger',
            );
            if (
                defaultLedgerConfig == null ||
                defaultLedgerConfig.length === 0
            ) {
                this.currentLedger = {
                    name: 'default',
                };
            } else {
                this.currentLedger = {
                    name: defaultLedgerConfig[0].value,
                };
            }

            // broadcast current ledger
            eventBus.$emit(
                'on-cur-ledger-changed',
                Object.assign({}, this.currentLedger),
            );
        }
    }

    onClickSwitchLedger(ledger: any) {
        Toast.loading({
            message: 'Loading...',
            forbidClick: true,
            duration: 0,
        });

        this.currentLedger = ledger;

        request({
            url: '/user/configs',
            method: 'post',
            data: {
                configs: [
                    {
                        key: 'default_ledger',
                        value: ledger.name,
                    },
                ],
            },
            headers: {
                'entity-token': store.getters.token,
            },
        }).then((resp) => {
            console.log(resp);
            console.debug('current ledger changed to: ', ledger);

            this.toggleLedgerSelection();

            eventBus.$emit('on-cur-ledger-changed', ledger);

            Toast.clear();
        });
    }

    onClickManageLedgerList() {
        eventBus.$emit('on-click-manage-ledger', {});
        this.toggleLedgerSelection();
    }

    toggleLedgerSelection() {
        this.visible = !this.visible;
    }
}
</script>
<style lang="scss" scoped>
@import '~@/style/common-style.scss';
@import '~@/style/style-specification';

.icon {
    font-size: 20px;
}
</style>
