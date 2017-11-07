/*******************************************************************************
 * Copyright (C) 2017 Marcelo Vinícius Cysneiros Aragão
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
package io.github.marcelovca90.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import weka.classifiers.AbstractClassifier;
import weka.core.Utils;

public class MethodHelper
{
    private static final Logger LOGGER = LogManager.getLogger(MethodHelper.class);

    public AbstractClassifier build(String className, String options)
    {
        AbstractClassifier classifier = null;

        try
        {
            Class<?> clazz = Class.forName(className);
            classifier = (AbstractClassifier) clazz.newInstance();
            classifier.setOptions(Utils.splitOptions(options));
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
