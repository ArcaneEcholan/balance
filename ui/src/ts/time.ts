export function getCurrentYear() {
    return new Date().getFullYear();
}

export function getCurrentMonth() {
    return new Date().getMonth() + 1;
}

export function getCurrentYearAndMonth() {
    return `${getCurrentYear()}-${getCurrentMonth()}`;
}
