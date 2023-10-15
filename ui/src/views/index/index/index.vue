<template>
    <div class="page" style="position: relative; height: 100%;">
        <div style="overflow-y: scroll;" v-loading="ledgerTransactionsLoading">

            <!--edit stack-->
            <router-view>
            </router-view>

            <van-dropdown-menu ref="menuRef">
                <van-dropdown-item :title="currentLedger.name" ref="ledgerSelection">
                    <van-cell-group>
                        <van-cell @click="onClickSwitchLedger(ledger)" v-for="ledger in ledgerList"
                                  :title="ledger.name">
                        </van-cell>
                    </van-cell-group>
                    <div style="padding: 5px 16px;">
                        <el-button @click="onClickManageLedgerList" round plain type="primary" style="width: 100%">
                            <i class="el-icon-edit">manage ledger</i>
                        </el-button>
                    </div>
                </van-dropdown-item>
            </van-dropdown-menu>


            <div>
                <!--region: amap-->
                <!--<div class="record-header">Amap</div>-->

                <!--<div style="position: relative" class="bg-white br8 shadow overflow-hidden">-->
                <!--    <div id="amap" style="width: 100%; height: auto">-->
                <!--    </div>-->
                <!--    <div class="fake-marker"></div>-->
                <!--</div>-->
                <!--endregion-->


                <div class="mgb20"></div>
                <div class="record-header">Location information
                    <el-button ref="refresh-btn" @click="onClickRefreshLocationData">
                        <van-icon name="replay"/>
                    </el-button>
                </div>
                <van-cell-group v-loading="locationLoading" class="shadow overflow-hidden br8 ">
                    <van-cell title="Coordinate (lat, lng)"
                              :value="`(${geoLocation.latitude}, ${geoLocation.longitude})`"/>
                    <van-cell title="Overview" :value="`${geoLocation.formattedName}`"/>
                </van-cell-group>

                <div class="mgb20"></div>

                <template v-for="type in transactionCategories">
                    <van-tag
                        class="mgr8 mgb8 pdr8 pdl8"
                        style="line-height: 24px"
                        type="primary"
                        round
                        @click="onClickOneType(type.value)"
                    >
                        {{ type.value }}
                    </van-tag>
                </template>

                <!--region: input area-->
                <div class="record-header">Add Some Record</div>


                <!--row: {{ cursorPosition.cursorRow }}, col: {{ cursorPosition.cursorColumn }}-->
                <!--Input-->

                <van-cursor-editor
                    ref="recordInput"
                    :value.sync="rawFormatString"
                    @input="parseInputStringToObjects"
                >
                </van-cursor-editor>


                <div class="mgb8"></div>

                <!--region: Preview-->
                <div class="record-header" v-show="parsedForms.length > 0">Preview</div>
                <div style="" class="shadow br8 overflow-hidden">
                    <template v-for="(form, index) in parsedForms">
                        <div class="flex pd10"
                             :style="
                                 `border-bottom: 1px solid #f5f5f5;
                                 ${index === parsedForms.length - 1 ? 'border-bottom: none; border-bottom-left-radius: 5px;border-bottom-right-radius: 5px;' : ''};
                                 background-color: white;
                                 `">
                            <div class="flexg5">
                                <div>
                                    <span>{{ form.categoryValue ? form.categoryValue.value : "" }}</span>
                                </div>
                                <div class="fs14">
                                    <span class="google-gray-400">{{ form.count ? form.count.value : "" }}</span>
                                    <span class="pd5 google-gray-300">|</span>
                                    <span class="google-gray-400">{{
                                            form.description ? form.description.value : ""
                                        }}</span>
                                </div>
                            </div>
                            <div class="flexg1">
                                <span class="bold">{{ form.amount ? form.amount.value : "" }}</span>
                            </div>
                        </div>
                    </template>
                </div>
                <!--endregion-->

                <div class="mgb8"></div>

                <!--endregion-->

                <el-button @click="onAddTrans" round plain type="primary" style="width: 100%">
                    <i class="el-icon-plus"></i>
                </el-button>

                <div class="mgb8"></div>

                <!--header-->
                <div class="record-header" v-show="transactionList.length > 0">This month</div>

                <!--region: this month-->
                <div style="" class="shadow br8 overflow-hidden">
                    <!--list-->
                    <template v-for="(form, index) in transactionList">
                        <!--list item-->
                        <div class="flex pdt10 pdb10 pdl16 pdr16"
                             @click="toEditTransactionPage(form.id)"
                             :style="
                     `border-bottom: 1px solid #f5f5f5;
                     background-color: white;
                     `
                ">
                            <!--card left-->
                            <div class="flexg5">
                                <div>
                                    <span class="pdr10">{{ form.categoryValue }}</span>
                                    <span class="fs14 google-gray-400">test location</span>
                                </div>
                                <div class="fs14">
                                    <span class="google-gray-400">{{ form.datetime | formatTimeForRecordItem }}</span>
                                    <span class="pd5 google-gray-300">|</span>
                                    <span class="google-gray-400">{{ form.description }}</span>
                                </div>
                            </div>
                            <!--card right-->
                            <div class="flexg1">
                            <span class="bold">{{ form.amount }}  {{
                                    `${form.count > 1 ? 'x ' + form.count : ''}`
                                }}</span>
                            </div>
                        </div>
                    </template>
                </div>
                <!--endregion-->


            </div>
        </div>
    </div>

</template>
<script lang="ts">
import AMapLoader from '@amap/amap-jsapi-loader';
import {Component, Vue} from 'vue-property-decorator';
import {Notification} from 'element-ui';
import Client from '@/request/client';
import request from '@/request';
import {convertToShortDateTime, findWordInLine, replace} from "@/ts/utils";
import {gotoPage} from "@/ts/pageStack";
import eventBus from "@/ts/EventBus";
import {Notify} from "vant";
import {getCurrentYearAndMonth} from '@/ts/time';
import {getRef, getVueEl} from "@/ts/vueUtils";
import VanCursorEditor from "@/views/components/VanCursorEditor.vue";
import VanCursorEditorComponent from "@/views/components/VanCursorEditor.vue";

(window as any)._AMapSecurityConfig = {
    securityJsCode: '172c59e3fd1b621adddca8f268ff879a',
};

class FormItemField {
    value: string | null = null;
    key: string | null = null;
    isValid: string | null = null;
}

class FormItem {
    categoryValue: FormItemField | null = null;
    amount: FormItemField | null = null;
    count: FormItemField | null = null;
    description: FormItemField | null = null;
}

Component.registerHooks([
    'beforeRouteEnter',
    'beforeRouteLeave',
    'beforeRouteUpdate',
]);

@Component({
    components: {VanCursorEditor},
    filters: {
        formatTimeForRecordItem: function (timeString: string | null) {
            if (!timeString) {
                return 'unknown datetime'
            }
            return convertToShortDateTime(timeString);
        }
    }
})
export default class IndexView extends Vue {
    transactionCategories: any[] = []

    ledgerTransactionsLoading = false
    currentLedger = {id: null, name: "default"}

    apiInvokingTimesSaver: any = null
    ledgersLoading = false
    ledgerList = []

    transactionList: any[] = [];
    amap: any;
    amapGeolocationPlugin: any;
    rawFormatString: string | null = null;
    parsedForms: FormItem[] = [];
    geoLocation: any = {
        latitude: null,
        longitude: null,
        formattedName: null
    };

    onClickRefreshLocationData() {
        this.getCurrentLocation(this.amap)
    }

    created() {
        this.registerEvents()

        this.initTransactionTypes();

        this.initLedgersAndTransactions();

        this.loadAmap()
    }

    registerEvents() {
        eventBus.$onIfNotExists('afterTransactionChanged', (newTransaction: any) => {
            this.updateTransaction(newTransaction)
        });

        eventBus.$onIfNotExists('ledges-changes', (list) => {
            this.ledgerList = list
        })

        eventBus.$onIfNotExists('ledger-deleted', (id) => {
            this.ledgerList = this.ledgerList.filter((item: any) => {
                return item.id !== id
            })
            if (this.currentLedger.id == id) {
                if (this.ledgerList.length != 0) {
                    this.currentLedger = this.ledgerList[0]
                } else {
                    // this is undefined behavior
                    this.currentLedger = {id: null, name: "unknown"}
                }
            }
        })
    }

    updateTransaction(newTransaction
                          :
                          any
    ) {
        let found = this.transactionList.find((item) => {
            return item.id === newTransaction.id
        })
        if (found) {
            found.amount = newTransaction.amount
            found.categoryValue = newTransaction.categoryValue
            found.count = newTransaction.count
            found.datetime = newTransaction.datetime
            found.description = newTransaction.description
        }
    }

    initTransactionTypes() {
        Client.getTransactionCategories().then(resp => {
            this.transactionCategories = resp.data
        })
    }

    initLedgersAndTransactions() {
        this.ledgersLoading = true
        Client.getLedgerList().then((resp: any) => {
            this.ledgersLoading = false
            this.ledgerList = resp.data
            eventBus.$emit('ledges-changes', this.ledgerList)
            return Client.getTransactionListByLedgerName(this.currentLedger.name, this.nowadays())
        }).then(resp => {
            this.ledgersLoading = false
            this.transactionList = resp.data
        }).catch((err: any) => {
            this.ledgersLoading = false
            console.error(err)
        })
    }

    loadAmap() {
        this.doLoadAmap((AMap: any) => {

            // this.mountAmap(AMap)
            //
            // this.registerAmapMoveEvent()

            this.getCurrentLocation(AMap)

        }, (e: any) => {
            console.log(e);
        })
    }

    doLoadAmap(success: any, fail: any) {
        AMapLoader.load({
            key: '8375fa99f0a19ba66b137bddcb2fceac', // 申请好的Web端开发者Key，首次调用 load 时必填
            version: '2.0', // 指定要加载的 JS API 的版本，缺省时默认为 1.4.15
            plugins: [], // 需要使用的的插件列表，如比例尺'AMap.Scale'等
        })
            .then((AMap) => {
                success(AMap)
            })
            .catch((e) => {
                fail(e)
            });
    }

    mountAmap(AMap: any) {
        this.amap = new AMap.Map('amap', {
            zoom: 16,
        });
    }

    registerAmapMoveEvent() {
        this.amap.on('mapmove', () => {
            let {lat, lng} = this.amap.getCenter()
            this.updateCor(lat, lng)
            clearTimeout(this.apiInvokingTimesSaver)
            this.apiInvokingTimesSaver = setTimeout(() => {
                this.getAndUpdateDetailLocationData()
            }, 500)
        })
    }


    locationLoading = false

    getCurrentLocation(AMap: any) {
        if (this.amapGeolocationPlugin == null) {
            this.loadAmapGeolocationPlugin(AMap, () => {
                this.doGetCurrentLocation()
            })
        } else {
            this.doGetCurrentLocation()
        }
    }

    loadAmapGeolocationPlugin(AMap: any, cb: any) {
        AMap.plugin('AMap.Geolocation', () => {
            let geolocation = new AMap.Geolocation();
            this.amapGeolocationPlugin = geolocation;
            cb()
        });
    }

    doGetCurrentLocation() {
        this.locationLoading = true
        this.amapGeolocationPlugin.getCurrentPosition(
            this.onSuccessGetCurrentPosition,
            this.onFailedGetCurrentPosition
        )
    }

    onSuccessGetCurrentPosition(status: any, result: any) {
        this.locationLoading = false
        if (status === 'complete') {
            this.ifSuccessGetCurrentPosition(result)
        } else {
            console.error(`fail to get geolocation, status: `, status, `result: `, result);
            Notify({
                message: 'Fail to locate your position',
                type: 'danger'
            })
        }
    }

    ifSuccessGetCurrentPosition(result: any) {
        this.locationLoading = false
        let position = result.position;
        this.updateCor(position.lat, position.lng)
        this.getAndUpdateDetailLocationData()
    }

    getAndUpdateDetailLocationData() {
        let lat = this.geoLocation.latitude
        let lng = this.geoLocation.longitude
        this.getDetailLocation(lat, lng,
            (locationDetail: any) => {
                // get information we need from detail response
                this.getInfoFromAmapLocationDetail(locationDetail);
            },
            (respBodyOrError: any) => {
                console.error(`fail to get location detail: `, respBodyOrError)
                Notify({
                    message: 'Fail to get location detail',
                    type: 'warning'
                })
            }
        )
    }

    getDetailLocation(lat: number, long: number, successCb: (location: any) => void, failCb: (respBodyOrError: any) => void) {
        request({
            url: '//restapi.amap.com/v3/geocode/regeo',
            method: 'get',
            params: {
                key: '0802cbb201f7d81e7ef6ae54d849dc2f',
                location: `${long},${lat}`,
                extensions: 'base',
                roadlevel: 0,
                output: 'json',
            },
        }).then((res) => {
            let responseData = res.data;
            let code = responseData.infocode;

            if (code == '10000') {
                successCb(responseData)
            } else {
                failCb(responseData)
            }
        }).catch((err) => {
            failCb(err)
        })
    }

    getInfoFromAmapLocationDetail(amapDetailLocationResp: any): any {
        let finalLocationInfo = this.getFormattedNameFromLocationDetail(amapDetailLocationResp)
        // assign details
        this.geoLocation = Object.assign(this.geoLocation, finalLocationInfo);
        console.debug(`get location detail: `, this.geoLocation)
    }


    getFormattedNameFromLocationDetail(amapDetailLocationResp: any) {
        let finalLocationInfo: any = {}
        finalLocationInfo.formattedName = this.returnBlankStringIfEmptyArray(amapDetailLocationResp.regeocode.formatted_address);

        if (finalLocationInfo.formattedName === "") {
            Notify(
                {
                    type: "danger",
                    message: "Your location is out of the service of amap "
                }
            )
            finalLocationInfo.formattedName = 'out of service'
        }
        return finalLocationInfo
    }

    returnBlankStringIfEmptyArray(entry: any): string {
        // this is the format of the response entry of amap api,
        // an empty array means a not existing entry value( the same meaning as null )

        if (Array.isArray(entry) && entry.length === 0) {
            return "";
        }
        return entry;
    }

    onFailedGetCurrentPosition(error: any) {
        Notify({
            message: `fail to get geollocation: ${error.code}-${error.message}`,
            type: 'danger'
        })
        console.error(`fail to get geollocation: ${error.code}-${error.message}`);
    }

    updateCor(lat: string | number, lng: string | number) {
        this.geoLocation.latitude = lat
        this.geoLocation.longitude = lng
    }

    mounted() {

        let a = getVueEl(this, "refresh-btn")
        a.style.padding = "4px";
        this.adjustAMapSize()
    }

    adjustAMapSize() {
        let amapElem: HTMLElement = document.getElementById("amap")!
        let amapWidth = amapElem.clientWidth;
        amapElem.style.height = amapWidth + 'px'
    }

    onClickSwitchLedger(ledger: any) {
        this.ledgerTransactionsLoading = true
        Client.getTransactionListByLedgerName(ledger.name, this.nowadays()).then((res) => {
            this.transactionList = res.data
            this.currentLedger = ledger
            this.ledgerTransactionsLoading = false;
            this.toggleLedgerSelection()
        }).catch((err) => {
            this.ledgerTransactionsLoading = false
        })
    }

    onClickManageLedgerList() {
        this.toggleLedgerSelection()
        this.present(`manage_ledger`, {})
    };

    toggleLedgerSelection() {
        getRef(this, "ledgerSelection").toggle()
    }

    toEditTransactionPage(recordId: string | number) {
        // find the record
        let foundTrans = this.transactionList.find((item) => {
            return item.id === recordId
        })

        if (!foundTrans) {
            return
        }

        // this.present(`/index/edit?recordId=${recordId}`)
        this.present(`edit_transaction`, {
            id: foundTrans.id,
            amount: foundTrans.amount,
            datetime: foundTrans.datetime,
            count: foundTrans.count,
            categoryValue: foundTrans.categoryValue,
            description: foundTrans.description
        })
    }

    present(viewName: string, data: any) {
        gotoPage(false, viewName, data)
        // pushPageWithName(viewName, data)
    }

    onAddTrans() {
        this.checkAddTransData()

        this.addTransactions(this.parsedForms);
    }

    checkAddTransData() {
        try {
            this.doCheckAddTransData()
        } catch (e: any) {
            Notify({
                message: e.message,
                type: 'danger'
            })
        }
    }

    doCheckAddTransData() {
        let errorType = ""
        if (this.parsedForms.length == 0) {
            errorType = "No data to save"
            throw new Error(errorType)
        }
        let error = false
        let parseForms = this.parsedForms
        parseForms.forEach((item: FormItem) => {
            if (error) {
                throw new Error(errorType)
            }
            let categoryValue = item.categoryValue?.value;
            let amount = item.amount?.value;
            let count = item.count?.value;
            let description = item.description?.value;

            // ensure no null
            if (categoryValue == null || amount == null || count == null || description == null) {
                error = true
                errorType = "Information not complete"
                throw new Error(errorType)
            }

            let countValid = this.isPositiveInteger(Number(count));
            let amountValid = this.isFloat(Number(amount));
            let amountDecimalPlaces = this.countDecimalPlaces(amount);
            if (amountDecimalPlaces > 2) {
                amountValid = false
            }

            if (!countValid || !amountValid) {
                error = true
                errorType = "Information format invalid"
                throw new Error(errorType)
            }
        })
        // @ts-ignore
        if (error === true) {
            Notification.error(errorType)
            throw new Error(errorType)
        }
    }

    isPositiveInteger(number: number): boolean {
        return Number.isInteger(number) && number > 0;
    }

    isFloat(number: number) {
        return Number(number) === number && !Number.isInteger(number) || Number.isInteger(number);
    }

    countDecimalPlaces(number: string) {
        if (number === '') {
            return 0; // Not a valid float string
        }

        const parts = number.split('.');

        if (parts.length === 1) {
            return 0; // No decimal point found
        }

        return parts[1].length; // Return the length of the decimal part
    }

    addTransactions(trans: any[]) {
        let request = this.assembleAddTransactionRequest(trans)

        Client.saveTransactionsByLedgerName(this.currentLedger.name, request).then((resp) => {
            Notify({
                message: 'save successfully',
                type: 'success'
            })
            return Client.getTransactionListByLedgerName(this.currentLedger.name, this.nowadays())
        }).then(resp => {
            this.transactionList = resp.data
        });
    }

    nowadays(): string {
        return getCurrentYearAndMonth()
    }

    assembleAddTransactionRequest(trans: any[]): any[] {
        let request = trans.map((tran) => {
            let requestTran = {
                categoryValue: tran.categoryValue.value,
                amount: tran.amount.value,
                count: tran.count.value,
                description: tran.description.value,
                location: this.geoLocation,
            };

            return requestTran;
        });
        return request
    }


    onClickOneType(type: string) {
        let curStringAtInput = this.rawFormatString
        if (curStringAtInput == null || curStringAtInput == "") {
            return;
        }

        this.replaceFirstWord(curStringAtInput, type)
    }

    replaceFirstWord(curStringAtInput: string, type: string) {
        let a = this.$refs.recordInput as VanCursorEditorComponent
        let rowNumber = a.cursorPosition.cursorRow
        let wordRange = findWordInLine(curStringAtInput, rowNumber, 1)

        if (wordRange == null) {
            return;
        }

        curStringAtInput = replace(curStringAtInput, wordRange.start, wordRange.end, type)
        this.rawFormatString = curStringAtInput

        this.parseInputStringToObjects()

        this.updateTextAreaInputCursorPosition(wordRange.start)
    }


    updateTextAreaInputCursorPosition(start: number) {
        let vantinput = (this.$refs.recordInput as VanCursorEditorComponent).$el

        let input = vantinput.querySelector('textarea')
        input!.selectionStart = start
    }


    parseInputStringToObjects() {
        if (this.rawFormatString == null) {
            return;
        }
        // food 44.00 kfc
        // fruit 33.00 watermalon
        let wordsOrder = ['categoryValue', 'amount', 'count', 'description'];

        var lines = this.rawFormatString.split('\n');

        let objs = lines
            .filter((line) => !/^\s*$/.test(line))
            .map((line) => line.trim())
            .map((line) => {
                let words = line.split(' ').filter((word) => word !== '');

                let obj = wordsOrder.reduce(
                    (obj, cur, index) =>
                        Object.assign(obj, {[cur]: words[index]}),
                    {},
                );

                return obj;
            }).map((obj: any) => {
                let objWithViewStatus = {};
                const keys = Object.keys(obj);
                return keys.reduce((objWithViewStatus, key) => {
                    return Object.assign(objWithViewStatus, {
                        [key]: {
                            value: obj[key],
                            isValid: false,
                        },
                    });
                }, objWithViewStatus);
            });
        this.parsedForms = objs as FormItem[];
    }

}
</script>
<style lang="scss" scoped>
@import "~@/style/common-style.scss";

.fake-marker {
    display: block;
    position: absolute;
    top: 50%;
    left: 50%;
    object-fit: cover;
    width: 20px;
    height: 33px;
    z-index: auto;
    transform: translate(-50%, -100%);
    background: url("~@/assets/mark_bs.png");
    background-size: cover;
}


.page {
    box-sizing: border-box;
    //padding: 0 8px 0 8px;
    padding: 8px;
    background-color: #f7f8fa;
}


.record-header {
    padding: 16px 16px 8px;
    color: #969799;
    font-size: 14px;
    line-height: 16px;
}
</style>
