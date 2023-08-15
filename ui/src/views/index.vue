<template>
    <div class="page">
        <!--region: amap-->
        <div class="mgb8"><span class="bold fs22">Location</span></div>
        <div id="amap" style="width: 100%; height: auto"></div>
        <div class="flex">
            <!--<el-button @click="onClickRefreshLocation">refresh</el-button>-->

            <span style="font-size: 16px">(lat, lng)=({{ geoLocation.latitude }},{{ geoLocation.longitude }})</span>
            <!--latitude-->
            <!--<el-input readonly v-model="geoLocation.latitude"></el-input>-->
            <!--longitude-->
            <!--<el-input readonly v-model="geoLocation.longitude"></el-input>-->
            <!--formated_address-->
            <!--<el-input readonly v-model="geoLocation.formattedName"></el-input>-->
        </div>
        <!--endregion-->

        <div class="mgb20"></div>

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
                            <span class="google-gray-400">{{ form.description ? form.description.value : "" }}</span>
                        </div>
                    </div>
                    <div class="flexg1">
                        <span class="bold">{{ form.amount ? form.amount.value : "" }}</span>
                    </div>
                </div>

                <!--categoryValue-->
                <!--<el-input-->
                <!--    v-model="form.categoryValue.value"-->
                <!--    @input="checkCategory(form)"-->
                <!--&gt;</el-input>-->
                <!--amount-->
                <!--<el-input-->
                <!--    v-model="form.amount.value"-->
                <!--    @input="checkAmount(form)"-->
                <!--&gt;</el-input>-->
                <!--count-->
                <!--<el-input-->
                <!--    v-model="form.count.value"-->
                <!--    @input="checkCount(form)"-->
                <!--&gt;</el-input>-->
                <!--description-->
                <!--<el-input-->
                <!--    v-model="form.description.value"-->
                <!--    @input="checkDescription(form)"-->
                <!--&gt;</el-input>-->
            </template>
        </div>
        <!--endregion-->

        <div class="mgb8"></div>

        <!--endregion-->

        <el-button @click="onSaveTrans">save all</el-button>

        <!--<el-button @click="location"></el-button>-->

        <!-- <el-select v-model="trans_type">
            <el-option
                v-for="tran in transactionList"
                :key="tran.value"
                :label="tran.label"
                :value="tran.value"
            />
        </el-select> -->

        <el-table :data="transactionList">
            <el-table-column prop="id" label="id"></el-table-column>
            <el-table-column prop="amount" label="amount"></el-table-column>
            <el-table-column
                prop="categoryId"
                label="categoryId"
            ></el-table-column>
            <el-table-column
                prop="description"
                label="description"
            ></el-table-column>
            <el-table-column prop="location" label="location"></el-table-column>
        </el-table>
    </div>
</template>

<script lang="ts">
import AMapLoader from '@amap/amap-jsapi-loader';
import {Component, Vue} from 'vue-property-decorator';
import {Notification} from 'element-ui';
import Client from '@/request/client';
import request from '@/request';

(window as any)._AMapSecurityConfig = {
    securityJsCode: '172c59e3fd1b621adddca8f268ff879a',
};

@Component({})
export default class TestView extends Vue {
    transactionList: any[] = [];
    amap: any;
    amapGeolocationPlugin: any;
    rawFormatString: string | null = null;
    parsedForms: any[] = [];
    geoLocation: any = {
        latitude: null,
        longitude: null,
        formattedName: null
    };

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
            console.log(resp);
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

    isFloat(number: number) {
        return Number(number) === number && !Number.isInteger(number);
    }

    isPositiveInteger(number: number) {
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
        this.transactionList = [
            {
                id: 1,
                amount: '14.55',
                categoryId: 1,
                description: 'test buying',
                location: 'test location',
            },
            {
                id: 2,
                amount: '14.55',
                categoryId: 1,
                description: 'test buying',
                location: 'test location',
            },
        ];
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
