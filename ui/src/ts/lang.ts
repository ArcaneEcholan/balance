import Vue from 'vue'; // @ts-ignore
import VueI18n from 'vue-i18n';
// @ts-ignore
import Cookies from 'js-cookie';
// Vant built-in lang
import { Locale } from 'vant';
// @ts-ignore
import enUS from 'vant/es/locale/lang/en-US'; // @ts-ignore
import zhCN from 'vant/es/locale/lang/zh-CN'; // @ts-ignore
import zhTW from 'vant/es/locale/lang/zh-TW'; // @ts-ignore
import jaJP from 'vant/es/locale/lang/ja-JP';

const languageKey = 'language';

export const getLanguage = () => Cookies.get(languageKey);
export const setLanguage = (language: string) =>
    Cookies.set(languageKey, language);

Vue.use(VueI18n);

let en = {
    toast: {
        loading: 'Loading...',
    },
    success: 'Success',
    confirm: 'Confirm',
    default: 'default',
    language: 'language',
    settings: {
        title: 'Settings',
        language: {
            title: 'Language',
        },
        profile: {
            title: 'Profile',
            reset_password: {
                title: 'Reset Password',
                password: 'Password',
                repeat: 'Repeat',
                password_format_error_prompt:
                    'Password must be at least 6 characters, numbers and letters only',
                password_repeat_error_prompt: 'Password does not match',
            },
            logout: {
                title: 'Logout',
                prompt: 'Are you sure you want to logout?',
            },
        },
    },

    change_language_successfully: 'change language successfully',
    home: 'Home',
    statistics: {
        title: 'Statistics',
        date_picker: 'DateTime',
        ledger_picker: {
            title: 'Ledger',
        },
        exp_ranking: {
            type: 'Expense Ranking By Class',
            record: 'Expense Ranking',
        },
        sibling_month_compare: {
            title: 'Compare with Last Month',
            last_month: 'Last Month',
            this_month: 'This Month',
        },
    },
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
        choose_type: {
            title: 'Choose Type',
        },
    },
    edit_record: {
        title: 'Edit Record',
        ledger: 'Ledger',
        select_ledger: {
            title: 'Select Ledger',
        },
        type: 'Type',
        amount: 'Amount',
        datetime: 'Date Time',
        count: 'Count',
        comment: 'Description',
    },
    add_type: {
        name: 'Name',
    },
    add_record_type: {
        title: 'Add Record Type',
    },
    unset: 'unset',
    unknown_record_type: 'unknown type',
    login_email_sent_prompt:
        "We've sent an email to your email address, please check your email and fill in the verification code below.",
    username: 'username',
    login_email_code: 'code',
    login_greeting: 'Welcome to Ledger',
    login_email: 'email',
    login_email_placeholder: 'Please enter your email',
    login_email_invalid_prompt: 'Please enter a valid email',
};

let zh = {
    toast: {
        loading: '加载中...',
    },
    success: '成功',
    confirm: '确定',
    default: '默认',
    language: '语言',
    settings: {
        title: '设置',
        language: {
            title: '语言',
        },
        profile: {
            title: '个人资料',
            reset_password: {
                title: '重置密码',
                password: '密码',
                repeat: '重复',
                password_format_error_prompt: '密码至少6位，仅支持数字和字母',
                password_repeat_error_prompt: '两次密码不一致',
            },
            logout: {
                title: '退出',
                prompt: '确定退出吗？',
            },
        },
    },

    settings_profile: '个人资料',
    settings_profile_title: '个人资料',
    settings_profile_reset_password: '重置密码',

    change_language_successfully: '更改语言成功',
    home: '首页',
    statistics: {
        title: '统计',
        date_picker: '日期',
        ledger_picker: {
            title: '选择账本',
        },
        exp_ranking: {
            type: '分类消费排行',
            record: '消费排行',
        },
        sibling_month_compare: {
            title: '对比上月',
            last_month: '上月',
            this_month: '本月',
        },
    },
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
    add_type: {
        name: '名称',
    },
    edit_record: {
        title: '编辑记录',

        select_ledger: {
            title: '选择账本',
        },
        ledger: '账本',
        type: '类型',
        amount: '金额',
        datetime: '日期时间',
        count: '数量',
        comment: '备注',
    },
    out_of_service: '定位仅支持中国大陆区域',
    unknown_record_type: '未知类型',
    add_record_type: {
        title: '添加类型',
    },
    login_email_sent_prompt:
        '我们已向您的电子邮件地址发送了一封电子邮件，请检查您的电子邮件并在下面填写验证码',
    username: '用户名',
    login_email_code: '验证码',
    login_email: '邮件',
    login_greeting: '欢迎使用记账本',
    login_email_placeholder: '请输入您的电子邮件',
    login_email_invalid_prompt: '请输入有效的电子邮件',
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
