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
package io.github.marcelovca90.classification;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.marcelovca90.data.DatasetMetadata;
import weka.classifiers.Classifier;

@RunWith(MockitoJUnitRunner.class)
public class ClassifierBuilderTest
{
    private String className;
    private String options;
    private Classifier classifier;

    @Mock
    private DatasetMetadata metadata;

    @InjectMocks
    private ClassifierBuilder classifierBuilder;

    @Before
    public void setUp()
    {
        when(metadata.getNumInstances()).thenReturn(100);
        when(metadata.getNumFeaturesBeforeReduction()).thenReturn(16);
        when(metadata.getNumFeaturesAfterReduction()).thenReturn(8);
        when(metadata.getNumClasses()).thenReturn(2);
    }

    @Test
    public void build_unknownClassValidOptions_shouldReturnNullClassifier()
    {
        // given
        className = "weka.classifiers.functions.SingleLayerPerceptron";
        options = "-I 1 -E 1.0 -S 1 -M 10000";

        // when
        classifier = classifierBuilder.withClassName(className).withOptions(options).customize(metadata).build();

        // then
        assertThat(classifier, nullValue());
    }

    @Test
    public void build_invalidClassValidOptions_shouldReturnNullClassifier()
    {
        // given
        className = "weka.classifiers.AbstractClassifier";
        options = "-I 1 -E 1.0 -S 1 -M 10000";

        // when
        classifier = classifierBuilder.withClassName(className).withOptions(options).customize(metadata).build();

        // then
        assertThat(classifier, nullValue());
    }

    @Test
    public void build_validClassInvalidOptions_shouldReturnNullClassifier()
    {
        // given
        className = "weka.classifiers.trees.RandomTree";
        options = "-K 0 -M 1.0 -V 0.001 -S 1 -FOO bar";

        // when
        classifier = classifierBuilder.withClassName(className).withOptions(options).customize(metadata).build();

        // then
        assertThat(classifier, notNullValue());
    }

    @Test
    public void build_validClassValidOptions_shouldReturnNotNullClassifier()
    {
        // given
        String className = "weka.classifiers.trees.RandomTree";
        String options = "-K 0 -M 1.0 -V 0.001 -S 1";

        // when
        classifier = classifierBuilder.withClassName(className).withOptions(options).customize(metadata).build();

        // then
        assertThat(classifier, notNullValue());
    }

    @Test
    public void build_validClassCustomOptions_shouldReturnNotNullClassifier()
    {
        // given
        String[] classNames = new String[] {
                "weka.classifiers.functions.LibLINEAR",
                "weka.classifiers.functions.LibLINEAR",
                "weka.classifiers.functions.LibSVM",
                "weka.classifiers.functions.LibSVM",
                "weka.classifiers.functions.MLPClassifier",
                "weka.classifiers.functions.MLPClassifier",
                "weka.classifiers.functions.MLPClassifier",
                "weka.classifiers.functions.MultilayerPerceptron",
                "weka.classifiers.functions.MultilayerPerceptron",
                "weka.classifiers.functions.RBFNetwork",
                "weka.classifiers.functions.RBFNetwork"
        };
        String[] options = new String[] {
                "-S 1 -C 1.0 -E 0.001 -B 1.0 -L 0.1 -I 1000",
                "-S 1 -C auto -E 0.001 -B 1.0 -L 0.1 -I 1000",
                "-S 0 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 1024.0 -C 1.0 -E 0.001 -P 0.1 -H -seed 1",
                "-S 0 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 1024.0 -C auto -E 0.001 -P 0.1 -H -seed 1",
                "-N 2 -R 0.01 -O 1.0E-6 -P 1 -E 1 -S 1 -L weka.classifiers.functions.loss.SquaredError -A weka.classifiers.functions.activation.ApproximateSigmoid",
                "-N auto -R 0.01 -O 1.0E-6 -P 1 -E 1 -S 1 -L weka.classifiers.functions.loss.SquaredError -A weka.classifiers.functions.activation.ApproximateSigmoid",
                "-N 2 -R 0.01 -O 1.0E-6 -P auto -E auto -S 1 -L weka.classifiers.functions.loss.SquaredError -A weka.classifiers.functions.activation.ApproximateSigmoid",
                "-L 0.3 -M 0.2 -N 500 -V 33 -S 1 -E 20 -H a",
                "-L 0.3 -M 0.2 -N 500 -V 33 -S 1 -E 20 -H auto",
                "-B 2 -S 1 -R 1.0E-8 -M -1 -W 0.1",
                "-B auto -S 1 -R 1.0E-8 -M -1 -W 0.1"
        };

        for (int i = 0; i < classNames.length; i++)
        {
            // when
            classifier = classifierBuilder.withClassName(classNames[i]).withOptions(options[i]).customize(metadata).build();

            // then
            assertThat(classifier, notNullValue());
        }
    }
}
