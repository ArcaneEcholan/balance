class Cache {
    whoKnowsTheName(reject) {
        let openRequest = indexedDB.open('site_idb', 1);

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
            db.createObjectStore('caches', { keyPath: 'key' });
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
                        resolve(event.target.result);
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
                    value: JSON.stringify(value),
                };
                let request = store.add(data);

                request.onsuccess = function () {
                    resolve(null);
                    console.log('Data added to the database');
                };

                request.onerror = function (errEvn) {
                    reject(
                        new Error(
                            `Error adding data to the database: ${errEvn.target.error.message}`,
                        ),
                    );
                };
            };
        });
    }
}

export default new Cache();
