class EventBus {
    private callbacks: { [name: string]: ((args: any) => void)[] };

    constructor() {
        this.callbacks = {};
    }

    $on(name: string, fn: (args: any) => void) {
        this.callbacks[name] = this.callbacks[name] || [];
        this.callbacks[name].push(fn);
    }

    $emit(name: string, args: any) {
        if (this.callbacks[name]) {
            this.callbacks[name].forEach((cb) => cb(args));
        }
    }
}

const eventBus = new EventBus();
export default eventBus;
