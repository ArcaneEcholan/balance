import request from '@/ts/request';
import store from '@/ts/store';
import eventBus from '@/ts/EventBus';

export function getDefaultLedger() {
    return new Promise((resolve, reject) => {
        request({
            url: '/user/configs',
            method: 'get',
            headers: {
                'entity-token': store.getters.token,
            },
        })
            .then((resp) => {
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

                store.commit('user/SET_CONFIGS', resp.data);

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
            })
            .catch((it) => {
                reject(it);
            });
    });
}
