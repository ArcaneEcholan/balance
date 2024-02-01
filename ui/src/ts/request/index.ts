import axios from 'axios';
import { Notify } from 'vant';
import { PageLocation } from '@/ts/dynamicLocation';

// create an axios instance
const service = axios.create({
    // baseURL: '/api',
    baseURL: new PageLocation().baseURL, // url = base url + request url
    // withCredentials: true, // send cookies when cross-domain requests
    timeout: 0, // request timeout
});

function sleep(ms = 500): Promise<void> {
    console.log('Kindly remember to remove `sleep`');
    return new Promise((resolve) => setTimeout(resolve, ms));
}

// request interceptor
service.interceptors.request.use(
    async (config) => {
        if (process.env.NODE_ENV === 'development') {
            await sleep();
        }
        logRequest(config);
        return config;
    },
    (error) => {
        // do something with request error
        console.log(error); // for debug
        return Promise.reject(error);
    },
);

function logRequest(config: any) {
    let logtag = `Network Request: =====> ${config.url}\n`;
    console.log(logtag, config);
}

export class HttpResponse {
    status: number;
    data: any;
    constructor(status: number, data: any) {
        this.status = status;
        this.data = data;
    }
}

export class HttpError extends Error {
    resp: HttpResponse | null;
    constructor(msg: string | null, resp: HttpResponse | null) {
        super(`HTTP Error ${msg}`);
        this.resp = resp;
    }
}

// response interceptor
service.interceptors.response.use(
    (response) => {
        logSuccessResponse(response);

        return Promise.resolve(response);
    },
    (error): Promise<HttpError> => {
        console.log(error);
        let response = error.response;

        if (response == null) {
            Notify({ type: 'danger', message: `server unreachable` });
            return Promise.reject();
        }
        let status = response.status;
        let data = response.data;
        console.log({ type: 'danger', message: `${status}: ${data}` });
        let httpResp = new HttpResponse(status, data);
        return Promise.reject(new HttpError(error.message, httpResp));
    },
);

function logSuccessResponse(response: any) {
    const res = response.data;
    let relativeUrl = response.config.url!.substring(
        response.config.baseURL!.length,
    );
    let logtag = `Network Response: <==== ${relativeUrl}\n`;
    console.log(logtag, res);
}

export default service;
