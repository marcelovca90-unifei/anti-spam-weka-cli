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

import java.nio.file.Paths;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.marcelovca90.common.TimedEvaluation;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

@RunWith (MockitoJUnitRunner.class)
public class ValidationHelperTest
{
    private final ClassLoader classLoader = getClass().getClassLoader();
    private final DatasetHelper datasetHelper = new DatasetHelper();
    private final MethodHelper methodHelper = new MethodHelper();
    private final Class<? extends AbstractClassifier> clazz = MultilayerPerceptron.class;

    private AbstractClassifier classifier;
    private Instances trainSet;
    private Instances testSet;
    private TimedEvaluation evaluation;

    @InjectMocks
    private ValidationHelper validationHelper;

    @Test
    public void computeAndPrint_singleExecution_shouldPrintZeroConfidenceInterval() throws Exception
    {
        // given
        performTraining(0);
        performTesting();

        // when
        validationHelper.compute(clazz, evaluation);
        validationHelper.print(clazz);
    }

    @Test
    public void computeAndPrint_multipleExecutions_shouldPrintNonZeroConfidenceInterval() throws Exception
    {
        // given
        for (int i = 0; i < 3; i++)
        {
            performTraining(i);
            performTesting();
            validationHelper.compute(clazz, evaluation);
        }

        // when
        validationHelper.print(clazz);
    }

    private void performTraining(int seed) throws Exception
    {
        String folder = Paths.get(classLoader.getResource("data/8").toURI()).toFile().getAbsolutePath();
        Triple<String, Integer, Integer> metadatum = Triple.of(folder, 0, 19);
        Instances dataset = datasetHelper.loadDataset(metadatum, false);
        datasetHelper.shuffle(dataset, seed);
        Pair<Instances, Instances> datasets = datasetHelper.split(dataset, 0.5);
        trainSet = datasets.getLeft();
        testSet = datasets.getRight();

        String className = "weka.classifiers.functions.MultilayerPerceptron";
        String options = "-L 0.3 -M 0.2 -N 100 -V 33 -S 1 -E 20 -H a";
        classifier = methodHelper.build(className, options);

        classifier.buildClassifier(trainSet);
    }

    private void performTesting() throws Exception
    {
        evaluation = new TimedEvaluation(testSet);
        evaluation.evaluateModel(classifier, testSet);
    }
}
