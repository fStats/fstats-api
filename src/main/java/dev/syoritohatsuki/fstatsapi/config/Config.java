package dev.syoritohatsuki.fstatsapi.config;

public final class Config {
    private final Integer version;
    private final Boolean enabled;
    private final Boolean hideLocation;
    private final Messages messages;

    public Config(Integer version, Boolean enabled, Boolean hideLocation, Messages messages) {
        this.version = version;
        this.enabled = enabled;
        this.hideLocation = hideLocation;
        this.messages = messages;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public Boolean isLocationHide() {
        return hideLocation;
    }

    public Messages getMessages() {
        return messages;
    }

    public Integer getVersion() {
        return version;
    }

    public static final class Messages {
        private final Boolean infos;
        private final Boolean warnings;
        private final Boolean errors;

        public Messages(Boolean notify, Boolean warnings, Boolean errors) {
            this.infos = notify;
            this.warnings = warnings;
            this.errors = errors;
        }

        public Boolean isInfosEnabled() {
            return infos;
        }

        public Boolean isWarningsEnabled() {
            return warnings;
        }

        public Boolean isErrorsEnabled() {
            return errors;
        }
    }
}