
class ErrorCode {
    static SUCCESS = 'SUCCESS';
}

export const SUCCESS = 'SUCCESS';

class GlobalHandledRespCode {
    codeHandlerMapping: any = {};
    constructor() {
        this.codeHandlerMapping = {
            TOKEN_MISSING: () => {
                console.log('token missing')
            },
            SERVER_ERROR: () => {
                console.log('server error')
            },
        };
    }

    handle(code: string) {
        if (this.codeHandlerMapping[code] != null) {
            this.codeHandlerMapping[code]();
        } else {
            console.log(`Unhandled global response code: ${code}`)
        }
    }

    isGlobalHandled(code: string): boolean {
        return this.codeHandlerMapping[code] != null;
    }
}
export { ErrorCode };

export default new GlobalHandledRespCode();
