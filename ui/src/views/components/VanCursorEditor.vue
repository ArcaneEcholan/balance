<template>
    <div class=''>
        <van-field
            :ref="id"
            @click="updateCurrentCursorPosition"
            @input="handleInputEvent"
            :value="value"
            label="Records (per / Line)"
            type="textarea"
            placeholder="Message"
            rows="2"
            autosize
        />
    </div>
</template>

<script lang='ts'>
import {Component, Prop, Vue} from 'vue-property-decorator';
import {v4 as uuidv4} from 'uuid';
import {getVueEl} from "@/ts/vueUtils";

@Component({})
export default class VanCursorEditorComponent extends Vue {
    cursorPosition: any = {
        absoluteCursorPosition: 0,
        cursorRow: 0,
        cursorColumn: 0
    }

    @Prop({default: ""})
    value!: string | null

    id = ""

    created() {
        this.id = uuidv4()
    }

    handleInputEvent(newV: string) {
        this.$emit("update:value", newV)
        this.updateCurrentCursorPositionWithValue(newV)
        this.$emit("input", newV)
    }

    updateCurrentCursorPosition() {
        this.updateCurrentCursorPositionWithValue(this.value)
    }

    updateCurrentCursorPositionWithValue(newV: string | null) {
        let result = {}
        if (newV == null) {
            result = {
                absoluteCursorPosition: 0,
                cursorRow: 0,
                cursorColumn: 0
            }
        } else {
            result = this.getCurrentCursorPosition(newV!)
        }

        this.cursorPosition = result
    }

    getCurrentCursorPosition(raw: string): any {
        let inputElement = this.getTextArea();
        let absoluteCursorPosition = inputElement.selectionStart;

        let before = raw.substring(0, absoluteCursorPosition!);
        let row = before.split('\n').length - 1;
        let col = absoluteCursorPosition! - before.lastIndexOf('\n') - 1;

        return {
            absoluteCursorPosition,
            cursorRow: row,
            cursorColumn: col,
        };
    }


    setCurrentCursorPosition(start: number) {
        let inputElement = this.getTextArea();
        inputElement.selectionStart = start
        this.updateCurrentCursorPosition()
    }


    getTextArea():HTMLTextAreaElement {
        let vantinput = getVueEl(this, this.id)
        return vantinput.querySelector('textarea') as HTMLTextAreaElement
    }

}
</script>
<style lang='scss' scoped>
</style>
