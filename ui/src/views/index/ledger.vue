<template>
  <modal-presentation
      @on-close="close"
      @after-swipe="afterSwipe"
      @swiping="swiping"
      @on-open="modalOpen"
      @before-swipe="beforeSwipe"
      @closed="onclose"
  >
    <van-action-sheet v-model="show">
      <div class="page">
        <div class="record-header">Edit Fields</div>
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
      </div>
    </van-action-sheet>
    <van-action-sheet v-model="addLedgerShow" title="Title">
      <div class="page">
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
      </div>
    </van-action-sheet>
    <div ref="page-main-frame" style="height: 100%">
      <div
          class="page"
          ref="page-main-area"
          style="padding-bottom: 66px; overflow: auto"
      >
        <div class="modal-title">Ledger Management</div>
        <gap-component :value="'55px'"></gap-component>
        <div class="record-header">Ledgers</div>
        <van-cell-group class="shadow br15 overflow-hidden">
          <van-swipe-cell v-for="ledger in ledgers">
            <van-cell :border="false" :title="ledger.name"/>
            <template #right>
              <van-button
                  square
                  type="primary"
                  text="Edit"
                  @click="onClickEdit(ledger.id, ledger.name)"
              />
              <van-button
                  square
                  type="danger"
                  text="Delete"
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
            @click="ff"
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
import CommonButton from '@/views/components/CommonButton.vue';
import GapComponent from '@/views/components/GapComponent.vue';
import {disableBodyScroll, enableBodyScroll, unmountComponent} from '@/ts/utils';
import SolidIcon from '@/views/components/SolidIcon.vue';
let that: any;
@Component({
  components: {
    SolidIcon,
    GapComponent,
    CommonButton,
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
    unmountComponent(this, 0);
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

  created() {
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
  }
}
</script>
<style lang="scss" scoped>
@import '~@/style/common-style.scss';
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
