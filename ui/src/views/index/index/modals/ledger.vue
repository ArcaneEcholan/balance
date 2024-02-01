<template>
    <modal-presentation :hooks="modalLifeCycleHooks" @closed="closed">
        <common-action-sheet :fit-content="true" :visible.sync="show">
            <template #header>Edit Ledger</template>
            <template #body>
                <div class="record-header"></div>
                <van-cell-group class="shadow overflow-hidden br8">
                    <van-field
                        v-model="editLedgerName"
                        type="text"
                        label="name"
                    />
                </van-cell-group>
                <gap-component></gap-component>
                <common-button
                    @click="submitEditLedger"
                    :disabled="editLedgerLoading"
                >
                    <template #default>Submit</template>
                </common-button>
            </template>
        </common-action-sheet>
        <common-action-sheet :fit-content="true" :visible.sync="addLedgerShow">
            <template #header>Add Ledger</template>
            <template #body>
                <gap-component :value="'32px'"></gap-component>
                <div class="record-header">Add Fields</div>
                <van-cell-group class="shadow overflow-hidden br8">
                    <van-field
                        v-model="addLedgerName"
                        type="text"
                        label="name"
                    />
                </van-cell-group>
                <gap-component></gap-component>
                <common-button
                    @click="submitAddLedger"
                    :disabled="addLedgerLoading"
                >
                    <template #default>Submit</template>
                </common-button>
            </template>
        </common-action-sheet>

        <div ref="page-main-frame" style="height: 100%">
            <div
                class="page"
                ref="page-main-area"
                style="padding-bottom: 66px; overflow: auto"
            >
                <div class="modal-title">{{ $t('ledger_management') }}</div>
                <gap-component :value="'55px'"></gap-component>
                <div class="record-header">Ledgers</div>
                <van-cell-group class="shadow br15 overflow-hidden">
                    <van-swipe-cell v-for="ledger in ledgers">
                        <van-cell :border="false" :title="ledger.name" />
                        <template #right>
                            <van-button
                                square
                                type="primary"
                                :text="$t('edit')"
                                @click="onClickEdit(ledger.id, ledger.name)"
                            />
                            <van-button
                                square
                                type="danger"
                                :text="$t('delete')"
                                @click="onClickDelete(ledger.id, ledger.name)"
                            />
                        </template>
                    </van-swipe-cell>
                </van-cell-group>
            </div>

            <div
                ref="bottom-tool-bar"
                class="bottom-bar"
                style="padding-left: 8px"
            >
                <solid-icon
                    :clickable="true"
                    icon-class="cw-icon-add-fat"
                    @click.native="ff"
                ></solid-icon>
            </div>
        </div>
    </modal-presentation>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import ModalPresentationView from '@/views/components/ModalPresentation.vue';
import { Notify } from 'vant';
import { getHtmlElem } from '@/ts/vueUtils';
import eventBus from '@/ts/EventBus';
import Client from '@/ts/request/client';
import CommonButton from '@/views/components/CommonButton.vue';
import GapComponent from '@/views/components/GapComponent.vue';
import {
    disableBodyScroll,
    enableBodyScroll,
    stripType,
    unmountComponent,
} from '@/ts/utils';
import SolidIcon from '@/views/components/SolidIcon.vue';
import CommonActionSheet from '@/views/components/CommonActionSheet.vue';
import request, { HttpError } from '@/ts/request';
import { globalLoadingStart, globalLoadingStop } from '@/ts/view';
import store from '@/ts/store';
import cache from '@/ts/cache';
import storage from '@/ts/storage';

let that: any;
@Component({
    components: {
        CommonActionSheet,
        SolidIcon,
        GapComponent,
        CommonButton,
        ModalPresentation: ModalPresentationView,
    },
})
export default class ManageLedgerView extends Vue {
    close() {
        enableBodyScroll();
        let modal = document.getElementById('page-main-area')!;
        modal.style.right = 0 + 'px';

        modal = document.getElementById('tabbar-area')!;
        modal.style.right = 0 + 'px';

        modal = document.getElementById('records-index-header')!;
        modal.style.right = 0 + 'px';
    }

    afterSwipe() {
        let modal = document.getElementById('page-main-area')!;
        modal.classList.add('tran');

        modal = document.getElementById('tabbar-area')!;
        modal.classList.add('tran');

        modal = document.getElementById('records-index-header')!;
        modal.classList.add('tran');
    }

    swiping(args: any) {
        let swipingPathPercent = args.swipingPathPercent;
        let r = 1 - swipingPathPercent;
        let modal = document.getElementById('page-main-area')!;
        modal.style.right = r * 100 + 'px';

        modal = document.getElementById('tabbar-area')!;
        modal.style.right = r * 100 + 'px';

        modal = document.getElementById('records-index-header')!;
        modal.style.right = r * 100 + 'px';
    }

    beforeSwipe() {
        let elem = document.getElementById('page-main-area')!;
        elem.classList.remove('tran');

        elem = document.getElementById('tabbar-area')!;
        elem.classList.remove('tran');

        elem = document.getElementById('records-index-header')!;
        elem.classList.remove('tran');
    }

    modalOpen() {
        disableBodyScroll();
        let elem = document.getElementById('page-main-area')!;
        let right = elem.style.right;
        right = right.replace('px', '');
        elem.style.right = 100 + 'px';

        elem = document.getElementById('tabbar-area')!;
        right = elem.style.right;
        right = right.replace('px', '');
        elem.style.right = 100 + 'px';

        elem = document.getElementById('records-index-header')!;
        right = elem.style.right;
        right = right.replace('px', '');
        elem.style.right = 100 + 'px';
    }

    beforeDestroy() {
        console.log('destroyed');
    }

    ledgers: any = [];
    varTable: any = {};
    show = false;

    name = '';
    editLedgerId: number | null = null;
    editLedgerName: string | null = '';
    addLedgerLoading = false;
    addLedgerShow = false;
    addLedgerName: string | null = '';

    submitAddLedger() {
        let name = this.addLedgerName;
        if (name == null || name === '') {
            Notify({
                type: 'danger',
                message: 'name is empty',
            });
            return;
        }
        this.addLedgerLoading = true;
        Client.addLedger(name)
            .then((resp) => {
                Notify({
                    type: 'success',
                    message: 'Add success',
                });

                this.addLedgerShow = false;
                this.ledgers.push({
                    id: resp.data.id,
                    name: resp.data.name,
                    ctime: resp.data.ctime,
                });

                storage.purgeLedgerCache(name!);
                // we don't recover the loading status to prevent the user click submit button twice
            })
            .catch(() => {
                this.addLedgerLoading = false;
                Notify({
                    type: 'danger',
                    message: 'add failed',
                });
            });
    }

    onClickEdit(ledgerId: number, ledgerName: string) {
        this.editLedgerId = ledgerId;
        this.editLedgerName = ledgerName;
        this.show = true;
    }

    onClickDelete(ledgerId: number, ledgerName: string) {
        request({
            url: `/ledger/${ledgerId}`,
            method: 'delete',
            headers: {
                'entity-token': store.getters.token,
            },
        })
            .then(() => {
                Notify({
                    type: 'success',
                    message: 'Delete success',
                });
                this.ledgers = this.ledgers.filter(
                    (item: any) => item.id != ledgerId,
                );

                storage.purgeLedgerCache(ledgerName);
            })
            .catch((httpErr: HttpError) => {
                Notify({
                    type: 'danger',
                    message: 'Delete failed: ' + httpErr.resp!.data!.message,
                });
            });
    }

    editLedgerLoading = false;

    submitEditLedger() {
        this.editLedgerLoading = true;
        globalLoadingStart();
        request({
            url: '/ledger',
            method: 'put',
            data: {
                id: this.editLedgerId,
                name: this.editLedgerName,
            },
            headers: {
                'entity-token': store.getters.token,
            },
        }).then((resp) => {
            Notify({
                type: 'success',
                message: 'Update success',
            });

            storage.purgeLedgerCache(this.editLedgerName!);

            this.show = false;
            this.ledgers.filter((item: any) => {
                if (item.id === this.editLedgerId) {
                    item.name = this.editLedgerName;
                }
            });

            globalLoadingStop();
        });
    }

    ledgersLoading = false;
    modalLifeCycleHooks: any;

    created() {
        this.modalLifeCycleHooks =
            stripType(this).$options.$mountProp.modalLifeCycleHooks;

        globalLoadingStart();
        storage
            .getLedgers()
            .then((ledgerList) => {
                this.ledgers = ledgerList;
            })
            .finally(() => {
                globalLoadingStop();
            });
    }

    pageMainFrameHeight = 0;
    pageMainAreaHeight = 0;
    pageRatio = 95;

    mounted() {
        let mainframe = getHtmlElem(this, 'page-main-frame');

        let pageMainArea = getHtmlElem(this, 'page-main-area');
        this.pageMainAreaHeight = pageMainArea.clientHeight;
        this.pageMainFrameHeight = mainframe.clientHeight;
    }

    ff() {
        this.addLedgerName = '';
        this.addLedgerLoading = false;
        this.addLedgerShow = true;
    }

    closed() {
        unmountComponent(this, 0);
    }
}
</script>
<style lang="scss" scoped>
@import '~@/style/common-style';
@import '~@/style/style-specification';

//.page {
//  padding: 0 8px 0 8px;
//  background-color: #f7f8fa;
//}

.record-header {
    padding: 16px 16px 8px;
    color: #969799;
    font-size: 14px;
    line-height: 16px;
}

.bottom-bar {
    display: flex;
    align-items: center;
    position: absolute;
    bottom: 0;
    height: 50px;
    width: 100%;
    border-top: 1px solid #ebecf0;

    background-color: white;

    display: flex;
}
</style>
