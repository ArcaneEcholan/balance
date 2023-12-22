<template>
    <modal-presentation
        ref="modal"
        @on-open="modalLifeCycleHooks.onOpen"
        @on-close="modalLifeCycleHooks.onClose"
        @closed="closed"
        @before-swipe="modalLifeCycleHooks.beforeSwipe"
        @swiping="modalLifeCycleHooks.swiping"
        @after-swipe="modalLifeCycleHooks.afterSwipe"
    >
        <div class="page" id="aaa" style="overflow: auto">
            <div class="modal-title">Edit Record</div>
            <gap-component :value="'55px'"></gap-component>
            <div class="record-header">Edit Fields</div>
            <panel>
                <van-cell-group>
                    <van-field
                        v-model="categoryValue"
                        type="string"
                        label="category"
                    />
                    <van-field v-model="amount" type="number" label="amount" />
                    <van-field
                        v-model="datetime"
                        type="text"
                        label="datetime"
                    />
                    <van-field v-model="count" type="digit" label="count" />
                    <van-field
                        v-model="description"
                        type="text"
                        label="description"
                    />
                </van-cell-group>
            </panel>
            <gap-component value="30px"></gap-component>
            <custom-button :disabled="!submitEnable" @click="submit">
                <template #default>Submit</template>
            </custom-button>
        </div>
    </modal-presentation>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import ModalPresentationView from '@/views/components/ModalPresentation.vue';
import { Notify } from 'vant';
import Client from '@/ts/request/client';
import {
    countDecimalPlaces,
    isFloat,
    isPositiveInteger,
    stripType,
    unmountComponent,
} from '@/ts/utils';
import eventBus from '@/ts/EventBus';
import CustomButton from '@/views/components/CustomButton.vue';
import GapComponent from '@/views/components/GapComponent.vue';
import Panel from '@/views/components/Panel.vue';

@Component({
    components: {
        Panel,
        GapComponent,
        CustomButton,
        ModalPresentation: ModalPresentationView,
    },
})
export default class EditRecordView extends Vue {
    modalLifeCycleHooks: any;

    beforeDestroy() {
        console.log('destroyed');
    }

    closed() {
        unmountComponent(this, 0);
    }

    categoryValue: string | null = null;

    amountFractionNumberValid() {
        let i = isFloat(Number(this.amount));
        let amountValid = true;
        if (this.amount == null) {
            return false;
        }
        let amountDecimalPlaces = countDecimalPlaces(this.amount);
        if (amountDecimalPlaces > 2 || !i) {
            amountValid = false;
        }
        return amountValid;
    }

    typeValid() {
        let categoryValid = true;
        let categoryValue = this.categoryValue;
        if (categoryValue == null || categoryValue.trim() == '') {
            categoryValid = false;
        }
        return categoryValid;
    }

    checkValidationOfData() {
        if (this.amount == null || this.count == null) {
            Notify({
                type: 'danger',
                message: 'Information incomplete',
            });
            this.submitEnable = true;
            throw new Error('Information format invalid');
        }

        let countValid = isPositiveInteger(Number(this.count));
        let amountValid = this.amountFractionNumberValid();
        let categoryValid = this.typeValid();

        if (!countValid || !amountValid || !categoryValid) {
            this.submitEnable = true;
            throw new Error('Information format invalid');
        }
    }

    normalizeDateTime() {
        let datetime = this.datetime;

        if (datetime != null) {
            datetime = datetime.trim();
            const datetimeRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/;

            if (!datetimeRegex.test(datetime)) {
                Notify({
                    type: 'danger',
                    message: 'Information format invalid',
                });
                this.submitEnable = true;
                return;
            }
        }
        this.datetime = datetime;
    }

    submit() {
        this.submitEnable = false;

        this.normalizeDateTime();
        let description = this.description;

        try {
            this.checkValidationOfData();
        } catch (e) {
            Notify({
                type: 'danger',
                message: 'Information format invalid',
            });
        }

        // format check pass
        Client.updateTransaction(
            this.recordId,
            this.categoryValue!,
            this.amount!,
            this.datetime,
            this.count!,
            description,
        )
            .then((resp: any) => {
                let newTrans = resp.data;
                this.categoryValue = newTrans.categoryValue;
                this.amount = newTrans.amount;
                this.datetime = newTrans.datetime;
                this.count = newTrans.count;
                this.description = newTrans.description;
                eventBus.$emit('on-transaction-updated', newTrans);
                Notify({
                    type: 'success',
                    message: 'Update success',
                });
                this.submitEnable = true;
            })
            .catch((err: any) => {
                console.log(err);
                this.submitEnable = true;
            });
    }

    recordId: number | string | null = null;

    submitEnable = true;

    amount: string | null = null;
    datetime: string | null = null;
    count: string | null = null;
    description: string | null = null;

    created() {
        let mountProp = stripType(this.$options).$mountProp;

        this.modalLifeCycleHooks = mountProp.modalLifeCycleHooks;

        this.recordId = mountProp.id;
        this.amount = mountProp.amount;
        this.datetime = mountProp.datetime;
        this.count = mountProp.count;
        this.categoryValue = mountProp.categoryValue;
        this.description = mountProp.description;
    }
}
</script>
<style lang="scss" scoped>
@import '~@/style/common-style.scss';
@import '~@/style/style-specification';

.record-header {
    padding: 16px 16px 8px;
    color: #969799;
    font-size: 14px;
    line-height: 16px;
}
</style>
