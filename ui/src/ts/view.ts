import { Toast } from 'vant';
import i18n from '@/ts/lang';
export function globalLoadingStart() {
    Toast.loading({
        message: i18n.t('toast.loading') as string,
        forbidClick: true,
        duration: 0,
    });
}

export function globalLoadingStop() {
    Toast.clear();
}
