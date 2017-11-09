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
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.marcelovca90.classification.ClassifierBuilder;
import io.github.marcelovca90.configuration.Configuration;
import io.github.marcelovca90.configuration.ConfigurationLoader;
import io.github.marcelovca90.data.DataHelper;
import io.github.marcelovca90.evaluation.EvaluationHelper;
import io.github.marcelovca90.evaluation.TimedEvaluation;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class Runner
{
    private static final Logger LOGGER = LogManager.getLogger(Runner.class);

    private static final ConfigurationLoader configLoader = new ConfigurationLoader();
    private static final DataHelper dataHelper = new DataHelper();
    private static final ClassifierBuilder classifierBuilder = new ClassifierBuilder();
    private static final EvaluationHelper evaluationHelper = new EvaluationHelper();

    public static void main(String[] args) throws Exception
    {
        Configuration config = configLoader.load();
        Set<Triple<String, Integer, Integer>> metadata = dataHelper.loadMetadata(config.getMetadataPath());

        for (Triple<String, Integer, Integer> metadatum : metadata)
        {
            Instances dataset = dataHelper.loadDataset(metadatum, false);

            for (Pair<String, String> classNameAndOptions : config.getClassNamesAndOptions())
            {
                String className = classNameAndOptions.getLeft();
                String options = classNameAndOptions.getRight();

                Classifier classifier = classifierBuilder.withClassName(className).withOptions(options).build();

                for (int run = 0; run < config.getRuns(); run++)
                {
                    // shuffle
                    dataHelper.shuffle(dataset, run);

                    // split
                    Pair<Instances, Instances> datasets = dataHelper.split(dataset, 0.5);

                    // train
                    Instances trainSet = datasets.getLeft();
                    classifier.buildClassifier(trainSet);

                    // test
                    Instances testSet = datasets.getRight();
                    TimedEvaluation evaluation = new TimedEvaluation(testSet);
                    evaluation.evaluateModel(classifier, testSet);

                    // evaluate
                    evaluationHelper.compute(classifier, evaluation);
                    evaluationHelper.print(classifier);
                }
                evaluationHelper.summarize(classifier);
            }
        }
    }
}
