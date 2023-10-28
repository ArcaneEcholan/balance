import {Component, Vue} from 'vue-property-decorator';
import eventBus from "@/ts/EventBus";

export function provideListeners(vue: any, mappings: any[]) {
    vue.eventListeners = mappings
    vue.listen()
}

@Component({})
export default class PageEventbusRegistrationMixin extends Vue {

    eventListeners :any[]= []


    listen() {
        let em = this.eventListeners

        if (em != null && em.length != 0) {
            em.forEach((item: any) => {
                eventBus.$on(item.eventName, item.handler)
            })
        }
    }

    destroyed() {
        this.offListen()
    }

    offListen() {
        let em = this.eventListeners
        if (em != null && em.length != 0) {
            em.forEach((item: any) => {
                eventBus.$off(item.eventName)
            })
        }
    }
}
