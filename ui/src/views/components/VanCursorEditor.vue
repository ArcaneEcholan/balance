<template>
    <div class=''>
        <van-field
            :ref="id"
            @click="test"
            @input="test1"
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

    test() {
        this.updateCurrentCursorPosition(this.value ?? "")
    }

    test1(newV: string) {
        this.updateCurrentCursorPosition(newV)
        this.$emit("input", newV)
    }

    updateCurrentCursorPosition(newV: string) {
        this.$emit("update:value", newV)
        let vantinput = (this.$refs[this.id] as any).$el
        let inputElement = vantinput.querySelector('textarea') as HTMLTextAreaElement;
        let result = {}
        if (newV == null) {
            result = {
                absoluteCursorPosition: 0,
                cursosrRow: 0,
                cursorColumn: 0
            }
        } else {
            result = this.getCurrentCursorPosition(newV!, inputElement)
        }

        this.cursorPosition = result
    }

    getCurrentCursorPosition(raw: string, inputElement: HTMLTextAreaElement): any {
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


}
</script>
<style lang='scss' scoped>
</style>
