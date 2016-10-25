package com.farmlogs.wiretap.weaving.plugin

class WiretapExtension {
    def enabled = true

    def setEnabled(boolean enabled) {
        this.enabled = enabled
    }

    def getEnabled() {
        return enabled;
    }
}
