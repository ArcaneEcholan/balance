import cache from '@/ts/cache';
import request from '@/ts/request';
import store from '@/ts/store';
import eventBus from '@/ts/EventBus';
import Cache from '@/ts/cache';
import Client from '@/ts/request/client';

class Storage {
    purgeLedgerCache(ledgerName: string) {
        this.purgeStatisticsCacheByLedgerName(ledgerName);

        // purge ledger list cache
        cache.removeItem('ledgers');
    }

    purgeRecordsCacheByLedgerNameAndMonth(
        ledgerName: string,
        currentMonth: string,
    ) {
        const transcachekey = `transaction_list_${ledgerName}_${currentMonth}`;
        cache.removeItem(transcachekey);
    }

    purgeAllRecordsCache() {
        cache
            .filterPairs((cursor) => {
                let key = cursor.key as string;
                let regex = `transaction_list.*`;
                return new RegExp(regex).test(key);
            })
            .then((dataSet) => {
                dataSet.forEach((item: any) => {
                    cache.removeItem(item.key);
                });
            });
    }

    purgeStatisticsCacheByLedgerName(ledgerName: string) {
        // purge statistics data cache
        cache
            .filterPairs((cursor) => {
                let key = cursor.key as string;
                let regex = `^statistics_${ledgerName}_\\d{4,4}-\\d{1,2}$`;
                return new RegExp(regex).test(key);
            })
            .then((dataSet) => {
                dataSet.forEach((item: any) => {
                    cache.removeItem(item.key);
                });
            });
    }

    getDefaultLedger(): Promise<string> {
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

    getStatisticsData(
        ledgerId: number | null,
        ledgerName: string,
        yearMonth: string,
    ) {
        let cacheKey = (ledgerName) => {
            return `statistics_${ledgerName}_${yearMonth}`;
        };
        return new Promise((resolve, reject) => {
            Cache.getItem(cacheKey(ledgerName))
                .then((cv) => {
                    if (cv) {
                        resolve(cv);
                    } else {
                        Client.getStatisticsData(yearMonth, ledgerId)
                            .then((resp: any) => {
                                Cache.setItem(cacheKey(ledgerName), resp.data);
                                resolve(resp.data);
                            })
                            .catch((err) => {
                                reject(err);
                            });
                    }
                })
                .catch((err) => {
                    reject(err);
                });
        });
    }

    getRecords(ledgerName: string, yearMonth: string) {
        return new Promise((resolve, reject) => {
            let cacheKey = `transaction_list_${ledgerName}_${yearMonth}`;

            Cache.getItem(cacheKey)
                .then((cachedData: any) => {
                    if (cachedData) {
                        resolve(cachedData);
                    } else {
                        Client.getTransactionListByLedgerName(
                            ledgerName,
                            yearMonth,
                        )
                            .then((res) => {
                                Cache.setItem(cacheKey, res.data);
                                resolve(cachedData);
                            })
                            .catch((err) => {
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
