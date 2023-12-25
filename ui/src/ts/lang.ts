import Vue from 'vue'; // @ts-ignore
import VueI18n from 'vue-i18n';

const languageKey = 'language';

// @ts-ignore
import Cookies from 'js-cookie';

export const getLanguage = () => Cookies.get(languageKey);
export const setLanguage = (language: string) =>
    Cookies.set(languageKey, language);

// Vant built-in lang
import { Locale } from 'vant';
// @ts-ignore
import enUS from 'vant/es/locale/lang/en-US'; // @ts-ignore
import zhCN from 'vant/es/locale/lang/zh-CN'; // @ts-ignore
import zhTW from 'vant/es/locale/lang/zh-TW'; // @ts-ignore
import jaJP from 'vant/es/locale/lang/ja-JP';

Vue.use(VueI18n);

let en = {
    default: 'default',
    language: 'language',
    settings: 'settings',
    change_language_successfully: 'change language successfully',
    home: 'Home',
    statistics: 'Statistics',
    pick_a_ledger: 'Pick a Ledger',
    all_ledgers: 'All Ledgers',
    ledger_management: 'Ledger Management',
    delete: 'Delete',
    edit: 'Edit',
    date_time_picker_title: 'Choose Date',
    out_of_service: 'out of service',
    add_record: {
        title: 'Add Record',
        type: 'Type',
        amount: 'Amount',
        comment: 'Comment',
        count: 'Count',
    },
    unset: 'unset',
    unknown_record_type: 'unknown type',
    add_record_type: {
        title: 'Add Record Type',
    },
    login_email_sent_prompt:
        "We've sent an email to your email address, please check your email and fill in the verification code below.",
    username: 'username',
    login_email_code: 'code',
};

let zh = {
    default: '默认',
    language: '语言',
    settings: '设置',
    change_language_successfully: '更改语言成功',
    home: '首页',
    statistics: '统计',
    pick_a_ledger: '选择账本',
    all_ledgers: '所有账本',
    ledger_management: '账本管理',
    delete: '删除',
    edit: '编辑',
    date_time_picker_title: '选择日期',
    add_record: {
        title: '记一笔',
        type: '类型',
        amount: '金额',
        count: '数量',
        comment: '备注',
        choose_type: {
            title: '选择类型',
        },
    },
    out_of_service: '定位仅支持中国大陆区域',
    unset: '未设置',
    unknown_record_type: '未知类型',
    add_record_type: {
        title: '添加类型',
    },
    login_email_sent_prompt:
        '我们已向您的电子邮件地址发送了一封电子邮件，请检查您的电子邮件并在下面填写验证码',
    username: '用户名',
    login_email_code: '验证码',
};

const messages: any = {
    'zh-CN': {
        ...zhCN,
        ...zh,
        // ...zhCnLocale
    },
    'zh-TW': {
        ...zhTW,
        // ...zhTwLocale
    },
    'en-US': {
        ...enUS,
        ...en,
        // ...enUsLocale
    },
    'ja-JP': {
        ...jaJP,
        // ...jaJpLocale
    },
};

export const getLocale = (): string => {
    const cookieLanguage = getLanguage();
    if (cookieLanguage) {
        document.documentElement.lang = cookieLanguage;
        return cookieLanguage;
    }

    const language = navigator.language.toLowerCase();
    const locales = Object.keys(messages);
    for (const locale of locales) {
        if (language.indexOf(locale) > -1) {
            document.documentElement.lang = locale;
            return locale;
        }
    }

    // Default language is english
    return 'en-US';
};

const CURRENT_LANG = getLocale();
// first entry
Locale.use(CURRENT_LANG, messages[CURRENT_LANG]);

const i18n = new VueI18n({
    locale: CURRENT_LANG,
    messages,
});

export default i18n;
