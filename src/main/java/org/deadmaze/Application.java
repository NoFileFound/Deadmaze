package org.deadmaze;

// Imports
import java.util.Map;
import lombok.Getter;
import org.deadmaze.database.DBManager;
import org.deadmaze.libraries.GeoIP;
import org.deadmaze.libraries.JakartaMail;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.deadmaze.properties.ConfigLoader;
import org.deadmaze.properties.configs.*;
import org.deadmaze.libraries.TranslationManager;

public final class Application {
    @Getter private static final Logger logger = LoggerFactory.getLogger(Application.class);
    @Getter private static final TranslationManager translationManager = new TranslationManager();
    @Getter private static final Reflections reflector = new Reflections("org.deadmaze");
    @Getter private static SwfConfig.SWFClass swfInfo;
    @Getter private static PropertiesConfig.PropertiesClass propertiesInfo;
    @Getter private static Map<String, String[]> languageInfo;
    @Getter private static Map<String, Map<String, String>> captchaInfo;

    @SuppressWarnings("unchecked")
    private static void loadConfigVariables() {
        swfInfo = (SwfConfig.SWFClass) ConfigLoader.getProperty(SwfConfig.class).getInstance();
        propertiesInfo = (PropertiesConfig.PropertiesClass) ConfigLoader.getProperty(PropertiesConfig.class).getInstance();
        languageInfo = (Map<String, String[]>) ConfigLoader.getProperty(LanguageConfig.class).getInstance();
        captchaInfo = (Map<String, Map<String, String>>) ConfigLoader.getProperty(CaptchaConfig.class).getInstance();
    }

    public static void main(String[] args) {
        ConfigLoader.loadConfig();
        loadConfigVariables();
        GeoIP.loadGeoDatabase();
        JakartaMail.initSmtpConfig();
        DBManager.initializeDatabase();
        Server server = new Server();
        server.startServer();
    }
}