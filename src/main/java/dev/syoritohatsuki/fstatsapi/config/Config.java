package dev.syoritohatsuki.fstatsapi.config;

public final class Config {
    private final Integer version;
    private Mode mode;
    private final Messages messages;

    public Config(Integer version, Mode mode, Messages messages) {
        this.version = version;
        this.mode = mode;
        this.messages = messages;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Boolean isEnabled() {
        return mode == Mode.ALL || mode == Mode.WITHOUT_LOCATION;
    }

    public Boolean isLocationHide() {
        return mode == Mode.WITHOUT_LOCATION;
    }

    public Mode getMode() {
        return mode;
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

    public enum Mode {
        ALL,
        WITHOUT_LOCATION,
        NOTHING;

        @Override
        public String toString() {
            return switch (this) {
                case ALL -> "All";
                case WITHOUT_LOCATION -> "Without location";
                case NOTHING -> "Nothing";
            };
        }
    }
}