package org.deadmaze.properties.configs;

// Imports
import org.deadmaze.libraries.JsonLoader;
import org.deadmaze.properties.Property;

public final class PropertiesConfig implements Property {
    private PropertiesClass propertiesInstance;

    @Override
    public Object getInstance() {
        return this.propertiesInstance;
    }

    @Override
    public void loadFile() {
        this.propertiesInstance = JsonLoader.loadJson("properties.json", PropertiesClass.class);
        if(this.propertiesInstance == null) {
            System.exit(1);
        }
    }

    @Override
    public void saveFile() {

    }

    public static class PropertiesClass {
        public String database_url = "mongodb://localhost:27017";
        public String collection_name = "transformice";
        public boolean is_debug = true;
        public boolean twitchStreaming;
        public boolean allow_email = true;
        public boolean use_tag_system = true;
        public int login_attempts;
        public int hazardousdamageindex;
        public Timers timers;
        public SMTP email_info;

        public static class Timers {
            public TimerObject keep_alive = new TimerObject();
            public TimerObject create_account = new TimerObject();
            public TimerObject reload_cafe = new TimerObject();
            public TimerObject create_cafe_topic = new TimerObject();
            public TimerObject create_cafe_post = new TimerObject();
            public TimerObject marriage = new TimerObject();
            public TimerObject chat_message = new TimerObject();
        }

        public static class TimerObject {
            public boolean enable = false;
            public int delay = 0;
        }

        public static class SMTP {
            public String smtpHost;
            public Integer smtpPort;
            public String smtpUsername;
            public String smtpPassword;
        }
    }
}