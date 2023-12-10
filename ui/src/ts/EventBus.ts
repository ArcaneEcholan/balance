class EventBus {
    private callbacks: { [name: string]: ((args: any) => void)[] };

    constructor() {
        this.callbacks = {};
    }

    $off(name: string) {
        if (this.callbacks[name]) {
            this.callbacks[name] = [];
        }
    }

    $on(name: string, fn: (args: any) => void) {
        this.callbacks[name] = this.callbacks[name] || [];
        this.callbacks[name].push(fn);
    }

    $onIfNotExists(name: string, fn: (args: any) => void) {
        this.callbacks[name] = this.callbacks[name] || [];
        if (this.callbacks[name].length <= 0) {
            this.callbacks[name].push(fn);
        }
    }

    $emit(name: string, args: any) {
        if (this.callbacks[name]) {
            this.callbacks[name].forEach((cb) => cb(args));
        }
    }

    $emitWithReturnValue(name: string, args: any): any | null {
        if (this.callbacks[name]) {
            if (this.callbacks[name].length > 0) {
                return this.callbacks[name][0](args);
            } else {
                return null;
            }
        }
    }
}

const eventBus = new EventBus();
export default eventBus;
