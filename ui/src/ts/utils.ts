export function get_display_file_size(bytes: number) {
    const g = bytes / 1024.0 / 1024.0 / 1024.0;
    if (g >= 1) {
        return `${g.toFixed(2)} G`;
    }
    const m = bytes / 1024.0 / 1024.0;
    if (m >= 1) {
        return `${m.toFixed(2)} M`;
    }
    const k = bytes / 1024.0;
    if (k >= 1) {
        return `${k.toFixed(2)} k`;
    }
    const b = bytes;
    if (b >= 1) {
        return `${b.toFixed(2)} b`;
    }
    return '0 b';
}

export function get_display_time(updateTime: number) {
    const now = new Date().getTime();
    const second = Math.floor((now - updateTime) / 1000);
    const minute = Math.floor(second / 60);
    const hour = Math.floor(minute / 60);
    const day = Math.floor(hour / 24);
    const month = Math.floor(day / 31);
    const year = Math.floor(month / 12);

    if (year > 0) {
        return year + '年前';
    } else if (month > 0) {
        return month + '月前';
    } else if (day > 0) {
        let ret = day + '天前';
        if (day >= 7 && day < 14) {
            ret = '1周前';
        } else if (day >= 14 && day < 21) {
            ret = '2周前';
        } else if (day >= 21 && day < 28) {
            ret = '3周前';
        } else if (day >= 28 && day < 31) {
            ret = '4周前';
        }
        return ret;
    } else if (hour > 0) {
        return hour + '小时前';
    } else if (minute > 0) {
        return minute + '分钟前';
    } else if (second > 0) {
        return second + '秒前';
    } else {
        return '刚刚';
    }
}

export function convertToShortDateTime(timeString: string) {
    // Create a Date object from the input time string
    const dateTime = new Date(timeString);

    // Extract month and day values
    const month = dateTime.getMonth() + 1; // Months are 0-indexed
    const day = dateTime.getDate();

    // Format hours and minutes with leading zeros
    const hours = String(dateTime.getHours()).padStart(2, '0');
    const minutes = String(dateTime.getMinutes()).padStart(2, '0');

    // Construct the desired formatted string
    const formattedString = `${month}-${day} ${hours}:${minutes}`;

    return formattedString;
}

export function
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
export function
isFloat(number: number) {
    return Number(number) === number && !Number.isInteger(number) || Number.isInteger(number);
}

export function
isPositiveInteger(number: number): boolean {
    return Number.isInteger(number) && number > 0;
}
