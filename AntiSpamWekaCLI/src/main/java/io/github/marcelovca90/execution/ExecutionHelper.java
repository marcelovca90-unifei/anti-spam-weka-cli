package io.github.marcelovca90.execution;

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

public class ExecutionHelper
{
    private static final Logger LOGGER = LogManager.getLogger(ExecutionHelper.class);

    // anti spam settings
    public static String metadataPath;
    public static List<Pair<Class<?>, String>> classNamesAndOptions;
    public static int runs;

    // run settings
    public static boolean skipTrain;
    public static boolean skipTest;
    public static boolean shrinkFeatures;
    public static boolean balanceClasses;
    public static boolean includeEmpty;
    public static boolean removeOutliers;
    public static boolean saveArff;
    public static boolean saveModel;
    public static boolean saveSets;
    public static boolean mailResults;

    // e-mail settings
    public static String sender;
    public static String recipients;
    public static String host;
    public static CryptoProtocol protocol;
    public static String username;
    public static String password;

    public void loadConfiguration(String filename)
    {
        Properties prop = new Properties();

        try (FileInputStream inputStream = new FileInputStream(new File(filename)))
        {
            prop.load(inputStream);

            // anti spam settings
            metadataPath = prop.getProperty("metadata");
            classNamesAndOptions = loadClassNamesAndOptions(prop);
            runs = Integer.parseInt(prop.getProperty("runs"));

            // run settings
            skipTrain = Boolean.parseBoolean(prop.getProperty("skipTrain"));
            skipTest = Boolean.parseBoolean(prop.getProperty("skipTest"));
            shrinkFeatures = Boolean.parseBoolean(prop.getProperty("shrinkFeatures"));
            balanceClasses = Boolean.parseBoolean(prop.getProperty("balanceClasses"));
            includeEmpty = Boolean.parseBoolean(prop.getProperty("includeEmpty"));
            removeOutliers = Boolean.parseBoolean(prop.getProperty("removeOutliers"));
            saveArff = Boolean.parseBoolean(prop.getProperty("saveArff"));
            saveModel = Boolean.parseBoolean(prop.getProperty("saveModel"));
            saveSets = Boolean.parseBoolean(prop.getProperty("saveSets"));
            mailResults = Boolean.parseBoolean(prop.getProperty("mailResults"));

            // e-mail settings
            sender = prop.getProperty("sender");
            recipients = prop.getProperty("recipients");
            host = prop.getProperty("server");
            protocol = CryptoProtocol.valueOf(prop.getProperty("protocol"));
            username = prop.getProperty("username");
            password = new String(DatatypeConverter.parseBase64Binary(prop.getProperty("password")));
        }
        catch (IOException e)
        {
            LOGGER.error("Could not read configuration file.", e);
        }
        catch (ClassNotFoundException e)
        {
            LOGGER.error("Could not find one or more classes.", e);
        }
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
