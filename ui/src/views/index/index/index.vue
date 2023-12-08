<template>
    <div class="page">

        <div style="position: relative; z-index: 101">
            <div :id="mountPointUid"></div>
            <div :id="mountPointUid2"></div>
            <div :id="mountPointUid3"></div>
        </div>

        <van-action-sheet :closeable="false" v-model="show">
            <div class="page">
                <div class="action-sheet-title">
                    {{ $t('add_record.title') }}
                </div>
                <div class="action-sheet-body">
                    <add-record-sheet-component
                        ref="add-transaction-editor-component"
                    ></add-record-sheet-component>
                </div>
            </div>
        </van-action-sheet>

        <div id="records-index-header">

            <div class="flex center">
                <div class="current-ledger">{{ curLedger.name }}</div>
            </div>

            <main-page-current-date-picker-component></main-page-current-date-picker-component>

            <div id="records-index-header-gradual-color-bg"></div>

            <div id="main-page-fix-header">
                <div class="flex align-center">

                    <ledger-switcher-component ref="ledger-switcher-component"></ledger-switcher-component>

                    <clickable @click="showLedgerSwitcherSheet">
                        <div class="mg20 flex align-center">
                            <i class="icon ali-international-icon-log"></i>
                        </div>
                    </clickable>

                    <clickable @click="onClickSettingIcon">
                        <div class="mg20 flex align-center">
                            <van-icon class="icon" name="setting-o"></van-icon>
                        </div>
                    </clickable>

                    <clickable @click="show = true">
                        <div class="mg20 flex align-center">
                            <i class="icon ali-international-icon-plus"></i>
                        </div>
                    </clickable>

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

import TransactionListComponent from '@/views/index/index/components/TransactionListComponent.vue';
import LedgerSwitcherComponent from '@/views/index/index/components/LedgerSwitcherComponent.vue';
import TransactionTypeComponent from '@/views/index/index/components/TransactionTypeComponent.vue';
import GapComponent from '@/views/components/GapComponent.vue';
import AddRecordSheet from './components/AddRecordSheetComponent.vue';
import MainPageCurrentDatePickerComponent from '@/views/index/index/components/MainPageCurrentDatePickerComponent.vue';
import CommonButton from '@/views/components/CommonButton.vue';
import {provideListeners} from '@/page-eventbus-registration-mixin';
import AddRecordSheetComponent from '@/views/index/index/components/AddRecordSheetComponent.vue';
import {disableBodyScroll, enableBodyScroll, generateMountPointUid, mountComponent} from '@/ts/utils';
import EditRecordView from '@/views/index/index/modals/edit_record.vue';
import ManageLedgerView from '@/views/index/index/modals/ledger.vue';
import SettingsView from "@/views/index/index/modals/settings.vue";
import Clickable from "@/views/components/Clickable.vue";

(window as any)._AMapSecurityConfig = {
    securityJsCode: '172c59e3fd1b621adddca8f268ff879a',
};

@Component({
    components: {
        Clickable,
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
    lwc: any | null = null;
    settingShow = false
    mountPointUid = generateMountPointUid();
    mountPointUid2 = generateMountPointUid();
    mountPointUid3 = generateMountPointUid();

    showLedgerSwitcherSheet() {
        this.lwc.showSheet()
    }

    onClickSettingIcon() {
        let arg: any = {}
        arg.modalLifeCycleHooks = this.getModalLifeCycleHooks()
        mountComponent(this.mountPointUid3, SettingsView, arg);
    }

    getModalLifeCycleHooks = () => {
        return {
            onOpen: () => {
                {
                    disableBodyScroll()

                    let elem = document.getElementById('page-main-area')!;
                    elem.style.right = 100 + 'px';

                    elem = document.getElementById('tabbar-area')!;
                    elem.style.right = 100 + 'px';

                    elem = document.getElementById('records-index-header')!;
                    elem.style.right = 100 + 'px';
                }
            },
            onClose: () => {
                enableBodyScroll()

                let modal = document.getElementById('page-main-area')!;
                modal.style.right = 0 + 'px';

                modal = document.getElementById('tabbar-area')!;
                modal.style.right = 0 + 'px';

                modal = document.getElementById('records-index-header')!;
                modal.style.right = 0 + 'px';
            },
            beforeSwipe: () => {

                let elem = document.getElementById('page-main-area')!;
                elem.classList.remove('tran')

                elem = document.getElementById('tabbar-area')!;
                elem.classList.remove('tran')

                elem = document.getElementById('records-index-header')!;
                elem.classList.remove('tran')
            },
            swiping: (args: any) => {
                let swipingPathPercent = args.swipingPathPercent
                let r = 1 - swipingPathPercent

                let modal = document.getElementById('page-main-area')!;
                modal.style.right = r * 100 + 'px';

                modal = document.getElementById('tabbar-area')!;
                modal.style.right = r * 100 + 'px';

                modal = document.getElementById('records-index-header')!;
                modal.style.right = r * 100 + 'px';
            },
            afterSwipe: () => {
                let modal = document.getElementById('page-main-area')!;
                modal.classList.add('tran')

                modal = document.getElementById('tabbar-area')!;
                modal.classList.add('tran')

                modal = document.getElementById('records-index-header')!;
                modal.classList.add('tran')
            },
            closed: () => {
                console.log("animation stop")
                // window.removeEventListener('touchstart', banTouch)
            },
            opened: () => {
                console.log("animation stop")
                // window.removeEventListener('touchstart', banTouch)
            }
        };
    }

    mountEditRecordComponent(arg: any) {
        arg.modalLifeCycleHooks = this.getModalLifeCycleHooks()
        mountComponent(this.mountPointUid, EditRecordView, arg);
    }


    mountLedgerManagementLedger(arg: any) {
        arg.modalLifeCycleHooks = this.getModalLifeCycleHooks()
        mountComponent(this.mountPointUid2, ManageLedgerView, arg);
    }

    curLedger = '';

    show = false;
    apiInvokingTimesSaver: any = null;

    amap: any;
    amapGeolocationPlugin: any;
    geoLocation: any = {
        latitude: null,
        longitude: null,
        formattedName: null,
    };

    // onClickRefreshLocationData() {
    //   this.getCurrentLocation(this.amap);
    // }
    // mountAmap(AMap: any) {
    //   this.amap = new AMap.Map('amap', {
    //     zoom: 16,
    //   });
    // }

    // registerAmapMoveEvent() {
    //   this.amap.on('mapmove', () => {
    //     let {lat, lng} = this.amap.getCenter();
    //     this.updateCor(lat, lng);
    //     clearTimeout(this.apiInvokingTimesSaver);
    //     this.apiInvokingTimesSaver = setTimeout(() => {
    //       this.getAndUpdateDetailLocationData();
    //     }, 500);
    //   });
    // }


    created() {
        provideListeners(this, [
            {
                eventName: 'on-cur-ledger-changed',
                handler: (ledger: any) => {
                    this.curLedger = ledger;
                },
            },
        ]);
        //loadAmap
        {
            AMapLoader.load({
                key: '8375fa99f0a19ba66b137bddcb2fceac', // 申请好的Web端开发者Key，首次调用 load 时必填
                version: '2.0', // 指定要加载的 JS API 的版本，缺省时默认为 1.4.15
                plugins: [], // 需要使用的的插件列表，如比例尺'AMap.Scale'等
            })
                .then((AMap) => {
                    this.getCurrentLocation(AMap);
                })
                .catch((e) => {
                    console.log(e);
                });
        }
    }


    locationLoading = false;

    getCurrentLocation(AMap: any) {
        if (this.amapGeolocationPlugin == null) {
            //loadAmapGeolocationPlugin
            {
                AMap.plugin('AMap.Geolocation', () => {
                    let geolocation = new AMap.Geolocation();
                    this.amapGeolocationPlugin = geolocation;
                    this.doGetCurrentLocation();
                });
            }
        } else {
            this.doGetCurrentLocation();
        }
    }

    doGetCurrentLocation() {

        let onSuccessGetCurrentPosition = (status: any, result: any) => {
            this.locationLoading = false;
            if (status === 'complete') {    //ifSuccessGetCurrentPosition
                this.locationLoading = false;
                let position = result.position;

                //updateCor
                {
                    this.geoLocation.latitude = position.lat;
                    this.geoLocation.longitude = position.lng;
                }

                //getAndUpdateDetailLocationData
                {
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

        let onFailedGetCurrentPosition = (error: any) => {
            Notify({
                message: `fail to get geollocation: ${error.code}-${error.message}`,
                type: 'danger',
            });
            console.error(
                `fail to get geollocation: ${error.code}-${error.message}`,
            );
        }

        this.locationLoading = true;
        this.amapGeolocationPlugin.getCurrentPosition(
            onSuccessGetCurrentPosition,
            onFailedGetCurrentPosition,
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
        let finalLocationInfo: any = {};
        // getFormattedNameFromAmapLocationDetail
        {
            // this is the format of the response entry of amap api,
            // an empty array means a not existing entry value( the same meaning as null )

            let entry = amapDetailLocationResp.regeocode.formatted_address
            if (Array.isArray(entry) && entry.length === 0) {
                finalLocationInfo.formattedName = "";
                Notify({
                    type: 'danger',
                    message: 'Your location is out of the service of amap ',
                });
                finalLocationInfo.formattedName = 'out of service';
            } else {
                finalLocationInfo.formattedName = entry;
            }
        }

        // assign details
        this.geoLocation = Object.assign(this.geoLocation, finalLocationInfo);
        this.$emit('on-cur-location-changed', this.geoLocation);
        console.debug(`get location detail: `, this.geoLocation);
    }

    mounted() {

        this.lwc = this.$refs["ledger-switcher-component"]

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
    transition: right var(--transition-duration) var(--transition-easing);
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
