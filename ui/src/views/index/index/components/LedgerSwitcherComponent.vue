<template>
  <van-action-sheet :closeable="false" v-model="show" title="">
    <div class="action-sheet-title">{{ $t('pick_a_ledger') }}</div>
    <div class="action-sheet-body">

      <div class="cells-block-title">
        {{ $t('ledger_list') }}
      </div>

      <panel>
        <van-cell-group class="shadow br15 overflow-hidden">
          <van-cell
              @click="onClickSwitchLedger(ledger)"
              v-for="ledger in ledgerList"
              :title="ledger.name"
          ></van-cell>
        </van-cell-group>
      </panel>

      <gap-component></gap-component>

      <div class="flex center">
        <custom-button @click="onClickManageLedgerList">
          {{ $t('all_ledgers') }}
        </custom-button>
      </div>

      <gap-component :value="'30px'"></gap-component>

    </div>
  </van-action-sheet>

</template>

<script lang="ts">
import {Component, Vue} from 'vue-property-decorator';
import Client from '@/request/client';
import eventBus from '@/ts/EventBus';
import {provideListeners} from '@/page-eventbus-registration-mixin';
import GapComponent from '@/views/components/GapComponent.vue';
import CustomButton from '@/components/CustomButton.vue';
import {getI18nValue} from "../../../../ts/utils";
import Panel from "@/components/Panel.vue";

@Component({
  methods: {getI18nValue},
  components: {Panel, CustomButton, GapComponent},
})
export default class LedgerSwitcherComponent extends Vue {
  show = false;

  ledgersLoading = false;

  showSheet() {
    this.show = true;
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
              this.currentLedger = {id: null, name: 'unknown'};
            }
          }
        },
      },
    ]);

    this.ledgersLoading = true;
    Client.getLedgerList()
        .then((resp: any) => {
          this.ledgersLoading = false;
          this.ledgerList = resp.data;
          this.currentLedger = this.ledgerList[0];
          eventBus.$emit(
              'on-cur-ledger-changed',
              Object.assign({}, this.currentLedger),
          );
          eventBus.$emit('ledges-changes', this.ledgerList);
        })
        .catch((err: any) => {
          this.ledgersLoading = false;
          console.error(err);
        });
  }

  currentLedger: any = {
    name: 'default',
  };

  ledgerList: any[] = [];

  onClickSwitchLedger(ledger: any) {
    this.currentLedger = ledger;
    console.debug('current ledger changed to: ', ledger);

    this.toggleLedgerSelection();

    eventBus.$emit('on-cur-ledger-changed', ledger);
  }

  onClickManageLedgerList() {
    eventBus.$emit('on-click-manage-ledger', {});
    this.toggleLedgerSelection();
  }

  toggleLedgerSelection() {
    this.show = !this.show;
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
