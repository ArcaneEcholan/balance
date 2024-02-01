import request from '@/ts/request';
import store from '@/ts/store';
import eventBus from '@/ts/EventBus';
import Cache from '@/ts/cache';

export function getDefaultLedger() {
    return new Promise((resolve, reject) => {
        let afterGetUserConfigs = (userConfigs: any[]) => {
            let findLedgerName = () => {
                // find current ledger
                let defaultLedger = store.getters.userConfigs.find(
                    (it) => it.key === 'default_ledger',
                );

                if (defaultLedger != null) {
                    return defaultLedger.value;
                } else {
                    return null;
                }
            };

            let oldLedgerName = findLedgerName();
            if (oldLedgerName == null) {
                reject('no default ledger');
                return;
            }

            store.commit('user/SET_CONFIGS', userConfigs);

            let newLegerName = findLedgerName();
            if (newLegerName == null) {
                reject('no default ledger');
                return;
            }

            if (newLegerName != oldLedgerName) {
                eventBus.$emit('on-cur-ledger-changed', {
                    name: newLegerName,
                });
            }
            resolve(newLegerName);
        };

        let cacheKey = 'user_configs';
        Cache.getItem(cacheKey)
            .then((cachedData: any) => {
                if (cachedData) {
                    let userConfigs = cachedData;
                    afterGetUserConfigs(userConfigs);
                } else {
                    request({
                        url: '/user/configs',
                        method: 'get',
                        headers: {
                            'entity-token': store.getters.token,
                        },
                    })
                        .then((resp) => {
                            let userConfigs = resp.data;
                            return Cache.setItem(cacheKey, userConfigs);
                        })
                        .then((pair: any) => {
                            afterGetUserConfigs(pair.value);
                        })
                        .catch((it) => {
                            reject(it);
                        });
                }
            })
            .catch((err) => {
                reject(err);
            });
    });
}
