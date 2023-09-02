<template>
    <div class="page">

        <!--edit stack-->
        <router-view>
        </router-view>

        <div>
            <!--region: amap-->


            <div class="record-header">Amap</div>

            <div class=" bg-white br8 shadow  overflow-hidden">
                <div id="amap" style="width: 100%; height: auto"></div>
            </div>


            <div class="mgb20"></div>

            <van-cell-group class="shadow overflow-hidden br8 " title="Location information">
                <van-cell title="Coordinate (lat, lng)"
                          :value="`(${geoLocation.latitude}, ${geoLocation.longitude})`"/>
                <van-cell title="Overview" :value="`${geoLocation.formattedName}`"/>
            </van-cell-group>

            <div class="mgb20"></div>
            <!--<van-list-->
            <!--    v-model:loading="loading"-->
            <!--    :finished="finished"-->
            <!--    finished-text="Finished"-->
            <!--    @load="onload"-->
            <!--&gt;-->
            <!--    <van-cell v-for="item in list" :key="item" :title="item" />-->
            <!--</van-list>-->
            <!--endregion-->

            <div class="mgb20"></div>

            <!--<div style="z-index: 10000000">{{ stackSize }}</div>-->

            <!--region: input area-->
            <div class="record-header">Add Some Record</div>

            <!--Input-->
            <van-field
                @input="onParseRawString"
                v-model="rawFormatString"
                label="Records (per / Line)"
                type="textarea"
                placeholder="Message"
                rows="2"
                autosize
            />

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

            <el-button @click="onSaveTrans" round plain type="primary" style="width: 100%">
                <i class="el-icon-plus"></i>
            </el-button>
            <!--            <div @click="onSaveTrans" style="-->
            <!--        background-color: white;-->
            <!--        text-align: center;-->
            <!--"-->
            <!--                 class="br5 pdt10 pdb10"-->
            <!--            >-->
            <!--                <i class="el-icon-plus"></i>-->
            <!--            </div>-->


            <div class="mgb8"></div>

            <!--header-->
            <div class="record-header" v-show="transactionList.length > 0">This month</div>

            <!--region: this month-->
            <div style="" class="shadow br8 overflow-hidden">
                <!--list-->
                <template v-for="(form, index) in transactionList">
                    <!--list item-->
                    <div class="flex pdt10 pdb10 pdl16 pdr16"
                         @click="edit(form.id)"
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
</template>

<script lang="ts">
import AMapLoader from '@amap/amap-jsapi-loader';
import {Component, Vue} from 'vue-property-decorator';
import {Notification} from 'element-ui';
import Client from '@/request/client';
import request from '@/request';
import {convertToShortDateTime} from "@/ts/utils";
import PageStack, {pushPage} from "@/ts/pageStack";
import pageStack from "@/ts/pageStack";
import {Route} from "vue-router";
import eventBus from "@/ts/EventBus";
import {Notify} from "vant";
import {getCurrentMonth, getCurrentYear} from '@/ts/time';

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
    get stackSize() {
        return pageStack.getStackSize()
    }

    showMain = true
    show = false
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

    nowadays(): string {
        return `${getCurrentYear()}-${getCurrentMonth()}`
    }

    updateTransasction(newTransaction: any) {
        let found = this.transactionList.find((item) => {
            return item.id === newTransaction.id
        })
        if (found) {
            found.amount = newTransaction.amount
            found.count = newTransaction.count
            found.datetime = newTransaction.datetime
            found.description = newTransaction.description
        }
    }

    edit(recordId: string | number) {
        this.present(`/index/edit?recordId=${recordId}`)
    }


    present(path: string) {
        pushPage(path)
    }


    mounted() {
        let amapElem: HTMLElement = document.getElementById("amap")!
        let amapWidth = amapElem.clientWidth;
        amapElem.style.height = amapWidth + 'px'
    }

    getOneInformationEntry(entry: any): string {
        // this is the format of the response entry of amap api,
        // an empty array means a not existing entry value( the same meaning as null )

        if (Array.isArray(entry) && entry.length === 0) {
            return "";
        }
        return entry;
    }

    getInfomationFromGetLocationDetail(detail: any): any {
        let usefullocationInfos: any = {}

        usefullocationInfos.formattedName =
            this.getOneInformationEntry(detail.regeocode.formatted_address);

        if (usefullocationInfos.formattedName === "") {
            Notify(
                {
                    type: "danger",
                    message: "Your location is out of the service of amap "
                }
            )
            this.geoLocation.formattedName = 'out of service'
            return;
        }

        let addressDetail = detail.regeocode.addressComponent;
        usefullocationInfos.adcode = this.getOneInformationEntry(addressDetail.adcode);
        usefullocationInfos.province = this.getOneInformationEntry(addressDetail.province);

        // if the city is empty, it means the city is a direct-controlled municipality(beijing, shanghai...)
        let city =
            this.getOneInformationEntry(addressDetail.city) == "" ? usefullocationInfos.province : this.getOneInformationEntry(addressDetail.city);
        usefullocationInfos.city = city;
        usefullocationInfos.district = this.getOneInformationEntry(addressDetail.district);
        usefullocationInfos.citycode = this.getOneInformationEntry(addressDetail.citycode);

        usefullocationInfos.township = this.getOneInformationEntry(addressDetail.township);
        usefullocationInfos.towncode = this.getOneInformationEntry(addressDetail.towncode);
        usefullocationInfos.streetName = this.getOneInformationEntry(addressDetail.streetNumber.street);
        usefullocationInfos.streetNumber = this.getOneInformationEntry(addressDetail.streetNumber.number)
        ;
        usefullocationInfos.streetLocation = this.getOneInformationEntry(
            addressDetail.streetNumber.location)
        usefullocationInfos.streetDirection = this.getOneInformationEntry(
            addressDetail.streetNumber.direction)
        usefullocationInfos.streetDistance = this.getOneInformationEntry(
            addressDetail.streetNumber.distance)
        return usefullocationInfos
    }

    getDetailLocation(lat: string, long: string, successCb: (location: any) => void, failCb: (respBodyOrError: any) => void) {
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

    onSaveTrans() {
        if (this.parsedForms.length == 0) {
            Notification.warning("No data to save");
            return
        }
        let error = false
        let errorType = ""
        let parseForms = this.parsedForms
        parseForms.forEach((item: FormItem) => {
            if (error) {
                return
            }
            let categoryValue = item.categoryValue?.value;
            let amount = item.amount?.value;
            let count = item.count?.value;
            let description = item.description?.value;

            // ensure no null
            if (categoryValue == null || amount == null || count == null || description == null) {
                error = true
                errorType = "Information not complete"
                return
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
                return
            }
        })
        // @ts-ignore
        if (error === true) {
            Notification.error(errorType)
            return
        }
        this.saveTransactions(this.parsedForms);
    }


    saveTransactions(trans: any[]) {


        let request;
        try {
            request = trans.map((tran) => {
                let requestTran = {
                    categoryValue: tran.categoryValue.value,
                    amount: tran.amount.value,
                    count: tran.count.value,
                    description: tran.description.value,
                    location: this.geoLocation,
                };
                if (requestTran.amount == null || requestTran.count == null) {
                    throw Error('amount or count is null');
                }
                return requestTran;
            });
        } catch (e: any) {
            Notification.error(e.message == null ? e : e.message);
            return;
        }

        Client.saveTransactions(request).then((resp) => {
            Notification.success("save successfully")
            return Client.getTransactionList(this.nowadays())
        }).then(resp => {
            this.transactionList = resp.data
        });
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

    isFloat(number: number) {
        return Number(number) === number && !Number.isInteger(number) || Number.isInteger(number);
    }

    isPositiveInteger(number: number): boolean {
        return Number.isInteger(number) && number > 0;
    }

    rawFormatStringToObject(rawFormatString: string): any {
        // food 44.00 kfc
        // fruit 33.00 watermalon
        let wordsOrder = ['categoryValue', 'amount', 'count', 'description'];

        var lines = rawFormatString.split('\n');

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
                console.log(obj);

                return obj;
            });
        return objs;
    }

    onParseRawString() {
        if (this.rawFormatString !== null) {
            let objs = this.rawFormatStringToObject(this.rawFormatString);
            this.parsedForms = objs.map((obj: any) => {
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
        }
    }

    getGeolocation(successCb: any, errorCb: any) {
        if (this.amapGeolocationPlugin == null) {
            Notification.error('wait location service to load');
            return;
        }
        this.amapGeolocationPlugin.getCurrentPosition(successCb, errorCb);
    }

    created() {
        eventBus.$on('afterTransactionChanged', (newTransaction: any) => {
            this.updateTransasction(newTransaction)
        });
        this.transactionList = [{
            id: 1,
            categoryValue: "food",
            amount: 44.00,
            count: 1,
            description: "kfc",
            location: {
                latitude: 0,
                longitude: 0,
            }
        }, {
            id: 2,
            categoryValue: "fruit",
            amount: 33.00,
            count: 1,
            description: "watermalon",
            location: {
                latitude: 0,
                longitude: 0,
            }
        }]
        Client.getTransactionList(this.nowadays())
            .then(resp => {
                this.transactionList = resp.data
            })
        AMapLoader.load({
            key: '8375fa99f0a19ba66b137bddcb2fceac', // 申请好的Web端开发者Key，首次调用 load 时必填
            version: '2.0', // 指定要加载的 JS API 的版本，缺省时默认为 1.4.15
            plugins: [], // 需要使用的的插件列表，如比例尺'AMap.Scale'等
        })
            .then((AMap) => {
                this.amap = new AMap.Map('amap');
                AMap.plugin('AMap.Geolocation', () => {
                    // 异步加载插件
                    var geolocation = new AMap.Geolocation();
                    this.amap.addControl(geolocation);
                    this.amapGeolocationPlugin = geolocation;
                    this.getGeolocation(
                        (status: any, result: any) => {
                            console.log(status);
                            console.log(result);

                            if (status === 'complete') {
                                let position = result.position;

                                // assign coordinate
                                this.geoLocation.latitude = position.lat;
                                this.geoLocation.longitude = position.lng;

                                console.debug(`get coordinates: ${position}`)
                                Notify({
                                    message: `(lat, lng) = (${position.lat}, ${position.lng})`,
                                    type: 'success'
                                })

                                this.getDetailLocation(position.lat, position.lng,
                                    (locationDetail: any) => {
                                        // get information we need from detail response
                                        let infomationFromGetLocationDetail = this.getInfomationFromGetLocationDetail(locationDetail);
                                        // assign details
                                        this.geoLocation = Object.assign(this.geoLocation, infomationFromGetLocationDetail);
                                        console.debug(`get location detail: `, this.geoLocation)
                                    },
                                    (respBodyOrError: any) => {
                                        console.error(`fail to get location detail: `, respBodyOrError)
                                        Notify({
                                            message: 'Fail to get location detail',
                                            type: 'warning'
                                        })
                                    }
                                )
                            } else {
                                console.error(`fail to get geolocation, status: `, status, `result: `, result);
                                Notify({
                                    message: 'Fail to locate your position',
                                    type: 'danger'
                                })
                            }
                        },
                        (error: any) => {
                            Notify({
                                message: `fail to get geollocation: ${error.code}-${error.message}`,
                                type: 'danger'
                            })
                            console.error(`fail to get geollocation: ${error.code}-${error.message}`);
                        },
                    );
                });
            })
            .catch((e) => {
                console.log(e);
            });
    }

    location() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (loc) => {
                    console.log(loc);
                    Notification.success({
                        title: 'Success',
                        message:
                            'Location: ' +
                            loc.coords.latitude +
                            ', ' +
                            loc.coords.longitude,
                    });
                },
                (positionError) => {
                    Notification.warning({
                        title: 'Warning',
                        message:
                            'Error getting location: ' + positionError.message,
                    });
                    console.log(positionError);
                },
                {
                    enableHighAccuracy: true,
                    maximumAge: 30000,
                    timeout: 27000,
                },
            );
        } else {
            Notification.warning({
                title: 'Warning',
                message: 'Geolocation is not supported by this browser.',
            });
        }
    }
}
</script>
<style lang="scss" scoped>
@import "~@/style/common-style.scss";

.page {
    padding: 8px;
    background-color: #f7f8fa;
}


.record-header {
    padding: 16px 16px 8px;
    color: #969799;
    font-size: 14px;
    line-height: 16px;

    //background-color: $google-gray-100;
    //padding: 10px;
    //border-top-left-radius: 5px;
    //border-top-right-radius: 5px;
}
</style>
