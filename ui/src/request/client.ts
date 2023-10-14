import request from '@/request';
import {AxiosPromise} from 'axios';

class Client {
    static saveTransactions(trans: any[]) {
        return request({
            url: `/transactions`,
            method: 'post',
            data: {
                transactionList: trans,
            },
        });
    }

    static saveTransactionsByLedgerName(ledgerName: string, trans: any[]) {
        return request({
            url: `/transactions/${ledgerName}`,
            method: 'post',
            data: {
                transactionList: trans,
            },
        });
    }

    // region: Playground Project Template
    static createPlaygroundProjectTemplateFromDirectory(
        path: string,
        name: string,
    ): AxiosPromise<any> {
        return request({
            url: `/playground-project-template/from-directory`,
            method: 'post',
            data: {
                path,
                name,
            },
        });
    }
    static uploadPlaygroundProjectTemplate(
        formData: FormData,
    ): AxiosPromise<any> {
        return request({
            url: `/playground-project-template`,
            method: 'post',
            data: formData,
        });
    }
    static updatePlaygroundProjectTemplateBinary(
        playgroundProjectTemplateId: number,
        formData: FormData,
    ): AxiosPromise<any> {
        return request({
            url: `/playground-project-template-binary/${playgroundProjectTemplateId}`,
            method: 'put',
            data: formData,
        });
    }

    static deletePlaygroundProjectTemplate(
        playgroundProjectTemplateId: number,
    ): AxiosPromise<any> {
        return request({
            url: `/playground-project-template/${playgroundProjectTemplateId}`,
            method: 'delete',
        });
    }

    static getPlaygroundProjectTemplateList(): AxiosPromise<any> {
        return request({
            url: `/playground-project-templates`,
            method: 'get',
        });
    }

    static downloadPlaygroundProjectTemplate(
        playgroundProjectTemplateId: number,
    ): AxiosPromise<any> {
        return request({
            url: `/playground-project-template/${playgroundProjectTemplateId}`,
            method: 'get',
            params: { playgroundProjectTemplateId },
            responseType: 'blob',
        });
    }
    // endregion
    static getTransactionList(yearHyphenMonth: string) {
        return request({
            url: `/transactions`,
            method: 'get',
            params: { month: yearHyphenMonth },
        });
    }

    static getTransactionListByLedgerName(ledgerName: string, yearHyphenMonth: string) {
        return request({
            url: `/transactions/${ledgerName}`,
            method: 'get',
            params: { month: yearHyphenMonth },
        });
    }

    static updateTransaction(recordId: number | string | null,
                                categoryValue: string,
                             amount: string,
                             datetime: string | null,
                             count: string,
                             description: string | null) {
        return request({
            url: `/transaction`,
            method: 'put',
            data: {
                transactionId: recordId,
                categoryValue,
                amount,
                datetime,
                count,
                description,
            }
        });
    }


    static getTransactionCategories() {
        return request({
            url: `/transaction/category`,
            method: 'get',
        });
    }

    static getLedgerList() {
        return request({
            url: `/ledgers`,
            method: 'get',
        });
    }

    static addLedger(addLedgerName: string) {
        return request({
            url: `/ledger`,
            method: 'post',
            data: {
                name: addLedgerName,
            }
        });
    }
}

export default Client;
