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
package io.github.marcelovca90.classification;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.marcelovca90.data.DatasetMetadata;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Utils;

public class ClassifierBuilder
{
    private static final Logger LOGGER = LogManager.getLogger(ClassifierBuilder.class);

    private String className;
    private String options;

    public ClassifierBuilder withClassName(String className)
    {
        this.className = className;
        return this;
    }

    public ClassifierBuilder withOptions(String options)
    {
        this.options = options;
        return this;
    }

    public ClassifierBuilder customize(DatasetMetadata metadata)
    {
        if ((className.endsWith("LibLINEAR") || className.endsWith("LibSVM")) && StringUtils.containsIgnoreCase(options, "-C auto"))
        {
            double C = Math.sqrt(metadata.getNumInstances() * (metadata.getNumFeaturesAfterReduction() - 1) + metadata.getNumClasses());
            options = options.replace("-C auto", String.format("-C %.1f", C));
        }
        else if (className.endsWith("MLPClassifier") && StringUtils.containsIgnoreCase(options, "-P auto -E auto"))
        {
            int procs = Runtime.getRuntime().availableProcessors();
            options = options.replace("-P auto -E auto", String.format("-P %d -E %d", procs, procs));
        }
        else if (className.endsWith("MultilayerPerceptron") && StringUtils.containsIgnoreCase(options, "-H auto"))
        {
            int h1 = (metadata.getNumFeaturesAfterReduction() + metadata.getNumClasses()) / 2, h2 = h1 / 2;
            options = options.replace("-H auto", String.format("-H %d,%d", h1, h2));
        }
        else if (className.endsWith("RBFNetwork") && StringUtils.containsIgnoreCase(options, "-B auto"))
        {
            int B = (int) Math.pow(Math.log(metadata.getNumFeaturesBeforeReduction()) / Math.log(2), 2);
            options = options.replace("-B auto", String.format("-B %d", B));
        }

        return this;
    }

    public Classifier build()
    {
        AbstractClassifier classifier = null;

        try
        {
            Class<?> clazz = Class.forName(className);
            classifier = (AbstractClassifier) clazz.newInstance();
            classifier.setOptions(Utils.splitOptions(options));

            LOGGER.info("Built classifier \"{}\" with options \"{}\".", className, options);
        }
        catch (ClassNotFoundException e)
        {
            LOGGER.error("The provided class was not found.", e);
        }
        catch (IllegalAccessException | InstantiationException e)
        {
            LOGGER.error("Could not instantiate an object of the provided class.", e);
        }
        catch (Exception e)
        {
            LOGGER.error("Could not parse the provided options.", e);
        }

        return classifier;
    }
}
