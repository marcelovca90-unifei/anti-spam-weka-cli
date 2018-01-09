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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.marcelovca90.classification.ClassifierBuilder;
import io.github.marcelovca90.configuration.Configuration;
import io.github.marcelovca90.configuration.ConfigurationLoader;
import io.github.marcelovca90.data.DatasetHelper;
import io.github.marcelovca90.data.DatasetMetadata;
import io.github.marcelovca90.evaluation.EvaluationHelper;
import io.github.marcelovca90.evaluation.TimedEvaluation;
import weka.classifiers.Classifier;
import weka.core.Instances;

@RunWith(MockitoJUnitRunner.class)
public class RunnerTest
{
    private final ClassLoader classLoader = getClass().getClassLoader();

    @Mock
    private ConfigurationLoader configLoader;

    @Mock
    private DatasetHelper datasetHelper;

    @Mock
    private ClassifierBuilder classifierBuilder;

    @Mock
    private EvaluationHelper evaluationHelper;

    @Mock
    private Configuration configuration;

    @InjectMocks
    private Runner runner;

    @Test
    public void main() throws Exception
    {
        // given
        String filename = Paths.get(classLoader.getResource("metadata.txt").toURI()).toFile().getAbsolutePath();
        when(configuration.getClassNamesAndOptions()).thenReturn(
            Arrays.asList(
                Pair.of("weka.classifiers.functions.MultilayerPerceptron", "-L 0.3 -M 0.2 -N 500 -V 33 -S 1 -E 20 -H a"),
                Pair.of("weka.classifiers.bayes.NaiveBayes", "")));
        when(configuration.getMetadataPath()).thenReturn(filename);
        when(configuration.getRuns()).thenReturn(2);
        when(configuration.shouldLoadArff()).thenReturn(false);
        when(configuration.shouldSaveArff()).thenReturn(false);
        when(configLoader.load()).thenReturn(configuration);

        when(classifierBuilder.withClassName(anyString())).thenCallRealMethod();
        when(classifierBuilder.withOptions(anyString())).thenCallRealMethod();
        when(classifierBuilder.customize(any(DatasetMetadata.class))).thenCallRealMethod();
        when(classifierBuilder.build()).thenCallRealMethod();

        String folder = Paths.get(classLoader.getResource("dataset/method/8").toURI()).toFile().getAbsolutePath();
        when(datasetHelper.loadMetadata(anyString())).thenReturn(Sets.newHashSet(new DatasetMetadata(folder, 0, 19)));
        when(datasetHelper.loadDataset(any(DatasetMetadata.class), anyBoolean())).thenCallRealMethod();
        when(datasetHelper.split(any(Instances.class), anyDouble())).thenCallRealMethod();

        // when
        runner.run();

        // then
        verify(classifierBuilder, times(2)).withClassName(anyString());
        verify(classifierBuilder, times(2)).withOptions(anyString());
        verify(classifierBuilder, times(2)).build();

        verify(evaluationHelper, times(2)).addAppender(any(Classifier.class));
        verify(evaluationHelper, times(4)).compute(any(Classifier.class), any(TimedEvaluation.class));
        verify(evaluationHelper, times(4)).print(any(DatasetMetadata.class), any(Classifier.class));
        verify(evaluationHelper, times(2)).summarize(any(DatasetMetadata.class), any(Classifier.class));
        verify(evaluationHelper, times(2)).removeAppender(any(Classifier.class));

        verify(datasetHelper, never()).saveToArff(any(DatasetMetadata.class), any(Instances.class));
    }
}
