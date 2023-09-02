<template>
    <!-- this is the modal -->
    <div
        class="slide-panel transition"
        :id="getContainerId()"
        :style="`display: none; z-index: 1900; ${backgroundColor?'background-color: '+backgroundColor + ';':''}`"
    >
        <div style="height: 100%">
            <slot>content</slot>
        </div>
    </div>
</template>

<script lang="ts">
import {Component, Prop, Vue} from 'vue-property-decorator';
import { v4 as uuidv4 } from 'uuid';
@Component
export default class ModalPresentationView extends Vue {
    modal!: HTMLElement;
    modalWidth = 0;
    swipePercentage = 0.3;
    uuid!: string;

    @Prop({default: null})
    backgroundColor!: string | null;

    viewDebug = false;

    /**
     * convenient function for closing modal style "right"
     */
    closeModal() {
        this.modal.style.right = `${-this.modalWidth}px`;
        setTimeout(() => {
            this.$emit('close', this);
        }, 500);
    }

    getContainerId() {
        return this.uuid;
    }

    /**
     * convenient function for opening modal style "right"
     */
    openModal() {
        this.modal.style.right = `0`;
        setTimeout(() => {
            this.$emit('open', this);
        }, 500);
    }

    /**
     * convenient function for getting modal style "right"
     *
     */
    modalStyleRight() {
        return Number(
            this.modal.style.right.substring(
                0,
                this.modal.style.right.length - 2,
            ),
        );
    }

    /**
     * convenient function for setting modal style "right"
     */
    setModalStyleRight(value: number) {
        this.modal.style.right = `${value}px`;
    }

    /**
     * Determine what should the modal do after user finish swiping, close or  * restore.
     *
     * If the modal is half body into the right side, close, otherwise restore.
     *
     * Invoked when user finish swipe(finger leave the screen).
     *
     */
    shouldModalClose() {
        let modalRightValue = Math.abs(this.modalStyleRight());
        if (modalRightValue > this.modalWidth / 2) {
            return true;
        }
        return false;
    }

    created() {
        this.uuid = uuidv4();
        console.debug(`new modal uuid: ${this.uuid}`);
    }

    mounted() {
        let pageWidth = window.innerWidth;
        let containerId = this.getContainerId();
        this.modal = document.getElementById(containerId) as HTMLElement;

        // Init width of the modal to page width
        this.modalWidth = pageWidth;

        console.debug(`modal width: ${this.modalWidth}`);

        // we give the modal a init size
        this.modal.style.right = -this.modalWidth + 'px'; // let it hide into the right side of the screen
        this.modal.style.width = this.modalWidth + 'px'; // give it a width

        // this might be confusing, together with the style of (#slidePanel) display: none;
        // the action of setting width and right will cause a quick flashing of the modal, so before setting the style, the modal has to be invisible
        // and after setting the style, we restore the display of the modal
        this.modal.style.display = 'block';

        // region: convenient function for manipulating modal
        // endregion
        const swipeAreaWidth = window.innerWidth * this.swipePercentage;
        console.debug('swipe area: ' + swipeAreaWidth);

        // this is the width of the area where user swipes over the screen, direction is from left to right
        let userSwipePathWidth = 0;

        // this is the start point of the swipe action
        let swipeStartPoint = 0;

        // indicate if there is a swipe action
        let isThereASwipe = false;

        // this is the time span limit of the swipe action, if the span is less than this value, we close the modal no matter where the modal is
        let closeModalSwipeSpanLimitation = 500;

        // this is the start time of the swipe action
        let swipeStartTime: number;

        // this is the end time of the swipe action
        let swipeEndTime;
        this.modal.addEventListener('touchstart', (e) => {
            // this is very important, if we don't stop propagation, the touch event will be passed to the element below when there are nested modal
            e.stopPropagation();
            const touch = e.changedTouches[0];

            // this is where the user's finger is, in this case, represents the swipe start point
            const touchClientX = touch.clientX;

            // if and only if the user's finger is in the swipe area, we start to record the swipe action
            if (touchClientX <= swipeAreaWidth) {
                swipeStartTime = new Date().getTime();
                // remove the transition of modal, because we need the modal to follow the user's finger without latency
                this.modal.classList.remove('transition');

                // record the swipe start point
                swipeStartPoint = touchClientX;

                isThereASwipe = true;

                console.debug('touch start');
            }
        });

        this.modal.addEventListener('touchmove', (e) => {
            // this is very important, if we don't stop propagation, the touch event will be passed to the element below when there are nested modal
            e.stopPropagation();

            // prevent
            e.preventDefault();
            if (isThereASwipe) {
                const touch = e.changedTouches[0];

                // this is where the user's finger is, in this case, represents where the user's finger is moving to
                const touchClientX = touch.clientX;

                userSwipePathWidth = touchClientX - swipeStartPoint;

                // prevent user to swipe the panel into the left side of the screen
                if (userSwipePathWidth >= 0) {
                    console.debug('touch move');
                    console.debug('swipe width: ' + userSwipePathWidth);

                    // how long should modal move from the beginning is the same as how long user's finger moves. For example if user swipe 20px, modal should move 20px from the beginning
                    let newModalRight = 0 - userSwipePathWidth;
                    console.debug('new modal right: ' + newModalRight);
                    this.setModalStyleRight(newModalRight);
                }
            }
        });

        this.modal.addEventListener('touchend', (e) => {
            // this is very important, if we don't stop propagation, the touch event will be passed to the element below when there are nested modal
            e.stopPropagation();

            if (isThereASwipe) {
                // restore the transition of modal
                this.modal.classList.add('transition');
                swipeEndTime = new Date().getTime();

                // if the span user swipes is less than a certain time span limit, close the modal no matter where the modal is
                if (
                    swipeEndTime - swipeStartTime <=
                    closeModalSwipeSpanLimitation &&
                    userSwipePathWidth > 0
                ) {
                    this.closeModal();
                    return;
                }

                // ok, may be user want to play with the modal, let him/her/it/helicopter do it, we handle the action of closing or restoring the modal in the next step
                if (this.shouldModalClose()) {
                    this.closeModal();
                } else {
                    this.openModal();
                }
                isThereASwipe = false;
            }
        });
    }
}
</script>
<style>

body {
    margin: 0;
    padding: 0;
    font-family: Arial, sans-serif;
    overflow-x: hidden;
}

.slide-panel {
    position: fixed;
    top: 0;
    height: 100%;
    background-color: #fff;
}

.slide-panel.transition {
    transition: right 0.5s cubic-bezier(0, 1, 0, 1);
}
</style>
