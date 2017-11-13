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
package io.github.marcelovca90.execution;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import io.github.marcelovca90.classification.ClassifierBuilder;
import io.github.marcelovca90.configuration.Configuration;
import io.github.marcelovca90.configuration.ConfigurationLoader;
import io.github.marcelovca90.data.DatasetHelper;
import io.github.marcelovca90.data.DatasetMetadata;
import io.github.marcelovca90.evaluation.EvaluationHelper;
import io.github.marcelovca90.evaluation.TimedEvaluation;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class Runner
{
    private ConfigurationLoader configLoader;
    private DatasetHelper datasetHelper;
    private ClassifierBuilder classifierBuilder;
    private EvaluationHelper evaluationHelper;

    public Runner()
    {
        configLoader = new ConfigurationLoader();
        datasetHelper = new DatasetHelper();
        classifierBuilder = new ClassifierBuilder();
        evaluationHelper = new EvaluationHelper();
    }

    public void run() throws Exception
    {
        // read configuration from properties file
        Configuration config = configLoader.load();
        Set<DatasetMetadata> datasetMetadata = datasetHelper.loadMetadata(config.getMetadataPath());

        for (DatasetMetadata metadata : datasetMetadata)
        {
            // read dataset from filesystem
            Instances dataset = datasetHelper.loadDataset(metadata, false);

            for (Pair<String, String> classNameAndOptions : config.getClassNamesAndOptions())
            {
                String className = classNameAndOptions.getLeft();
                String options = classNameAndOptions.getRight();

                // create classifier and set its options
                Classifier classifier = classifierBuilder.withClassName(className).withOptions(options).build();

                // add logger for this method
                evaluationHelper.addAppender(classifier);

                for (int run = 0; run < config.getRuns(); run++)
                {
                    // select attributes
                    dataset = datasetHelper.selectAttributes(dataset);
                    metadata.setNoFeaturesAfter(dataset.numAttributes() - 1);

                    // balance
                    datasetHelper.balance(dataset, run);

                    // shuffle
                    datasetHelper.shuffle(dataset, run);

                    // split
                    Pair<Instances, Instances> datasets = datasetHelper.split(dataset, 0.5);
                    Instances trainSet = datasets.getLeft();
                    Instances testSet = datasets.getRight();

                    // add empty instances
                    datasetHelper.addEmptyInstances(testSet, metadata);

                    // create evaluation object
                    TimedEvaluation evaluation = new TimedEvaluation(testSet);

                    // train
                    evaluation.markTrainStart();
                    classifier.buildClassifier(trainSet);
                    evaluation.markTrainEnd();

                    // test
                    evaluation.markTestStart();
                    evaluation.evaluateModel(classifier, testSet);
                    evaluation.markTestEnd();

                    // evaluate single execution
                    evaluationHelper.compute(classifier, evaluation);
                    evaluationHelper.print(metadata, classifier);
                }

                // evaluate all executions for this method
                evaluationHelper.summarize(metadata, classifier);

                // remove logger for this method
                evaluationHelper.removeAppender(classifier);
            }

            // save to arff
            datasetHelper.saveToArff(metadata, dataset);
        }
    }

    public static void main(String[] args) throws Exception
    {
        new Runner().run();
    }
}
