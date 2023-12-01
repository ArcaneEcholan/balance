<template>
    <button class="confirm-btn" @click="onclick">ledgers</button>
</template>

<script lang='ts'>
import { Component, Vue } from 'vue-property-decorator';
@Component({})
export default class CommonButtonComponent extends Vue {
    onclick($event: any) {
        this.$emit('click', $event)
    }
}
</script>
<style lang='scss' scoped>
.confirm-btn{
    background: none;
    border: none;
    outline: none;
    box-shadow: none;

    width: 100%;

    border-radius: 10px;
    padding: 10px 18px;
    background-color: #3574F0;

    font-weight: 600;
    color: white;
}
</style>
