<template>
  <modal-presentation
      @on-open="modalLifeCycleHooks.onOpen"
      @on-close="modalLifeCycleHooks.onClose"
      @closed="closed"
      @before-swipe="modalLifeCycleHooks.beforeSwipe"
      @swiping="modalLifeCycleHooks.swiping"
      @after-swipe="modalLifeCycleHooks.afterSwipe"
  >

    <van-action-sheet v-model="show">
      <div class="action-sheet-title">{{ $t('edit_ledger_title') }}</div>
      <div class="action-sheet-body">
        <div class="cells-block-title">{{ $t('ledger_name') }}</div>
        <van-cell-group class="shadow overflow-hidden br8">
          <van-field
              v-model="editLedgerName"
              type="text"
              label="name"
          />
        </van-cell-group>
        <gap-component></gap-component>
        <custom-button
            @click="submitEditLedger"
            :disabled="editLedgerLoading"
        >
          <template #default>{{$t('save')}}</template>
        </custom-button>
      </div>
    </van-action-sheet>

    <van-action-sheet v-model="addLedgerShow">
      <div class="action-sheet-title">{{ $t('add_ledger_title') }}</div>
      <div class="action-sheet-body">
        <div class="cells-block-title">{{ $t('ledger_name') }}</div>
        <van-cell-group class="shadow overflow-hidden br8">
          <van-field
              v-model="addLedgerName"
              type="text"
              label="name"
          />
        </van-cell-group>
        <gap-component></gap-component>
        <custom-button
            @click="submitAddLedger"
            :disabled="addLedgerLoading"
        >
          <template #default>{{$t('save')}}</template>
        </custom-button>
      </div>
    </van-action-sheet>

    <div ref="page-main-frame" style="height: 100%">
      <div
          class="page"
          ref="page-main-area"
          style="padding-bottom: 66px; overflow: auto"
      >
        <div class="modal-title">{{ $t('ledger_management') }}</div>
        <gap-component :value="'55px'"></gap-component>
        <div class="cells-block-title">{{$t('ledger_list')}}</div>
        <van-cell-group class="shadow br15 overflow-hidden">
          <van-swipe-cell v-for="ledger in ledgers">
            <van-cell :border="false" :title="ledger.name"/>
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
            icon-class="ali-international-icon-add-1"
            @click.native="ff"
        ></solid-icon>
      </div>
    </div>
  </modal-presentation>
</template>

<script lang="ts">
import {Component, Vue} from 'vue-property-decorator';
import ModalPresentationView from '@/components/ModalPresentation.vue';
import {Notify} from 'vant';
import {getHtmlElem} from '@/ts/vueUtils';
import eventBus from '@/ts/EventBus';
import Client from '@/request/client';
import CustomButton from '@/components/CustomButton.vue';
import GapComponent from '@/views/components/GapComponent.vue';
import {disableBodyScroll, enableBodyScroll, unmountComponent} from '@/ts/utils';
import SolidIcon from '@/views/components/SolidIcon.vue';

let that: any;
@Component({
  components: {
    SolidIcon,
    GapComponent,
    CustomButton,
    ModalPresentation: ModalPresentationView,
  },
})
export default class ManageLedgerView extends Vue {

  close() {
    enableBodyScroll()
    let modal = document.getElementById('page-main-area')!;
    modal.style.right = 0 + 'px';

    modal = document.getElementById('tabbar-area')!;
    modal.style.right = 0 + 'px';

    modal = document.getElementById('records-index-header')!;
    modal.style.right = 0 + 'px';
  }

  afterSwipe() {
    let modal = document.getElementById('page-main-area')!;
    modal.classList.add('tran')

    modal = document.getElementById('tabbar-area')!;
    modal.classList.add('tran')

    modal = document.getElementById('records-index-header')!;
    modal.classList.add('tran')
  }

  swiping(args: any) {
    let swipingPathPercent = args.swipingPathPercent
    let r = 1 - swipingPathPercent
    let modal = document.getElementById('page-main-area')!;
    modal.style.right = r * 100 + 'px';


    modal = document.getElementById('tabbar-area')!;
    modal.style.right = r * 100 + 'px';


    modal = document.getElementById('records-index-header')!;
    modal.style.right = r * 100 + 'px';
  }

  beforeSwipe() {
    let elem = document.getElementById('page-main-area')!;
    elem.classList.remove('tran')

    elem = document.getElementById('tabbar-area')!;
    elem.classList.remove('tran')

    elem = document.getElementById('records-index-header')!;
    elem.classList.remove('tran')
  }

  modalOpen() {
    disableBodyScroll()
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

  onclose() {
  }

  ledgers: any = [];
  varTable: any = {};
  show = false;

  categoryValue: string | null = null;
  name = '';
  editLedgerId: number | null = null;
  editLedgerName: string | null = '';
  addLedgerLoading = false;
  addLedgerShow = false;
  addLedgerName: string | null = '';
  countxxx = 100;

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

  onClickDelete(ledgerId: number, ledgeName: string) {
    Client.deleteLedger(ledgerId)
        .then(() => {
          Notify({
            type: 'success',
            message: 'Delete success',
          });
          this.ledgers = this.ledgers.filter(
              (item: any) => item.id != ledgerId,
          );
          eventBus.$emit('ledger-deleted', ledgerId);
        })
        .catch(() => {
          Notify({
            type: 'danger',
            message: 'Delete failed',
          });
        });
  }

  editLedgerLoading = false;

  submitEditLedger() {
    this.editLedgerLoading = true;
    setTimeout(() => {
      this.editLedgerLoading = false;
      Notify({
        type: 'success',
        message: 'Update success',
      });

      this.show = false;
      this.ledgers.filter((item: any) => {
        if (item.id === this.editLedgerId) {
          item.name = this.editLedgerName;
        }
      });
    }, 500);
  }

  ledgersLoading = false;
  modalLifeCycleHooks: any

  created() {
    // @ts-ignore
    this.recordId = this.$options.$mountProp.id// @ts-ignore
    this.amount = this.$options.$mountProp.amount// @ts-ignore
    this.datetime = this.$options.$mountProp.datetime// @ts-ignore
    this.count = this.$options.$mountProp.count// @ts-ignore
    this.categoryValue = this.$options.$mountProp.categoryValue// @ts-ignore
    this.description = this.$options.$mountProp.description


    this.modalLifeCycleHooks = {// @ts-ignore
      onOpen: this.$options.$mountProp.modalLifeCycleHooks.onOpen,// @ts-ignore
      beforeSwipe: this.$options.$mountProp.modalLifeCycleHooks.beforeSwipe,// @ts-ignore
      swiping: this.$options.$mountProp.modalLifeCycleHooks.swiping,// @ts-ignore
      afterSwipe: this.$options.$mountProp.modalLifeCycleHooks.afterSwipe,// @ts-ignore
      onClose: this.$options.$mountProp.modalLifeCycleHooks.onClose
    }

    this.ledgersLoading = true;
    Client.getLedgerList()
        .then((resp) => {
          this.ledgersLoading = false;
          this.ledgers = resp.data;
          eventBus.$emit('ledges-changes', this.ledgers);
        })
        .catch(() => {
          this.ledgersLoading = false;
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
