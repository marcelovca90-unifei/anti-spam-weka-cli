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
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.FileAppender;

import io.github.marcelovca90.data.ClassType;
import io.github.marcelovca90.data.DatasetMetadata;
import weka.classifiers.Classifier;

public class EvaluationHelper
{
    private static final Logger LOGGER = LogManager.getLogger(EvaluationHelper.class);
    private static final Map<Classifier, Map<String, DescriptiveStatistics>> RESULTS = new HashMap<>();
    private static final Map<Classifier, Appender> APPENDERS = new HashMap<>();

    public void addAppender(Classifier classifier)
    {
        String simpleName = classifier.getClass().getSimpleName();
        String loggerName = "logs/" + simpleName + ".log";

        if (!APPENDERS.containsKey(classifier))
        {
            Appender appender = FileAppender.newBuilder().withFileName(loggerName).withName(simpleName).build();
            ((org.apache.logging.log4j.core.Logger) LOGGER).addAppender(appender);
            appender.start();
            APPENDERS.put(classifier, appender);
        }
    }

    public void removeAppender(Classifier classifier)
    {
        Appender appender = APPENDERS.get(classifier);
        ((org.apache.logging.log4j.core.Logger) LOGGER).removeAppender(appender);
        appender.stop();
        APPENDERS.remove(classifier);
    }

    public void compute(Classifier classifier, TimedEvaluation evaluation)
    {
        int hamIndex = ClassType.HAM.ordinal();
        int spamIndex = ClassType.SPAM.ordinal();

        aggregate(classifier, "hamPrecision", 100.0 * evaluation.precision(hamIndex));
        aggregate(classifier, "spamPrecision", 100.0 * evaluation.precision(spamIndex));
        aggregate(classifier, "weightedPrecision", 100.0 * evaluation.weightedPrecision());

        aggregate(classifier, "hamRecall", 100.0 * evaluation.recall(hamIndex));
        aggregate(classifier, "spamRecall", 100.0 * evaluation.recall(spamIndex));
        aggregate(classifier, "weightedRecall", 100.0 * evaluation.weightedRecall());

        aggregate(classifier, "hamAreaUnderPRC", 100.0 * evaluation.areaUnderPRC(hamIndex));
        aggregate(classifier, "spamAreaUnderPRC", 100.0 * evaluation.areaUnderPRC(spamIndex));
        aggregate(classifier, "weightedAreaUnderPRC", 100.0 * evaluation.weightedAreaUnderPRC());

        aggregate(classifier, "hamAreaUnderROC", 100.0 * evaluation.areaUnderROC(hamIndex));
        aggregate(classifier, "spamAreaUnderROC", 100.0 * evaluation.areaUnderROC(spamIndex));
        aggregate(classifier, "weightedAreaUnderROC", 100.0 * evaluation.weightedAreaUnderROC());

        aggregate(classifier, "hamFMeasure", 100.0 * evaluation.fMeasure(hamIndex));
        aggregate(classifier, "spamFMeasure", 100.0 * evaluation.fMeasure(spamIndex));
        aggregate(classifier, "weightedFMeasure", 100.0 * evaluation.weightedFMeasure());

        aggregate(classifier, "trainingTime", evaluation.trainingTime());
        aggregate(classifier, "testingTime", evaluation.testingTime());
    }

    public void print(DatasetMetadata metadata, Classifier classifier)
    {
        if (RESULTS.get(classifier).values().stream().allMatch(stat -> stat.getN() == 1))
        {
            LOGGER.info(
                buildHeaderPrefix(classifier) + "\t" + RESULTS
                    .get(classifier)
                    .keySet()
                    .stream()
                    .map(k -> StringUtils.rightPad(k, 15))
                    .collect(Collectors.joining("\t")));
        }

        LOGGER.info(
            buildBodyPrefix(metadata, classifier) + "\t" + RESULTS
                .get(classifier)
                .entrySet()
                .stream()
                .map(this::formatSingleOutput)
                .collect(Collectors.joining("\t")));
    }

    public void summarize(DatasetMetadata metadata, Classifier classifier)
    {
        LOGGER.info(
            buildHeaderPrefix(classifier) + "\t" + RESULTS
                .get(classifier)
                .keySet()
                .stream()
                .map(k -> StringUtils.rightPad(k, 15))
                .collect(Collectors.joining("\t")));

        LOGGER.info(
            buildBodyPrefix(metadata, classifier) + "\t" + RESULTS
                .get(classifier)
                .entrySet()
                .stream()
                .map(this::formatCompositeOutput)
                .collect(Collectors.joining("\t")));
    }

    private void aggregate(Classifier classifier, String metric, double value)
    {
        RESULTS.putIfAbsent(classifier, new LinkedHashMap<>());
        RESULTS.get(classifier).putIfAbsent(metric, new DescriptiveStatistics());
        RESULTS.get(classifier).get(metric).addValue(value);
    }

    private String buildHeaderPrefix(Classifier classifier)
    {
        return String.format("%s\t%s\t%s\t%s\t%s", "name", "featureSelection", "noFeaturesBefore", "noFeaturesAfter", "classifier");
    }

    private String buildBodyPrefix(DatasetMetadata metadata, Classifier classifier)
    {
        return String.format("%s\t%s\t%d\t%d\t%s", metadata.getName(), metadata.getFeatureSelection(), metadata.getNumFeaturesBeforeReduction(), metadata.getNumFeaturesAfterReduction(), classifier.getClass().getSimpleName());
    }

    private double confidenceInterval(DescriptiveStatistics statistics, double significance)
    {
        if (statistics.getN() <= 1)
            return 0.0;

        TDistribution tDist = new TDistribution(statistics.getN() - 1);
        double a = tDist.inverseCumulativeProbability(1.0 - significance / 2);
        return a * statistics.getStandardDeviation() / Math.sqrt(statistics.getN());
    }

    private String formatMillis(double millis)
    {
        return DurationFormatUtils.formatDurationHMS((Double.valueOf(Math.abs(millis))).longValue());
    }

    private String formatSingleOutput(Entry<String, DescriptiveStatistics> entry)
    {
        int lastIndex = (int) entry.getValue().getN() - 1;
        double lastValue = entry.getValue().getValues()[lastIndex];
        if (entry.getKey().endsWith("Time"))
            return String.format("%s", formatMillis(lastValue));
        else
            return String.format("%.2f", lastValue);
    }

    private String formatCompositeOutput(Entry<String, DescriptiveStatistics> entry)
    {
        double mean = entry.getValue().getMean();
        double confInterval = confidenceInterval(entry.getValue(), 0.05);
        if (entry.getKey().endsWith("Time"))
            return String.format("%s ± %s", formatMillis(mean), formatMillis(confInterval));
        else
            return String.format("%.2f ± %.2f", mean, confInterval);
    }
}
