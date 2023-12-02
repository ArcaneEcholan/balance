import Vue from "vue";// @ts-ignore
import VueI18n from "vue-i18n";

const languageKey = 'language'


// @ts-ignore
import Cookies from 'js-cookie'

export const getLanguage = () => Cookies.get(languageKey)
export const setLanguage = (language: string) => Cookies.set(languageKey, language)


// Vant built-in lang
import {Locale} from 'vant';
// @ts-ignore
import enUS from "vant/es/locale/lang/en-US";// @ts-ignore
import zhCN from "vant/es/locale/lang/zh-CN";// @ts-ignore
import customZhCN from "./locales/zhCn"// @ts-ignore
import customEnUs from "./locales/enUs"// @ts-ignore
import zhTW from "vant/es/locale/lang/zh-TW";// @ts-ignore
import jaJP from "vant/es/locale/lang/ja-JP";

// User defined lang
// import enUsLocale from "./en_US";
// import zhCnLocale from "./zh_CN";
// import zhTwLocale from "./zh_TW";
// import jaJpLocale from "./ja_JP";

Vue.use(VueI18n);

const messages:any = {
    'zh-CN': {
        ...zhCN,
        ...customZhCN
        // ...zhCnLocale
    },
    'zh-TW': {
        ...zhTW,
        // ...zhTwLocale
    },
    'en-US': {
        ...enUS,
        ...customEnUs
        // ...enUsLocale
    },
    'ja-JP': {
        ...jaJP,
        // ...jaJpLocale
    }
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
    return "en-US";
};

const CURRENT_LANG = getLocale();
// first entry
Locale.use(CURRENT_LANG, messages[CURRENT_LANG])

const i18n = new VueI18n({
    locale: CURRENT_LANG,
    messages
});

export default i18n;

