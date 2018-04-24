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
package io.github.marcelovca90.execution;

import java.util.Arrays;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.primes.Primes;

import io.github.marcelovca90.analysis.TsneAnalyser;
import io.github.marcelovca90.classification.ClassifierBuilder;
import io.github.marcelovca90.configuration.Configuration;
import io.github.marcelovca90.configuration.ConfigurationLoader;
import io.github.marcelovca90.data.DatasetHelper;
import io.github.marcelovca90.data.DatasetMetadata;
import io.github.marcelovca90.evaluation.EvaluationHelper;
import io.github.marcelovca90.evaluation.TimedEvaluation;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MLPClassifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.REPTree;
import weka.core.Instances;

public class Runner
{
    private ConfigurationLoader configLoader;
    private DatasetHelper datasetHelper;
    private TsneAnalyser tsneAnalyser;
    private ClassifierBuilder classifierBuilder;
    private EvaluationHelper evaluationHelper;

    public Runner()
    {
        configLoader = new ConfigurationLoader();
        datasetHelper = new DatasetHelper();
        tsneAnalyser = new TsneAnalyser();
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
            Instances dataset = datasetHelper.loadDataset(metadata, config.shouldLoadArff());

            // check if it is a t-SNE analysis
            if (config.isTsneAnalysis())
            {
                tsneAnalyser.run(metadata, dataset, true);
            }
            // otherwise, it is a train/run execution
            else
            {
                // select attributes
                if (config.shouldShrinkFeatures())
                {
                    dataset = datasetHelper.selectAttributes(dataset);
                }
                metadata.setNumFeaturesAfterReduction(dataset.numAttributes() - 1);

                for (Triple<String, String, String> classNamesOptionsAndLogNames : config.getClassNamesOptionsAndLogNames())
                {
                    // parse the classifier class name, options and log name
                    String className = classNamesOptionsAndLogNames.getLeft();
                    String options = classNamesOptionsAndLogNames.getMiddle();
                    String logFilename = classNamesOptionsAndLogNames.getRight();

                    // build the classifier
                    Classifier classifier = classifierBuilder.withClassName(className).withOptions(options).customize(metadata).build();

                    // add logger for this method
                    evaluationHelper.addAppender(classifier, logFilename);

                    // initialize random number generator seed
                    int seed = 2;

                    // run {config.getRuns()} executions
                    for (int run = 0; run < config.getRuns(); run++)
                    {
                        Instances datasetCopy = new Instances(dataset);

                        // balance
                        if (config.shouldBalanceClasses())
                        {
                            datasetHelper.balance(datasetCopy, seed);
                        }

                        // shuffle
                        datasetHelper.shuffle(datasetCopy, seed);

                        // split with percentage depending of whether the classifier uses a validation set or not
                        Class<?>[] validatorClassifiers = { MultilayerPerceptron.class, MLPClassifier.class, REPTree.class };
                        double splitPercentage = Arrays.stream(validatorClassifiers).anyMatch(c -> c.isInstance(classifier)) ? 0.6 : 0.5;
                        Pair<Instances, Instances> datasets = datasetHelper.split(datasetCopy, splitPercentage);
                        Instances trainSet = datasets.getLeft();
                        Instances testSet = datasets.getRight();

                        // add empty instances
                        if (config.shouldIncludeEmpty())
                        {
                            datasetHelper.addEmptyInstances(testSet, metadata);
                        }

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

                        // persist model
                        if (config.shouldSaveModel())
                        {
                            datasetHelper.saveModel(metadata, classifier, seed);
                        }

                        // update random number generator seed
                        seed = Primes.nextPrime(seed + 1);
                    }

                    // evaluate all executions for this method
                    evaluationHelper.summarize(metadata, classifier);

                    // remove logger for this method
                    evaluationHelper.removeAppender(classifier, logFilename);
                }

                // save to arff
                if (config.shouldSaveArff())
                {
                    datasetHelper.saveToArff(metadata, dataset);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        new Runner().run();
    }
}
