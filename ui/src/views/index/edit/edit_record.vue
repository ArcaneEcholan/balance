<template>
    <div>
        <modal-presentation :background-color="'#f7f8fa'" @close="closed" ref="modal"
        >
            <template #default>
                <div class="header">
                    <div class="flex flex-center" style="background-color: #fff;
height: 56px;">
                        <div class="fs20 bold"
                             style="
                         text-transform: capitalize;">
                            hello
                        </div>
                    </div>
                </div>

                <div class="pdt32 pdl16 pdr16 pdb16 google-gray-400 capitalize">test</div>
                <van-cell-group inset>
                    <van-field v-model="text" label="Text" />
                    <van-field v-model="tel" type="tel" label="Phone" />
                    <van-field v-model="digit" type="digit" label="Digit" />
                    <van-field v-model="number" type="number" label="Number" />
                    <van-field v-model="password" type="password" label="Password" />
                </van-cell-group>
            </template>
        </modal-presentation>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import ModalPresentationView from "@/components/ModalPresentation.vue";
import PageStack, {popPage} from "@/ts/pageStack";


// Register the router hooks with their names
// Component.registerHooks([
//     "beforeRouteEnter",
//     "beforeRouteLeave",
//     "beforeRouteUpdate"
// ]);
@Component({
    components: {
        ModalPresentation: ModalPresentationView
    }
})
export default class EditRecordView extends Vue {

    text: string | null = null
    tel: string | null = null
    digit: string | null = null
    number: string | null = null
    password: string | null = null

    modal!: ModalPresentationView

    mounted() {
        this.modal = this.$refs.modal as ModalPresentationView;
        this.modal.openModal()
    }

    closed() {
        popPage()
    }
}
</script>
<style lang='scss' scoped>
@import "~@/style/common-style.scss";
.page-background {
    background-color: $google-gray-400;
}
</style>
