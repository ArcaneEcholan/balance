import cache from '@/ts/cache';
import request from '@/ts/request';
import store from '@/ts/store';

class Storage {
    getLedgers(): Promise<any[]> {
        return new Promise((resolve, reject) => {
            cache
                .getItem('ledgers')
                .then((cv: any) => {
                    if (cv) {
                        resolve(cv);
                    } else {
                        request({
                            url: `/ledgers`,
                            method: 'get',
                            headers: {
                                'entity-token': store.getters.token,
                            },
                        })
                            .then((resp: any) => {
                                cache.setItem('ledgers', resp.data);
                                resolve(resp.data);
                            })
                            .catch((err: any) => {
                                reject(err);
                            });
                    }
                })
                .catch((err) => {
                    reject(err);
                });
        });
    }
}

export default new Storage();
