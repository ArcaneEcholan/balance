<template>
    <div class="page">
        <div style="position: relative; z-index: 101">
            <div :id="mountPointUid"></div>
            <div :id="mountPointUid2"></div>
        </div>

        <van-action-sheet :closeable="false" v-model="show" title="Add Records">
            <div class="page">
                <!--<div class="record-header">Add Some Record</div>-->
                <add-record-sheet-component
                    ref="add-transaction-editor-component"
                ></add-record-sheet-component>
            </div>
        </van-action-sheet>

        <div id="records-index-header">
            <div class="flex center">
                <div class="current-ledger">{{ curLedger.name }}</div>
            </div>
            <main-page-current-date-picker-component></main-page-current-date-picker-component>
            <div id="records-index-header-gradual-color-bg"></div>
            <div id="main-page-fix-header">
                <div class="flex justify-between align-center">
                    <div class="mg20">
                        <ledger-switcher-component></ledger-switcher-component>
                    </div>
                    <div class="mg20">
                        <div @click="onclickAddRecord">
                            <i class="icon ali-international-icon-plus"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <gap-component :value="'140px'"></gap-component>

        <transaction-list-component
            ref="transaction-list-component"
        ></transaction-list-component>

        <gap-component :value="'58px'"></gap-component>
    </div>
</template>
<script lang="ts">
import AMapLoader from '@amap/amap-jsapi-loader';
import {Component, Vue} from 'vue-property-decorator';
import request from '@/request';
import {Notify} from 'vant';
import {getRef} from '@/ts/vueUtils';

import TransactionListComponent from '@/views/index/index/components/TransactionListComponent.vue';
import LedgerSwitcherComponent from '@/views/index/index/components/LedgerSwitcherComponent.vue';
import TransactionTypeComponent from '@/views/index/index/components/TransactionTypeComponent.vue';
import GapComponent from '@/views/components/GapComponent.vue';
import AddRecordSheet from './components/AddRecordSheetComponent.vue';
import MainPageCurrentDatePickerComponent from '@/views/index/index/components/MainPageCurrentDatePickerComponent.vue';
import CommonButton from '@/views/components/CommonButton.vue';
import store from '@/store';
import {provideListeners} from '@/page-eventbus-registration-mixin';
import AddRecordSheetComponent from '@/views/index/index/components/AddRecordSheetComponent.vue';
import {generateMountPointUid, mountComponent} from '@/ts/utils';
import EditRecordView from '@/views/index/index/edit/edit_record.vue';
import ManageLedgerView from '@/views/index/ledger.vue';

(window as any)._AMapSecurityConfig = {
    securityJsCode: '172c59e3fd1b621adddca8f268ff879a',
};

@Component({
    components: {
        AddRecordSheetComponent,
        CommonButton,
        MainPageCurrentDatePickerComponent,
        GapComponent,
        TransactionListComponent,
        LedgerSwitcherComponent,
        TransactionTypeComponent,
        AddRecordSheet,
    },
})
export default class IndexView extends Vue {
    mountPointUid = generateMountPointUid();
    mountPointUid2 = generateMountPointUid();


    mountEditRecordComponent(arg: any) {
        mountComponent(this.mountPointUid, EditRecordView, arg);
    }

    mountLedgerManagementLedger(arg: any) {
        mountComponent(this.mountPointUid2, ManageLedgerView, arg);
    }

    onclickAddRecord() {
        this.show = true;
    }

    curLedger = '';

    gets() {
        return store.getters.scrollPosition;
    }

    show = false;
    apiInvokingTimesSaver: any = null;

    amap: any;
    amapGeolocationPlugin: any;
    geoLocation: any = {
        latitude: null,
        longitude: null,
        formattedName: null,
    };

    onClickRefreshLocationData() {
        this.getCurrentLocation(this.amap);
    }

    created() {
        provideListeners(this, [
            {
                eventName: 'on-cur-ledger-changed',
                handler: (ledger: any) => {
                    this.curLedger = ledger;
                },
            },
        ]);
        this.loadAmap();
    }

    loadAmap() {
        this.doLoadAmap(
            (AMap: any) => {
                // this.mountAmap(AMap)
                //
                // this.registerAmapMoveEvent()

                this.getCurrentLocation(AMap);
            },
            (e: any) => {
                console.log(e);
            },
        );
    }

    doLoadAmap(success: any, fail: any) {
        AMapLoader.load({
            key: '8375fa99f0a19ba66b137bddcb2fceac', // 申请好的Web端开发者Key，首次调用 load 时必填
            version: '2.0', // 指定要加载的 JS API 的版本，缺省时默认为 1.4.15
            plugins: [], // 需要使用的的插件列表，如比例尺'AMap.Scale'等
        })
            .then((AMap) => {
                success(AMap);
            })
            .catch((e) => {
                fail(e);
            });
    }

    mountAmap(AMap: any) {
        this.amap = new AMap.Map('amap', {
            zoom: 16,
        });
    }

    registerAmapMoveEvent() {
        this.amap.on('mapmove', () => {
            let {lat, lng} = this.amap.getCenter();
            this.updateCor(lat, lng);
            clearTimeout(this.apiInvokingTimesSaver);
            this.apiInvokingTimesSaver = setTimeout(() => {
                this.getAndUpdateDetailLocationData();
            }, 500);
        });
    }

    locationLoading = false;

    getCurrentLocation(AMap: any) {
        if (this.amapGeolocationPlugin == null) {
            this.loadAmapGeolocationPlugin(AMap, () => {
                this.doGetCurrentLocation();
            });
        } else {
            this.doGetCurrentLocation();
        }
    }

    loadAmapGeolocationPlugin(AMap: any, cb: any) {
        AMap.plugin('AMap.Geolocation', () => {
            let geolocation = new AMap.Geolocation();
            this.amapGeolocationPlugin = geolocation;
            cb();
        });
    }

    doGetCurrentLocation() {
        this.locationLoading = true;
        this.amapGeolocationPlugin.getCurrentPosition(
            this.onSuccessGetCurrentPosition,
            this.onFailedGetCurrentPosition,
        );
    }

    onSuccessGetCurrentPosition(status: any, result: any) {
        this.locationLoading = false;
        if (status === 'complete') {
            this.ifSuccessGetCurrentPosition(result);
        } else {
            console.error(
                `fail to get geolocation, status: `,
                status,
                `result: `,
                result,
            );
            Notify({
                message: 'Fail to locate your position',
                type: 'danger',
            });
        }
    }

    ifSuccessGetCurrentPosition(result: any) {
        this.locationLoading = false;
        let position = result.position;
        this.updateCor(position.lat, position.lng);
        this.getAndUpdateDetailLocationData();
    }

    getAndUpdateDetailLocationData() {
        let lat = this.geoLocation.latitude;
        let lng = this.geoLocation.longitude;
        this.getDetailLocation(
            lat,
            lng,
            (locationDetail: any) => {
                // get information we need from detail response
                this.getInfoFromAmapLocationDetail(locationDetail);
            },
            (respBodyOrError: any) => {
                console.error(`fail to get location detail: `, respBodyOrError);
                Notify({
                    message: 'Fail to get location detail',
                    type: 'warning',
                });
            },
        );
    }

    getDetailLocation(
        lat: number,
        long: number,
        successCb: (location: any) => void,
        failCb: (respBodyOrError: any) => void,
    ) {
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
        })
            .then((res) => {
                let responseData = res.data;
                let code = responseData.infocode;

                if (code == '10000') {
                    successCb(responseData);
                } else {
                    failCb(responseData);
                }
            })
            .catch((err) => {
                failCb(err);
            });
    }

    getInfoFromAmapLocationDetail(amapDetailLocationResp: any): any {
        let finalLocationInfo = this.getFormattedNameFromLocationDetail(
            amapDetailLocationResp,
        );
        // assign details
        this.geoLocation = Object.assign(this.geoLocation, finalLocationInfo);
        this.$emit('on-cur-location-changed', this.geoLocation);
        console.debug(`get location detail: `, this.geoLocation);
    }

    getFormattedNameFromLocationDetail(amapDetailLocationResp: any) {
        let finalLocationInfo: any = {};
        finalLocationInfo.formattedName = this.returnBlankStringIfEmptyArray(
            amapDetailLocationResp.regeocode.formatted_address,
        );

        if (finalLocationInfo.formattedName === '') {
            Notify({
                type: 'danger',
                message: 'Your location is out of the service of amap ',
            });
            finalLocationInfo.formattedName = 'out of service';
        }
        return finalLocationInfo;
    }

    returnBlankStringIfEmptyArray(entry: any): string {
        // this is the format of the response entry of amap api,
        // an empty array means a not existing entry value( the same meaning as null )

        if (Array.isArray(entry) && entry.length === 0) {
            return '';
        }
        return entry;
    }

    onFailedGetCurrentPosition(error: any) {
        Notify({
            message: `fail to get geollocation: ${error.code}-${error.message}`,
            type: 'danger',
        });
        console.error(
            `fail to get geollocation: ${error.code}-${error.message}`,
        );
    }

    updateCor(lat: string | number, lng: string | number) {
        this.geoLocation.latitude = lat;
        this.geoLocation.longitude = lng;
    }

    mounted() {
        provideListeners(this, [
            {
                eventName: 'on-click-record-item',
                handler: (arg: any) => {
                    this.mountEditRecordComponent(arg);
                },
            },
            {
                eventName: 'on-click-manage-ledger',
                handler: (arg: any) => {
                    this.mountLedgerManagementLedger(arg);
                },
            },
        ]);
    }

    adjustAMapSize() {
        let amapElem: HTMLElement = document.getElementById('amap')!;
        let amapWidth = amapElem.clientWidth;
        amapElem.style.height = amapWidth + 'px';
    }

    onClickOneType(type: string) {
        getRef(this, 'add-transaction-editor-component').replaceFirstWord(type);
    }
}
</script>
<style lang="scss" scoped>
@import '~@/style/common-style.scss';
@import '~@/style/style-specification.scss';
@import '~@/assets/custom-icon.css';

.icon {
    font-size: 20px;
}

$header-bgc: #fcf4d4;
#records-index-header {
    //background-color: #3574F0;
    background-color: $header-bgc;

    box-sizing: border-box;
    padding: 16px 8px 0 8px;
    position: fixed;
    z-index: 100;
    top: 0px;
    right: 0px;
    width: 100%;

    #main-page-fix-header {
        position: absolute;
        left: 8px;
        right: 8px;
        top: 76px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

        background-color: white;
        border-radius: 5px;
        padding: 0 16px;
    }

    #records-index-header-gradual-color-bg {
        left: -8px;
        position: relative;
        width: 200%;
        height: 50px;
        background: linear-gradient(to bottom, $header-bgc, #f7f8fa);
    }

    .current-ledger {
        font-size: 24px;
    }
}

#records-index-header.tran {
    transition: right 0.5s cubic-bezier(0, 1, 0, 1);
}

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
    background: url('~@/assets/mark_bs.png');
    background-size: cover;
}

.record-header {
    padding: 16px 16px 8px;
    color: #969799;
    font-size: 14px;
    line-height: 16px;
}
</style>
