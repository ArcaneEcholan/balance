class PageConfig {
    private title: string;

    constructor(title: string) {
        this.title = title;
    }

    public getTitle(): string {
        return this.title;
    }

    public setTitle(title: string): void {
        this.title = title;
    }
}

let pageConfig = new PageConfig('title');
export { PageConfig };
export default pageConfig;
