package backend;

import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;

/**
 * Created by david on 14.03.16.
 */
@Singleton
public class ConfigurationImpl implements Configuration {

    private Config _config;

    private String _databaseUri;

    public ConfigurationImpl(){
        _config = ConfigFactory.load();
        _databaseUri = _config.getString("database.url");
    }

    @Override
    public String getDatabaseUrl() {
        return _databaseUri;
    }
}
