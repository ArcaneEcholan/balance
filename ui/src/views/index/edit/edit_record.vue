<template>
    <div>
        <modal-presentation
            :background-color="'#f7f8fa'"
            @close="closed"
            ref="modal"
        >
            <template #default>

                <div>
                    <el-input type="textarea" v-model="variableVisibleString">
                    </el-input>
                </div>
                <div class="header">
                    <div style="z-index: 10000000">{{ stackSize }}</div>
                    <div class="flex flex-center" style="background-color: #fff;
height: 56px;">
                        <div class="fs20 bold"
                             style="
                         text-transform: capitalize;">
                            Edit Record
                        </div>
                    </div>
                </div>
                <div class="pdl16 pdr16">
                    <div class="google-gray-400 capitalize">
                        <!--test-->
                    </div>
                </div>
                <div class="pdb16 pdt16"></div>
                {{
                    amount
                }}
                {{ datetime }}
                {{ count }}
                {{ description }}

                <van-cell-group inset>
                    <van-field v-model="amount" type="number" label="amount"/>
                    <van-field v-model="datetime" type="text" label="datetime"/>
                    <van-field v-model="count" type="digit" label="count"/>
                    <van-field v-model="description" type="text" label="description"/>
                </van-cell-group>
                <div class="pdb16 pdt16"></div>
                <div class="pdl16 pdr16">
                    <el-button round plain type="primary" style="width: 100%"
                    @click="submitCommit">Submit</el-button>
                </div>
            </template>
        </modal-presentation>
    </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import ModalPresentationView from "@/components/ModalPresentation.vue";
import {popPage} from "@/ts/pageStack";
import pageStack from "@/ts/pageStack";
import {Notify} from "vant";

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
    get stackSize() {
        return pageStack.getStackSize()
    }


    varTable: any = {}

     visibleVariable(varName:string, varValue: any) {
        this.varTable[varName] = varValue
        this.renderToString()
    }

     renderToString() {
        let keys = Object.keys(this.varTable)
        let result = ""
        for (let i = 0; i < keys.length; i++) {
            let key = keys[i]
            let value = this.varTable[key]
            result += `${key} = ${value}\n`
        }
        this.variableVisibleString = result
    }

    variableVisibleString: string | null = null

    amount: string | null = null
    datetime: string | null = null
    count: string | null = null
    description: string | null = null


    modal!: ModalPresentationView

    created() {




    }

    mounted() {
        this.modal = this.$refs.modal as ModalPresentationView;
        setTimeout(() => {
            this.modal.openModal()
        }, 1)

        setTimeout(() => {
            let recordId = this.$route.query.recordId
            this.visibleVariable("recordId", recordId)
            if(recordId != null) {

                return
            }

            Notify("page status invalid")
            setTimeout(() => {
                Notify.clear()
                this.$router.push("/")
            }, 1000)
        }, 300)

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
