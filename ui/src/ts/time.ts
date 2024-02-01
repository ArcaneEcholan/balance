export function getCurrentYear() {
    return new Date().getFullYear();
}

export function getCurrentMonth() {
    return new Date().getMonth() + 1;
}

export function getCurrentYearAndMonth() {
    return `${getCurrentYear()}-${getCurrentMonth()}`;
}

export function getYearAndMonthAsString(date: string | Date) {
    let currentDate = new Date(date);
    return currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1);
}
