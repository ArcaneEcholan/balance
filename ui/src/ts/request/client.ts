import request from '@/ts/request/index';
import { AxiosPromise } from 'axios';
import store from '@/ts/store';

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
            url: `/transactions/${encodeURIComponent(ledgerName)}`,
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

    static getTransactionListByLedgerName(
        ledgerName: string,
        yearHyphenMonth: string,
    ) {
        return request({
            url: `/transactions`,
            method: 'get',
            params: { month: yearHyphenMonth, ledger_name: ledgerName },
        });
    }

    static updateTransaction(
        recordId: number | string | null,
        categoryValue: string,
        amount: string,
        datetime: string | null,
        count: string,
        description: string | null,
    ) {
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
            },
        });
    }

    static getTransactionCategories() {
        return request({
            url: `/transaction/category`,
            method: 'get',
        });
    }

    static addLedger(addLedgerName: string) {
        return request({
            url: `/ledger`,
            method: 'post',
            data: {
                name: addLedgerName,
            },
        });
    }

    static deleteLedger(ledgerId: number) {
        return request({
            url: `/ledger/${ledgerId}`,
            method: 'delete',
        });
    }

    static getStatisticsData(currentMonth: string, ledgerId: number | null) {
        return request({
            url: `/statistics`,
            method: 'get',
            params: { month: currentMonth, ledger_id: ledgerId },
            headers: {
                'entity-token': store.getters.token,
            },
        });
    }
}

export default Client;
