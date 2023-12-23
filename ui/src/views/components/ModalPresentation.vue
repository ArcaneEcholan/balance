<template>
    <div
        class="slide-panel transition"
        :id="modalElemUid"
        :style="` ${zIndex ? 'z-index: ' + zIndex + ';' : ''} `"
    >
        <div style="height: 100%">
            <slot>content</slot>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { v4 as uuidv4 } from 'uuid';

@Component
export default class ModalPresentationView extends Vue {
    modal!: HTMLElement;
    modalWidth = 0;
    swipePercentage = 0.05;
    modalElemUid: string = uuidv4();

    @Prop({ default: null })
    zIndex!: string | null;

    @Prop({
        default: () => ({
            beforeSwipe: (arg: any) => {
                console.log('before swipe');
            },
            swiping: (arg: any) => {
                console.log('swiping');
            },
            afterSwipe: (arg: any) => {
                console.log('after swipe');
            },
            onOpen: (arg: any) => {
                console.log('on open');
            },
            opened: (arg: any) => {
                console.log('opened');
            },
            onClose: (arg: any) => {
                console.log('on close');
            },
            close100: (arg: any) => {
                console.log('close 100');
            },
            close200: (arg: any) => {
                console.log('close 200');
            },
            close300: (arg: any) => {
                console.log('close 300');
            },
            closed: (arg: any) => {
                console.log('closed');
            },
        }),
        required: false,
    })
    hooks!: any;

    created() {
        let requiredFuncs = [
            'beforeSwipe',
            'swiping',
            'afterSwipe',
            'onOpen',
            'opened',
            'onClose',
            'close100',
            'close200',
            'close300',
            'closed',
        ];

        let hooksObj = this.hooks;

        requiredFuncs.forEach((it) => {
            let hookPassedIn = hooksObj[it];
            if (hookPassedIn != null) {
                if (typeof hookPassedIn !== 'function') {
                    throw new Error(`hook: ${it} must be a function`);
                } else {
                    console.debug(`hook: ${it} exists`);
                }
            } else {
                console.debug(`hook: ${it} not exists`);
                hooksObj[it] = (arg: any) => {
                    console.debug(`default impl of hook: ${it}`);
                };
            }
        });
    }

    invokeCallbacks(event: string, arg: any) {
        switch (event) {
            case 'before-swipe': {
                this.hooks.beforeSwipe(arg);
                break;
            }
            case 'swiping': {
                this.hooks.swiping(arg);
                break;
            }
            case 'after-swipe': {
                this.hooks.afterSwipe(arg);
                break;
            }
            case 'on-open': {
                this.hooks.onOpen(arg);
                break;
            }
            case 'opened': {
                this.hooks.opened(arg);
                break;
            }
            case 'on-close': {
                this.hooks.onClose(arg);
                break;
            }
            case 'close-100': {
                this.hooks.close100(arg);
                break;
            }
            case 'close-200': {
                this.hooks.close200(arg);
                break;
            }
            case 'close-300': {
                this.hooks.close300(arg);
                break;
            }
            case 'closed': {
                this.hooks.closed(arg);
                this.$emit('closed', arg);
                break;
            }
            default: {
                console.warn('modal unknown event: ' + event);
            }
        }
    }

    mounted() {
        this.registerSwipeRelatedEventHandler();
        this.openModal();
    }

    registerSwipeRelatedEventHandler() {
        this.modal = document.getElementById(this.modalElemUid) as HTMLElement;

        // Init width of the modal to page width
        this.modalWidth = window.innerWidth;

        // we give the modal a init size
        this.modal.style.right = -this.modalWidth + 'px'; // let it hide into the right side of the screen
        this.modal.style.width = this.modalWidth + 'px'; // give it a width

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
                e.preventDefault();
                swipeStartTime = new Date().getTime();
                // remove the transition of modal, because we need the modal to stay tight with the user's finger without latency
                {
                    this.invokeCallbacks('before-swipe', this);
                    this.modal.classList.remove('transition');
                }

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
            // e.preventDefault();

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

                this.invokeCallbacks('swiping', {
                    swipingPathPercent: userSwipePathWidth / this.modalWidth,
                });

                this.setModalStyleRight(-userSwipePathWidth);
            }
        });

        this.modal.addEventListener('touchend', (e) => {
            // this is very important, if we don't stop propagation, the touch event will be passed to the element below when there are nested modal
            e.stopPropagation();

            if (isThereASwipe) {
                // restore the transition of modal
                this.modal.classList.add('transition');
                this.invokeCallbacks('after-swipe', null);

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

    increPageRight(num: number) {}

    openModal() {
        setTimeout(() => {
            this.invokeCallbacks('on-open', null);
            this.modal.style.right = `0`;
            setTimeout(() => {
                this.invokeCallbacks('opened', null);
            }, 500);
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

    func() {}

    beforeDestroy() {
        // this.closeModal()
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
        this.invokeCallbacks('on-close', null);
        this.modal.style.right = `${-this.modalWidth}px`;
        setTimeout(() => {
            this.invokeCallbacks('close-100', null);
        }, 100);
        setTimeout(() => {
            this.invokeCallbacks('close-200', null);
        }, 200);
        setTimeout(() => {
            this.invokeCallbacks('close-300', null);
        }, 300);
        setTimeout(() => {
            this.invokeCallbacks('closed', null);
        }, 500);
    }
}
</script>
<style lang="scss">
.slide-panel {
    position: fixed;
    top: 0;
    height: 100%;
    background-color: #fff;
}

.transition {
    transition: right var(--transition-duration) var(--transition-easing);
}
</style>
