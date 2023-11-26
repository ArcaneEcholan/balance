<template>
  <div class=''>
    <div class="keyboard flex" style="height: 200px; width: 100%">
      <div class="flexg2 flex column">
        <div class="flex flexg1">
          <clickable @touchend="touchKeyBoardEnd" class=" keyboard-key">1</clickable>
          <clickable @touchend="touchKeyBoardEnd" class=" keyboard-key">2</clickable>
          <clickable @touchend="touchKeyBoardEnd" class=" keyboard-key">3</clickable>
        </div>
        <div class="flex flexg1">
          <clickable @touchend="touchKeyBoardEnd" class=" keyboard-key">4</clickable>
          <clickable @touchend="touchKeyBoardEnd" class=" keyboard-key">5</clickable>
          <clickable @touchend="touchKeyBoardEnd" class=" keyboard-key">6</clickable>
        </div>
        <div class="flex flexg1">
          <clickable @touchend="touchKeyBoardEnd" class=" keyboard-key">7</clickable>
          <clickable @touchend="touchKeyBoardEnd" class=" keyboard-key">8</clickable>
          <clickable @touchend="touchKeyBoardEnd" class=" keyboard-key">9</clickable>
        </div>
        <div class="flex flexg1">
          <clickable @touchend="touchKeyBoardEnd" class=" keyboard-key">.</clickable>
          <clickable @touchend="touchKeyBoardEnd" class=" keyboard-key">0</clickable>
          <clickable @touchend="touchKeyBoardEnd" class=" keyboard-key delete">
            <van-icon name="cross"/>
          </clickable>
        </div>
      </div>
      <div class="flexg1 flex column">
        <clickable :id="`arrow-up`" @touchend="touchKeyBoardEnd" class=" keyboard-key" @click="focusOnPreviousRow">
          <van-icon name="arrow-up"/>
        </clickable>
        <div class="flex flexg1">
          <clickable :id="`arrow-left`" @touchend="touchKeyBoardEnd" class=" keyboard-key"
                     @click="focusPrevious()">
            <van-icon name="arrow-left"/>
          </clickable>
          <clickable :id="`arrow-right`" @touchend="touchKeyBoardEnd" class=" keyboard-key" @click="focusNext()">
            <van-icon name="arrow"/>
          </clickable>
        </div>
        <clickable :id="`arrow-down`" @touchend="touchKeyBoardEnd" class=" keyboard-key" @click="focusOnNextRow">
          <van-icon name="arrow-down"/>
        </clickable>
        <clickable @touchend="touchKeyBoardEnd" class=" keyboard-key" @click="onClickOK">
          OK
        </clickable>
      </div>
    </div>
  </div>
</template>

<script lang='ts'>
import {Component, Vue} from 'vue-property-decorator';
import Clickable from "@/views/components/Clickable.vue";

@Component({
  components: {Clickable}
})
export default class KeyBoardComponent extends Vue {
  touchKeyBoardEnd(e: any) {
    let domKeyElem = e.target.closest(".keyboard-key")
    let keyObj = {
      label: "",
      value: ""
    }
    let key = domKeyElem.innerText
    if (key === "0" || key === "1" || key === "2" || key === "3" || key === "4" || key === "5" || key === "6" || key === "7" || key === "8" || key === "9") {
      keyObj.label = "number";
      keyObj.value = key;
    } else if (key === ".") {
      keyObj.label = "dot";
      keyObj.value = key;
    } else if (domKeyElem.classList.contains("delete")) {
      keyObj.label = "delete";
      keyObj.value = "";
    } else if (key === "OK") {
      keyObj.label = "confirm";
      keyObj.value = key;
    } else if (domKeyElem.id === "arrow-up") {
      keyObj.label = "arrow-up";
      keyObj.value = key;
    } else if (domKeyElem.id === "arrow-left") {
      keyObj.label = "arrow-left";
      keyObj.value = key;
    } else if (domKeyElem.id === "arrow-right") {
      keyObj.label = "arrow-right";
      keyObj.value = key;
    } else if (domKeyElem.id === "arrow-down") {
      keyObj.label = "arrow-down";
      keyObj.value = key;
    }
    this.$emit('key-touched', keyObj);
  }

  focusOnPreviousRow() {
    this.$emit('focus-on-previous-row');
  }

  focusOnNextRow() {
    this.$emit('focus-on-next-row');
  }

  focusPrevious() {
    this.$emit('focus-previous');
  }

  focusNext() {
    this.$emit('focus-next');
  }

  onClickOK() {
    this.$emit('on-click-ok');
  }
}
</script>
<style lang='scss' scoped>
@import "~@/style/common-style.scss";
@import "~@/style/style-specification.scss";

.keyboard {
  background-color: #ffffff;

  .keyboard-key {
    font-size: 20px;
    border: 1px solid #EBECF0;
    margin: -0.5px; // collapse border

    display: flex;
    justify-content: center;
    align-items: center;

    flex: 1;
  }

}

</style>
