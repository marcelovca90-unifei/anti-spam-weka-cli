package io.github.marcelovca90.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigurationLoader
{
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationLoader.class);

    public Configuration load()
    {
        return load("run.properties");
    }

    protected Configuration load(String filename)
    {
        Configuration config = new Configuration();
        Properties prop = new Properties();

        try (FileInputStream inputStream = new FileInputStream(new File(filename)))
        {
            prop.load(inputStream);

            // anti spam settings
            config.setMetadataPath(prop.getProperty("metadata"));
            config.setRuns(Integer.parseInt(prop.getProperty("runs")));

            // run settings
            config.setTsneAnalysis(Boolean.parseBoolean(prop.getProperty("tsneAnalysis")));
            config.setLoadArff(Boolean.parseBoolean(prop.getProperty("loadArff")));
            config.setShrinkFeatures(Boolean.parseBoolean(prop.getProperty("shrinkFeatures")));
            config.setBalanceClasses(Boolean.parseBoolean(prop.getProperty("balanceClasses")));
            config.setIncludeEmpty(Boolean.parseBoolean(prop.getProperty("includeEmpty")));
            config.setSaveModel(Boolean.parseBoolean(prop.getProperty("saveModel")));
            config.setSaveArff(Boolean.parseBoolean(prop.getProperty("saveArff")));

            // methods and options
            config.setClassNamesAndOptions(loadClassNamesAndOptions(prop));
        }
        catch (IOException e)
        {
            LOGGER.error("Could not read configuration file.", e);
            config = null;
        }

        return config;
    }

    private List<Pair<String, String>> loadClassNamesAndOptions(Properties prop) throws IOException
    {
        List<Pair<String, String>> classNamesAndOptions = new ArrayList<>();

        long classNameCount = prop.keySet().stream().filter(k -> ((String) k).startsWith("className")).count();
        long optionsCount = prop.keySet().stream().filter(k -> ((String) k).startsWith("options")).count();

        if (classNameCount == optionsCount)
        {
            for (int i = 1; i <= classNameCount; i++)
            {
                String className = prop.getProperty("className" + i).replaceAll("^\"|\"$", "");
                String options = prop.getProperty("options" + i).replaceAll("^\"|\"$", "");
                classNamesAndOptions.add(Pair.of(className, options));
            }
        }
        else
        {
            throw new IOException("Malformed properties file (class names and options amount do not match).");
        }

        return classNamesAndOptions;
    }
}
