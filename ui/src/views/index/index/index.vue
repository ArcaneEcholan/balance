<template>
    <div class="page">
        <van-action-sheet :closeable="false" v-model="show" title="Add Records">
            <!--<div class="record-header">Add Some Record</div>-->
            <add-transaction-editor-component
                    ref="add-transaction-editor-component"></add-transaction-editor-component>
        </van-action-sheet>
        <!--
        Don't put the element inside z-index context, or the tab bar
        in the App.vue will cover the pop-ups like action sheet or
        swipe modal.

        Apple does not respect the z-index context mechanism
        -->
        <div style="position:relative; z-index: 101">
            <router-view>
            </router-view>
        </div>

        <div id="header">
            <div class="flex center">
                <div class="current-ledger">{{ curLedger.name }}</div>
            </div>
            <main-page-current-date-picker-component></main-page-current-date-picker-component>
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
        <div id="header-gradual-color-bg"
        ></div>
        <div style="height: 100px;"></div>

        <gap-component :value="'8px'"></gap-component>

        <transaction-list-component
            ref="transaction-list-component"
        ></transaction-list-component>
    </div>

</template>
<script lang="ts">
import AMapLoader from '@amap/amap-jsapi-loader';
import {Component, Vue} from 'vue-property-decorator';
import request from '@/request';
import {Notify} from "vant";
import {getRef} from "@/ts/vueUtils";

import TransactionListComponent from "@/views/index/index/components/TransactionListComponent.vue";
import LedgerSwitcherComponent from "@/views/index/index/components/LedgerSwitcherComponent.vue";
import TransactionTypeComponent from "@/views/index/index/components/TransactionTypeComponent.vue";
import GapComponent from "@/views/components/GapComponent.vue";
import AddTransactionEditorComponent from './components/AddTransactionEditorComponent.vue';
import MainPageCurrentDatePickerComponent from "@/views/index/index/components/MainPageCurrentDatePickerComponent.vue";
import CommonButton from "@/views/components/CommonButton.vue";
import store from "@/store";
import {provideListeners} from "@/page-eventbus-registration-mixin";

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
    components: {
        CommonButton,
        MainPageCurrentDatePickerComponent,
        GapComponent,
        TransactionListComponent,
        LedgerSwitcherComponent,
        TransactionTypeComponent,
        AddTransactionEditorComponent,
    },

})
export default class IndexView extends Vue {
    onclickAddRecord() {
        this.show = true
    }

    curLedger = ""

    gets() {
        return store.getters.scrollPosition
    }

    show = false
    apiInvokingTimesSaver: any = null

    amap: any;
    amapGeolocationPlugin: any;
    geoLocation: any = {
        latitude: null,
        longitude: null,
        formattedName: null
    };

    onClickRefreshLocationData() {
        this.getCurrentLocation(this.amap)
    }

    created() {
        provideListeners(this, [
            {
                eventName: "on-cur-ledger-changed",
                handler: (ledger: any) => {
                    this.curLedger = ledger
                }
            },
        ])
        this.loadAmap()
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
        this.$emit("on-cur-location-changed", this.geoLocation)
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
        // let a = getRef(this, "refresh-btn")
        // a.style.height = "auto";
        // a.style.width = "auto";
        //
        // a.style.padding = "4px";
        // this.adjustAMapSize()

        //
        // let b = document.getElementById("main-page-fix-header")!
        // let bh = b.clientHeight

        // let c = document.getElementById("main-page-fix-header-placeholder")!
        // c.style.height = bh + "px";
    }

    adjustAMapSize() {
        let amapElem: HTMLElement = document.getElementById("amap")!
        let amapWidth = amapElem.clientWidth;
        amapElem.style.height = amapWidth + 'px'
    }

    onClickOneType(type: string) {
        getRef(this, "add-transaction-editor-component").replaceFirstWord(type)
    }

}
</script>
<style lang="scss" scoped>
@import "~@/style/common-style.scss";
@import "~@/assets/custom-icon.css";

.icon {
    font-size: 20px;
}

$header-bgc: #FCF4D4;
#header {
    //background-color: #3574F0;
    background-color: $header-bgc;

    padding: 16px 8px 0 8px;
    position: fixed;
    z-index: 100;
    top: 0px;
    left: 0px;
    right: 0px;


    #main-page-fix-header {
        position: relative;
        top: 32px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

        background-color: white;
        border-radius: 5px;
        padding: 0 16px;
    }

    .current-ledger {
        font-size: 24px;
    }


}

#header-gradual-color-bg {
    top: 120px;
    position: fixed;
    height: 30px;
    left: 0px;
    right: 0px;
    background: linear-gradient(to bottom, $header-bgc, #ffffff);
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
    background: url("~@/assets/mark_bs.png");
    background-size: cover;
}

.page {
    box-sizing: border-box;
    //padding: 0 8px 0 8px;
    padding: 16px;
    background-color: #ffffff;
}

.record-header {
    padding: 16px 16px 8px;
    color: #969799;
    font-size: 14px;
    line-height: 16px;
}

</style>
