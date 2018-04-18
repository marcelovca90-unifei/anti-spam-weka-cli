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
package io.github.marcelovca90.evaluation;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.marcelovca90.classification.ClassifierBuilder;
import io.github.marcelovca90.data.DatasetHelper;
import io.github.marcelovca90.data.DatasetMetadata;
import weka.classifiers.Classifier;
import weka.core.Instances;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationHelperTest
{
    private final ClassLoader classLoader = getClass().getClassLoader();
    private final DatasetHelper datasetHelper = new DatasetHelper();
    private final ClassifierBuilder classifierBuilder = new ClassifierBuilder();

    private DatasetMetadata metadata;
    private Instances dataset;
    private Instances trainSet;
    private Instances testSet;
    private Classifier classifier;
    private TimedEvaluation evaluation;

    @InjectMocks
    private EvaluationHelper evaluationHelper;

    @Before
    public void setUp() throws URISyntaxException
    {
        String folder = Paths.get(classLoader.getResource("dataset/method/8").toURI()).toFile().getAbsolutePath();
        metadata = new DatasetMetadata(folder, 0, 19);
        dataset = datasetHelper.loadDataset(metadata, false);

        String className = "weka.classifiers.trees.RandomTree";
        String options = "-K 0 -M 1.0 -V 0.001 -S 1";
        classifier = classifierBuilder.withClassName(className).withOptions(options).build();
    }

    @Test
    public void computePrintSummarize_singleExecution_shouldPrintZeroConfidenceInterval() throws Exception
    {
        // given
        performTraining(dataset);
        performTesting(dataset);

        // when
        evaluationHelper.addAppender(classifier, null);
        evaluationHelper.compute(classifier, evaluation);
        evaluationHelper.print(metadata, classifier);
        evaluationHelper.summarize(metadata, classifier);
        evaluationHelper.removeAppender(classifier, null);
    }

    @Test
    public void computePrintSummarize_multipleExecutions_shouldPrintNonZeroConfidenceInterval() throws Exception
    {
        // given
        for (int i = 0; i < 3; i++)
        {
            datasetHelper.shuffle(dataset, i);
            Pair<Instances, Instances> datasets = datasetHelper.split(dataset, 0.5);
            trainSet = datasets.getLeft();
            testSet = datasets.getRight();

            performTraining(trainSet);
            performTesting(testSet);

            // when
            evaluationHelper.addAppender(classifier, null);
            evaluationHelper.compute(classifier, evaluation);
            evaluationHelper.print(metadata, classifier);
        }

        evaluationHelper.summarize(metadata, classifier);
        evaluationHelper.removeAppender(classifier, null);
    }

    private void performTraining(Instances trainSet) throws Exception
    {
        classifier.buildClassifier(trainSet);
    }

    private void performTesting(Instances testSet) throws Exception
    {
        evaluation = new TimedEvaluation(testSet);
        evaluation.evaluateModel(classifier, testSet);
    }
}
