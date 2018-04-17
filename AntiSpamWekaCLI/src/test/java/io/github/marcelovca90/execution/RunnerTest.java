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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.marcelovca90.analysis.TsneAnalyser;
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
    private TsneAnalyser tsneAnalyser;

    @Mock
    private ClassifierBuilder classifierBuilder;

    @Mock
    private EvaluationHelper evaluationHelper;

    @Mock
    private Configuration configuration;

    @InjectMocks
    private Runner runner;

    @Test
    public void main_falseConfiguration_shouldNotCallMethods() throws Exception
    {
        // given
        mockCalls(2, false, false, false, false, false, false);

        // when
        runner.run();

        // then
        verifyCalls(false, false, false, false, false);
    }

    @Test
    public void main_trueConfiguration_shouldCallMethods() throws Exception
    {
        // given
        mockCalls(2, false, true, true, true, true, true);

        // when
        runner.run();

        // then
        verifyCalls(true, true, true, true, true);
    }

    @Test
    public void main_tsneConfiguration_shouldCallMethods() throws Exception
    {
        // given
        mockCalls(2, true, false, false, false, false, false);

        // when
        runner.run();

        // then
        verify(tsneAnalyser).run(any(DatasetMetadata.class), any(Instances.class), anyBoolean());
    }

    private void mockCalls(int runs, boolean tsne, boolean shrink, boolean balance, boolean addEmpty, boolean saveModel, boolean saveArff) throws URISyntaxException
    {
        String filename = Paths.get(classLoader.getResource("metadata.txt").toURI()).toFile().getAbsolutePath();

        List<Triple<String, String, String>> classNamesOptionsAndLogFilenames = Arrays.asList(
            Triple.of("weka.classifiers.functions.MultilayerPerceptron", "-L 0.3 -M 0.2 -N 500 -V 33 -S 1 -E 20 -H a", ""),
            Triple.of("weka.classifiers.bayes.NaiveBayes", "", ""));

        when(configuration.getClassNamesOptionsAndLogNames()).thenReturn(classNamesOptionsAndLogFilenames);
        when(configuration.getMetadataPath()).thenReturn(filename);
        when(configuration.getRuns()).thenReturn(runs);
        when(configuration.isTsneAnalysis()).thenReturn(tsne);
        when(configuration.shouldShrinkFeatures()).thenReturn(shrink);
        when(configuration.shouldBalanceClasses()).thenReturn(balance);
        when(configuration.shouldIncludeEmpty()).thenReturn(addEmpty);
        when(configuration.shouldSaveModel()).thenReturn(saveModel);
        when(configuration.shouldSaveArff()).thenReturn(saveArff);
        when(configLoader.load()).thenReturn(configuration);

        when(classifierBuilder.withClassName(anyString())).thenCallRealMethod();
        when(classifierBuilder.withOptions(anyString())).thenCallRealMethod();
        when(classifierBuilder.customize(any(DatasetMetadata.class))).thenCallRealMethod();
        when(classifierBuilder.build()).thenCallRealMethod();

        String folder = Paths.get(classLoader.getResource("dataset/method/8").toURI()).toFile().getAbsolutePath();
        when(datasetHelper.loadMetadata(anyString())).thenReturn(Sets.newHashSet(new DatasetMetadata(folder, 0, 19)));
        when(datasetHelper.loadDataset(any(DatasetMetadata.class), anyBoolean())).thenCallRealMethod();
        if (shrink) when(datasetHelper.selectAttributes(any(Instances.class))).thenCallRealMethod();
        when(datasetHelper.split(any(Instances.class), anyDouble())).thenCallRealMethod();

        doNothing().when(tsneAnalyser).run(any(DatasetMetadata.class), any(Instances.class), anyBoolean());
    }

    private void verifyCalls(boolean shrink, boolean balance, boolean addEmpty, boolean saveModel, boolean saveArff)
    {
        verify(datasetHelper, shrink ? times(1) : never()).selectAttributes(any(Instances.class));
        verify(datasetHelper, balance ? times(4) : never()).balance(any(Instances.class), anyInt());
        verify(datasetHelper, addEmpty ? times(4) : never()).addEmptyInstances(any(Instances.class), any(DatasetMetadata.class));
        verify(datasetHelper, saveModel ? times(4) : never()).saveModel(any(DatasetMetadata.class), any(Classifier.class), anyInt());
        verify(datasetHelper, saveArff ? times(1) : never()).saveToArff(any(DatasetMetadata.class), any(Instances.class));

        verify(classifierBuilder, times(2)).withClassName(anyString());
        verify(classifierBuilder, times(2)).withOptions(anyString());
        verify(classifierBuilder, times(2)).build();

        verify(evaluationHelper, times(2)).addAppender(any(Classifier.class), anyString());
        verify(evaluationHelper, times(4)).compute(any(Classifier.class), any(TimedEvaluation.class));
        verify(evaluationHelper, times(4)).print(any(DatasetMetadata.class), any(Classifier.class));
        verify(evaluationHelper, times(2)).summarize(any(DatasetMetadata.class), any(Classifier.class));
        verify(evaluationHelper, times(2)).removeAppender(any(Classifier.class), anyString());

        verify(tsneAnalyser, never()).run(any(DatasetMetadata.class), any(Instances.class), anyBoolean());
    }
}
