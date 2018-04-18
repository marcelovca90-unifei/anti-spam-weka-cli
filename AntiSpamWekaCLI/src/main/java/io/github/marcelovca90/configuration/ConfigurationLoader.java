/*******************************************************************************
 * Copyright (C) 2018 Marcelo Vinícius Cysneiros Aragão
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package io.github.marcelovca90.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.tuple.Triple;
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
            config.setClassNamesOptionsAndLogFilenames(loadClassNamesOptionsAndLogNames(prop));
        }
        catch (IOException e)
        {
            LOGGER.error("Could not read configuration file.", e);
            config = null;
        }

        return config;
    }

    private List<Triple<String, String, String>> loadClassNamesOptionsAndLogNames(Properties prop) throws IOException
    {
        List<Triple<String, String, String>> classNamesOptionsAndLogNames = new ArrayList<>();

        long classNameCount = prop.keySet().stream().filter(k -> ((String) k).startsWith("className")).count();
        long optionsCount = prop.keySet().stream().filter(k -> ((String) k).startsWith("options")).count();

        if (classNameCount == optionsCount)
        {
            for (int i = 1; i <= classNameCount; i++)
            {
                String className = prop.getProperty("className" + i).replaceAll("^\"|\"$", "");
                String options = prop.getProperty("options" + i).replaceAll("^\"|\"$", "");
                String logName = Optional.ofNullable(prop.getProperty("logName" + i)).orElse("").replaceAll("^\"|\"$", "");

                classNamesOptionsAndLogNames.add(Triple.of(className, options, logName));
            }
        }
        else
        {
            throw new IOException("Malformed properties file (class names and options amount do not match).");
        }

        return classNamesOptionsAndLogNames;
    }
}
