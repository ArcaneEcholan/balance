<template>
    <div class="page">

        <!--edit stack-->
        <div style="position: relative; z-index: 1901;">
            <router-view>
            </router-view>
        </div>


        <!--create a zindex context to separate basic page and modal, and avoid
        components like v-loading(zindex 2000) step over to popped modal-->
        <div style="position: relative; z-index: 1900;">
            <!--add zindex context to ensure this dropdown can step over any sibling loading-->

            <div style="position: relative; z-index: 1000">
                <ledger-switcher-component
                ></ledger-switcher-component>
            </div>

            <!--region: amap-->
            <!--<div class="record-header">Amap</div>-->

            <!--<div style="position: relative" class="bg-white br8 shadow overflow-hidden">-->
            <!--    <div id="amap" style="width: 100%; height: auto">-->
            <!--    </div>-->
            <!--    <div class="fake-marker"></div>-->
            <!--</div>-->
            <!--endregion-->


            <gap-component></gap-component>

            <div class="record-header">Location information
                <el-button ref="refresh-btn" @click="onClickRefreshLocationData">
                    <van-icon name="replay"/>
                </el-button>
            </div>

            <van-cell-group style="position: relative; z-index: 999;" v-loading="locationLoading"
                            class="shadow overflow-hidden br8 ">
                <van-cell title="Coordinate (lat, lng)"
                          :value="`(${geoLocation.latitude}, ${geoLocation.longitude})`"/>
                <van-cell title="Overview" :value="`${geoLocation.formattedName}`"/>
            </van-cell-group>

            <gap-component></gap-component>

            <transaction-type-component @on-click-one-type="onClickOneType"></transaction-type-component>

            <!--region: input area-->
            <div class="record-header">Add Some Record</div>
            <!--row: {{ cursorPosition.cursorRow }}, col: {{ cursorPosition.cursorColumn }}-->
            <!--Input-->

            <add-transaction-editor-component>

            </add-transaction-editor-component>

            <!--endregion-->


            <gap-component :value="'8px'"></gap-component>

            <!--header-->

            <!--region: this month-->
            <transaction-list-component
                ref="transaction-list-component"
            ></transaction-list-component>
            <!--endregion-->

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

import TransactionListComponent from "@/views/index/index/components/TransactionListComponent.vue";
import LedgerSwitcherComponent from "@/views/index/index/components/LedgerSwitcherComponent.vue";
import TransactionTypeComponent from "@/views/index/index/components/TransactionTypeComponent.vue";
import GapComponent from "@/views/components/GapComponent.vue";
import AddTransactionEditorComponent from './components/AddTransactionEditorComponent.vue';

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
        GapComponent,
        TransactionListComponent,
        LedgerSwitcherComponent,
        TransactionTypeComponent,
        AddTransactionEditorComponent,
    },

})
export default class IndexView extends Vue {

    currentLedger = {id: null, name: "default"}

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
        let a = getVueEl(this, "refresh-btn")
        a.style.padding = "4px";
        this.adjustAMapSize()
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
