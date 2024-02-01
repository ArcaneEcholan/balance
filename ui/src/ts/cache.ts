class Cache {
    whoKnowsTheName(reject) {
        let openRequest = indexedDB.open('site_idb', 2);

        openRequest.onerror = function (event) {
            reject(
                new Error(
                    // @ts-ignore
                    'Open Database error: ' + event.target.error.message,
                ),
            );
        };

        openRequest.onupgradeneeded = function (event) {
            // @ts-ignore
            let db = event.target.result;
            let names = db.objectStoreNames;
            if (names == null || names.length === 0) {
                db.createObjectStore('caches', {
                    keyPath: 'key',
                });
            } else {
                if (names.contains('caches')) {
                    db.deleteObjectStore('caches');
                    db.createObjectStore('caches', {
                        keyPath: 'key',
                    });
                }
            }
        };

        return openRequest;
    }

    getItem(key: string) {
        return new Promise((resolve, reject) => {
            let openRequest = this.whoKnowsTheName(reject);
            openRequest.onsuccess = function (event) {
                // @ts-ignore
                let db = event.target.result;
                let transaction = db.transaction(['caches'], 'readonly');
                let objectStore = transaction.objectStore('caches');
                let getRequest = objectStore.get(key);

                getRequest.onerror = function (event) {
                    reject(
                        new Error(
                            'Error reading data from the database: ' +
                                // @ts-ignore
                                event.target.error.message,
                        ),
                    );
                };

                getRequest.onsuccess = function (event) {
                    if (event.target.result) {
                        resolve(event.target.result.value);
                    } else {
                        resolve(null); // Resolve with null if no data is found
                    }
                };
            };
        });
    }

    setItem(key: string, value: any) {
        return new Promise((resolve, reject) => {
            let openRequest = this.whoKnowsTheName(reject);
            openRequest.onsuccess = function (event) {
                // @ts-ignore
                let db = event.target.result;
                let transaction = db.transaction(['caches'], 'readwrite');
                let store = transaction.objectStore('caches');

                let data = {
                    key: key,
                    value: value,
                };
                let request = store.put(data);

                request.onsuccess = function () {
                    resolve({
                        key,
                        value,
                    });
                    console.log('Data set to the database');
                };

                request.onerror = function (errEvn) {
                    reject(
                        new Error(
                            `Error setting data to the database: ${errEvn.target.error.message}`,
                        ),
                    );
                };
            };
        });
    }

    getAllKeys(): Promise<any[]> {
        return new Promise((resolve, reject) => {
            let openRequest = this.whoKnowsTheName(reject);
            openRequest.onsuccess = function (event) {
                // @ts-ignore
                let db = event.target.result;

                let transaction = db.transaction(['caches'], 'readonly');
                let store = transaction.objectStore('caches');
                let request = store.getAllKeys();

                request.onerror = function (event) {
                    reject(
                        new Error(
                            'Error retrieving keys from the database: ' +
                                event.target.error.message,
                        ),
                    );
                };

                request.onsuccess = function (event) {
                    resolve(event.target.result);
                };
            };
        });
    }

    removeItem(key) {
        return new Promise<void>((resolve, reject) => {
            let openRequest = this.whoKnowsTheName(reject);
            openRequest.onsuccess = function (event) {
                // @ts-ignore
                let db = event.target.result;

                let transaction = db.transaction(['caches'], 'readwrite');
                let store = transaction.objectStore('caches');
                let request = store.delete(key);

                request.onerror = function (event) {
                    reject(
                        new Error(
                            'Error deleting item from the database: ' +
                                event.target.error.message,
                        ),
                    );
                };

                request.onsuccess = function () {
                    resolve();
                };
            };
        });
    }
}

export default new Cache();
