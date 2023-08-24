<template>
    <div class="page">


        <!--edit stack-->
        <router-view>
        </router-view>


        <div>
            <van-cell title="Show Popup" is-link @click="showPopup"/>
            <van-popup v-model:show="show" position="right"
                       :style="{ width: '100%', height: '100%'}">
                <div class="pd10">
                    Content
                </div>
            </van-popup>


            <!--region: amap-->
            <div class="mgb8"><span class="bold fs22">Location</span></div>
            <div id="amap" style="width: 100%; height: auto"></div>
            <div class="flex">
                <span style="font-size: 16px">(lat, lng)=({{ geoLocation.latitude }},{{ geoLocation.longitude }})</span>
            </div>
            <!--endregion-->

            <div class="mgb20"></div>

            <div style="z-index: 10000000">{{ stackSize }}</div>

            <!--region: input area-->
            <div class="mgb8"><span class="bold fs22">Record</span></div>

            <!--Input-->
            <el-input
                @input="onParseRawString"
                type="textarea"
                :autosize="{ minRows: 2 }"
                v-model="rawFormatString"
            ></el-input>

            <div class="mgb8"></div>

            <!--region: Preview-->
            <div style="" class="shadow br5">
                <div class="record-header" v-show="parsedForms.length > 0">Preview</div>
                <template v-for="(form, index) in parsedForms">
                    <div class="flex pd10"
                         :style="
                     `border-bottom: 1px solid #f5f5f5;
                     ${index === parsedForms.length - 1 ? 'border-bottom: none; border-bottom-left-radius: 5px;border-bottom-right-radius: 5px;' : ''};
                     background-color: white;
                     `
                ">
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

            <div @click="onSaveTrans" style="
        background-color: white;
        text-align: center;
"
                 class="br5 pdt10 pdb10"
            >
                <i class="el-icon-plus"></i>
            </div>


            <div class="mgb8"></div>

            <!--region: this month-->
            <div style="" class="shadow br5">
                <!--header-->
                <div class="record-header" v-show="transactionList.length > 0">This month</div>
                <!--list-->
                <template v-for="(form, index) in transactionList">
                    <!--list item-->
                    <div class="flex pd10"
                         @click="edit(form.id)"
                         :style="
                     `border-bottom: 1px solid #f5f5f5;
                     ${index === transactionList.length - 1 ? 'border-bottom: none; border-bottom-left-radius: 5px;border-bottom-right-radius: 5px;' : ''};
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

            <!--<el-table :data="transactionList">-->
            <!--    <el-table-column prop="id" label="id"></el-table-column>-->
            <!--    <el-table-column prop="amount" label="amount"></el-table-column>-->
            <!--    <el-table-column-->
            <!--        prop="categoryId"-->
            <!--        label="categoryId"-->
            <!--    ></el-table-column>-->
            <!--    <el-table-column-->
            <!--        prop="description"-->
            <!--        label="description"-->
            <!--    ></el-table-column>-->
            <!--    <el-table-column prop="location" label="location"></el-table-column>-->
            <!--</el-table>-->
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
            if(!timeString) {
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

    showPopup() {
        this.show = true
    }

    mounted() {
        let amapElem: HTMLElement = document.getElementById("amap")!
        let amapWidth = amapElem.clientWidth;
        amapElem.style.height = amapWidth + 'px'
    }

    getDetailLocation(lat: string, long: string) {
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
                this.geoLocation.formattedName =
                    responseData.regeocode.formatted_address;

                let addressDetail = responseData.regeocode.addressComponent;
                this.geoLocation.adcode = addressDetail.adcode;
                this.geoLocation.province = addressDetail.province;

                let city =
                    addressDetail.city.length == 0 ? '' : addressDetail.city[0];
                this.geoLocation.city = city;
                this.geoLocation.district = addressDetail.district;
                this.geoLocation.citycode = addressDetail.citycode;

                this.geoLocation.township = addressDetail.township;
                this.geoLocation.towncode = addressDetail.towncode;
                this.geoLocation.streetName = addressDetail.streetNumber.street;
                this.geoLocation.streetNumber =
                    addressDetail.streetNumber.number;
                this.geoLocation.streetLocation =
                    addressDetail.streetNumber.location;
                this.geoLocation.streetDirection =
                    addressDetail.streetNumber.direction;
                this.geoLocation.streetDistance =
                    addressDetail.streetNumber.distance;
                console.log(this.geoLocation);
            }
        });
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
            return Client.getTransactionList("2023-8")
        }).then(resp => {
            this.transactionList = resp.data
        });
    }

    checkCategory(form: any) {
        // form?.categoryValue?.isValid = true;
    }

    checkAmount(form: any) {
        // form.amount.isValid = true;
    }

    checkCount(form: any) {
        // form.count.isValid = true;
    }

    checkDescription(form: any) {
        // form.description.isValid = true;
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

    onClickRefreshLocation() {
        this.doGetGeoLocation()
    }

    doGetGeoLocation() {
        this.getGeolocation(
            (status: any, result: any) => {
                console.log(status);
                console.log(result);

                if (status === 'complete') {
                    let position = result.position;
                    this.geoLocation.latitude = position.lat;
                    this.geoLocation.longitude = position.lng;
                    console.log(position);

                    Notification.success(
                        `'(lat, lng) = (${position.lat}, ${position.lng})`,
                    );

                    // this.getDetailLocation(
                    //     position.lat,
                    //     position.lng,
                    // );
                } else {
                    console.log(result);
                    Notification.error('定位失败');
                }
            },
            (error: any) => {
                Notification.error(
                    `fail to get geollocation: ${error.code}-${error.message}`,
                );
            },
        );
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
        Client.getTransactionList("2023-8")
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
                    this.doGetGeoLocation();
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
    background-color: $google-gray-300;
    padding: 5px;
}

.record-header {
    background-color: $google-gray-100;
    padding: 10px;
    border-top-left-radius: 5px;
    border-top-right-radius: 5px;
}
</style>
