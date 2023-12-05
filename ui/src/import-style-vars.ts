import settings from "@/settings";

function set(name:string, value: string) {
    document.documentElement.style.setProperty(name, value)
}

set("--transition-duration", settings.animation.duration + "ms")
set("--transition-easing", settings.animation.easing)
