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
package io.github.marcelovca90.evaluation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.marcelovca90.data.ClassType;
import weka.classifiers.AbstractClassifier;

public class EvaluationHelper
{
    private static final Logger LOGGER = LogManager.getLogger(EvaluationHelper.class);
    private static final Map<Class<? extends AbstractClassifier>, Map<String, DescriptiveStatistics>> RESULTS = new HashMap<>();

    public void compute(Class<? extends AbstractClassifier> clazz, TimedEvaluation evaluation)
    {
        int hamIndex = ClassType.HAM.ordinal();
        int spamIndex = ClassType.SPAM.ordinal();

        aggregate(clazz, "hamPrecision", 100.0 * evaluation.precision(hamIndex));
        aggregate(clazz, "spamPrecision", 100.0 * evaluation.precision(spamIndex));
        aggregate(clazz, "weightedPrecision", 100.0 * evaluation.weightedPrecision());

        aggregate(clazz, "hamRecall", 100.0 * evaluation.recall(hamIndex));
        aggregate(clazz, "spamRecall", 100.0 * evaluation.recall(spamIndex));
        aggregate(clazz, "weightedRecall", 100.0 * evaluation.weightedRecall());

        aggregate(clazz, "hamAreaUnderPRC", 100.0 * evaluation.areaUnderPRC(hamIndex));
        aggregate(clazz, "spamAreaUnderPRC", 100.0 * evaluation.areaUnderPRC(spamIndex));
        aggregate(clazz, "weightedAreaUnderPRC", 100.0 * evaluation.weightedAreaUnderPRC());

        aggregate(clazz, "hamAreaUnderROC", 100.0 * evaluation.areaUnderROC(hamIndex));
        aggregate(clazz, "spamAreaUnderROC", 100.0 * evaluation.areaUnderROC(spamIndex));
        aggregate(clazz, "weightedAreaUnderROC", 100.0 * evaluation.weightedAreaUnderROC());

        aggregate(clazz, "hamFMeasure", 100.0 * evaluation.fMeasure(hamIndex));
        aggregate(clazz, "spamFMeasure", 100.0 * evaluation.fMeasure(spamIndex));
        aggregate(clazz, "weightedFMeasure", 100.0 * evaluation.weightedFMeasure());

        aggregate(clazz, "trainingTime", evaluation.trainingTime());
        aggregate(clazz, "testingTime", evaluation.testingTime());
    }

    public void print(Class<? extends AbstractClassifier> clazz)
    {
        LOGGER.info(RESULTS.get(clazz).keySet().stream().map(k -> StringUtils.rightPad(k, 15)).collect(Collectors.joining("\t")));
        LOGGER.info(RESULTS.get(clazz).values().stream().map(v -> String.format("%.2f ± %.2f", v.getMean(), confidenceInterval(v, 0.05))).collect(Collectors.joining("\t")));
    }

    private void aggregate(Class<? extends AbstractClassifier> clazz, String metric, double value)
    {
        RESULTS.putIfAbsent(clazz, new LinkedHashMap<>());
        RESULTS.get(clazz).putIfAbsent(metric, new DescriptiveStatistics());
        RESULTS.get(clazz).get(metric).addValue(value);
    }

    private double confidenceInterval(DescriptiveStatistics statistics, double significance)
    {
        if (statistics.getN() <= 1)
            return 0.0;

        TDistribution tDist = new TDistribution(statistics.getN() - 1);
        double a = tDist.inverseCumulativeProbability(1.0 - significance / 2);
        return a * statistics.getStandardDeviation() / Math.sqrt(statistics.getN());
    }
}
