<template>
    <div
        class="slide-panel transition"
        :id="modalElemUid"
        :style="` ${zIndex?('z-index: '+zIndex + ';'):''} `"
    >
        <div style="height: 100%">
            <slot>content</slot>
        </div>
    </div>
</template>

<script lang="ts">
import {Component, Prop, Vue} from 'vue-property-decorator';
import {v4 as uuidv4} from 'uuid';


@Component
export default class ModalPresentationView extends Vue {
    modal!: HTMLElement;
    modalWidth = 0;
    swipePercentage = 0.3;
    modalElemUid: string = uuidv4();

    @Prop({default: null})
    zIndex!: string | null;

    mounted() {
        this.registerSwipeRelatedEventHandler()
        this.openModal()
    }

    registerSwipeRelatedEventHandler() {
        this.modal = document.getElementById(this.modalElemUid) as HTMLElement;

        // Init width of the modal to page width
        this.modalWidth = window.innerWidth;

        // we give the modal a init size
        this.modal.style.right = -this.modalWidth + 'px'; // let it hide into the right side of the screen
        this.modal.style.width = this.modalWidth + 'px'; // give it a width

        // this might be confusing, together with the style of (#slidePanel) display: none;
        // the action of setting width and right will cause a quick flashing of the modal, so before setting the style, the modal has to be invisible
        // and after setting the style, we restore the display of the modal
        this.modal.style.display = 'block';

        const swipeAreaWidth = window.innerWidth * this.swipePercentage;

        // this is the width of the area where user swipes over the screen, direction is from left to right
        let userSwipePathWidth = 0;

        // this is the start point of the swipe action
        let swipeStartPoint = 0;

        // indicate if there is a swipe action
        let isThereASwipe = false;

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
                {
                    let modal = document.getElementById('app')!;
                    modal.classList.remove('tran')


                    modal = document.getElementById('tabbar-area')!;
                    modal.classList.remove('tran')

                    modal = document.getElementById('records-index-header')!;
                    modal.classList.remove('tran')
                }

                // record the swipe start point
                swipeStartPoint = touchClientX;

                isThereASwipe = true;

                console.debug('touch start');
            }
        });

        function setPageRight(num: number) {

        }

        this.modal.addEventListener('touchmove', (e) => {
            // this is very important, if we don't stop propagation, the touch event will be passed to the element below when there are nested modal
            e.stopPropagation();
            // prevent
            e.preventDefault();

            if (!isThereASwipe) {
                return;
            }

            // this is where the user's finger is, in this case, represents where the user's finger is moving to
            const touchClientX = e.changedTouches[0].clientX;

            userSwipePathWidth = touchClientX - swipeStartPoint;

            // prevent user to swipe the panel into the left side of the screen
            if (userSwipePathWidth >= 0) {
                console.debug('swipe width: ' + userSwipePathWidth);

                // how long should modal move from the beginning is the same as how long user's finger moves. For example if user swipe 20px, modal should move 20px from the beginning
                console.debug('new modal right: ' + -userSwipePathWidth);


                {
                    let modal = document.getElementById('app')!;
                    let r = 1 - userSwipePathWidth / this.modalWidth
                    let right = modal.style.right;
                    right = right.replace('px', '');
                    modal.style.right = r * 100 + 'px';


                    modal = document.getElementById('tabbar-area')!;
                    r = 1 - userSwipePathWidth / this.modalWidth
                    right = modal.style.right;
                    right = right.replace('px', '');
                    modal.style.right = r * 100 + 'px';



                    modal = document.getElementById('records-index-header')!;
                    r = 1 - userSwipePathWidth / this.modalWidth
                    right = modal.style.right;
                    right = right.replace('px', '');
                    modal.style.right = r * 100 + 'px';
                }


                this.setModalStyleRight(-userSwipePathWidth);
            }
        });

        this.modal.addEventListener('touchend', (e) => {
            // this is very important, if we don't stop propagation, the touch event will be passed to the element below when there are nested modal
            e.stopPropagation();

            if (isThereASwipe) {
                // restore the transition of modal
                this.modal.classList.add('transition');
                {
                    let modal = document.getElementById('app')!;
                    modal.classList.add('tran')

                    modal = document.getElementById('tabbar-area')!;
                    modal.classList.add('tran')

                    modal = document.getElementById('records-index-header')!;
                    modal.classList.add('tran')
                }


                swipeEndTime = new Date().getTime();

                // if the span user swipes is less than a certain time span limit, close the modal no matter where the modal is
                if (
                    swipeEndTime - swipeStartTime <= closeModalSwipeSpanLimitation
                    && userSwipePathWidth > 0
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

    increPageRight(num: number) {

    }

    openModal() {
        setTimeout(() => {


            let modal = document.getElementById('app')!;
            let right = modal.style.right;
            right = right.replace('px', '');
            modal.style.right = 100 + 'px';


             modal = document.getElementById('tabbar-area')!;
             right = modal.style.right;
            right = right.replace('px', '');
            modal.style.right = 100 + 'px';


            modal = document.getElementById('records-index-header')!;
            right = modal.style.right;
            right = right.replace('px', '');
            modal.style.right = 100 + 'px';

            this.modal.style.right = `0`;
        }, 0);
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

    beforeDestroy() {
        this.closeModal()
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
        return modalRightValue > this.modalWidth / 2;
    }


    closeModal() {

        {
            let modal = document.getElementById('app')!;
            modal.style.right = 0 + 'px';

            modal = document.getElementById('tabbar-area')!;
            modal.style.right = 0 + 'px';

            modal = document.getElementById('records-index-header')!;
            modal.style.right = 0 + 'px';

        }

        this.modal.style.right = `${-this.modalWidth}px`;
        this.$emit('on-close', this);
        setTimeout(() => {
            this.$emit('close-100', this);
        }, 100);
        setTimeout(() => {
            this.$emit('close-200', this);
        }, 200);
        setTimeout(() => {
            this.$emit('close-300', this);
        }, 300);
        setTimeout(() => {
            this.$emit('closed', this);
        }, 500);
    }

}
</script>
<style>


.slide-panel {
    display: none;
    position: fixed;
    top: 0;
    height: 100%;
    background-color: #fff;
}

.transition {
    transition: right 0.5s cubic-bezier(0, 1, 0, 1);
}

</style>
