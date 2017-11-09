package io.github.marcelovca90.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.marcelovca90.mail.CryptoProtocol;

public class ConfigurationHelper
{
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationHelper.class);

    public Configuration load(String filename)
    {
        Configuration config = new Configuration();
        Properties prop = new Properties();

        try (FileInputStream inputStream = new FileInputStream(new File(filename)))
        {
            prop.load(inputStream);

            // anti spam settings
            config.setMetadataPath(prop.getProperty("metadata"));
            config.setClassNamesAndOptions(loadClassNamesAndOptions(prop));
            config.setRuns(Integer.parseInt(prop.getProperty("runs")));

            // run settings
            config.setSkipTrain(Boolean.parseBoolean(prop.getProperty("skipTrain")));
            config.setSkipTest(Boolean.parseBoolean(prop.getProperty("skipTest")));
            config.setShrinkFeatures(Boolean.parseBoolean(prop.getProperty("shrinkFeatures")));
            config.setBalanceClasses(Boolean.parseBoolean(prop.getProperty("balanceClasses")));
            config.setIncludeEmpty(Boolean.parseBoolean(prop.getProperty("includeEmpty")));
            config.setRemoveOutliers(Boolean.parseBoolean(prop.getProperty("removeOutliers")));
            config.setSaveArff(Boolean.parseBoolean(prop.getProperty("saveArff")));
            config.setSaveModel(Boolean.parseBoolean(prop.getProperty("saveModel")));
            config.setSaveSets(Boolean.parseBoolean(prop.getProperty("saveSets")));
            config.setMailResults(Boolean.parseBoolean(prop.getProperty("mailResults")));

            // e-mail settings
            config.setSender(prop.getProperty("sender"));
            config.setRecipients(prop.getProperty("recipients"));
            config.setHost(prop.getProperty("server"));
            config.setProtocol(CryptoProtocol.valueOf(prop.getProperty("protocol")));
            config.setUsername(prop.getProperty("username"));
            config.setPassword(new String(DatatypeConverter.parseBase64Binary(prop.getProperty("password"))));
        }
        catch (IOException e)
        {
            LOGGER.error("Could not read configuration file.", e);
            config = null;
        }
        catch (ClassNotFoundException e)
        {
            LOGGER.error("Could not find one or more classes.", e);
            config = null;
        }

        return config;
    }

    private List<Pair<Class<?>, String>> loadClassNamesAndOptions(Properties prop) throws ClassNotFoundException, IOException
    {
        List<Pair<Class<?>, String>> classNamesAndOptions = new ArrayList<>();

        long classNameCount = prop.keySet().stream().filter(k -> ((String) k).startsWith("className")).count();
        long optionsCount = prop.keySet().stream().filter(k -> ((String) k).startsWith("options")).count();

        if (classNameCount == optionsCount)
        {
            for (int i = 1; i <= classNameCount; i++)
            {
                String className = prop.getProperty("className" + i).replaceAll("^\"|\"$", "");
                String options = prop.getProperty("options" + i).replaceAll("^\"|\"$", "");
                Class<?> clazz = Class.forName(className);
                classNamesAndOptions.add(Pair.of(clazz, options));
            }
        }
        else
        {
            throw new IOException("Malformed properties file (class names and options amount do not match).");
        }

        return classNamesAndOptions;
    }
}
