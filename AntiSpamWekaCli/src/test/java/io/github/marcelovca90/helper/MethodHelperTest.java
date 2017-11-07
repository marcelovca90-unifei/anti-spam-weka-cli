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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import weka.classifiers.AbstractClassifier;

@RunWith (MockitoJUnitRunner.class)
public class MethodHelperTest
{
    private String className;
    private String options;
    private AbstractClassifier classifier;

    @InjectMocks
    private MethodHelper methodHelper;

    @Test
    public void build_unknownClassValidOptions_shouldReturnNullClassifier()
    {
        // given
        className = "weka.classifiers.functions.SingleLayerPerceptron";
        options = "-L 0.3 -M 0.2 -N 500 -V 33 -S 1 -E 20 -H a";

        // when
        classifier = methodHelper.build(className, options);

        // then
        assertThat(classifier, nullValue());
    }

    @Test
    public void build_invalidClassValidOptions_shouldReturnNullClassifier()
    {
        // given
        className = "weka.classifiers.AbstractClassifier";
        options = "-L 0.3 -M 0.2 -N 500 -V 33 -S 1 -E 20 -H a";

        // when
        classifier = methodHelper.build(className, options);

        // then
        assertThat(classifier, nullValue());
    }

    @Test
    public void build_validClassInvalidOptions_shouldReturnNullClassifier()
    {
        // given
        className = "weka.classifiers.functions.MultilayerPerceptron";
        options = "-L 0.3 -M 0.2 -N 500 -V 33 -S 1 -E 20 -H a -FOO bar";

        // when
        classifier = methodHelper.build(className, options);

        // then
        assertThat(classifier, notNullValue());
        assertThat(classifier.getOptions(), notNullValue());
    }

    @Test
    public void build_validClassValidOptions_shouldReturnNotNullClassifier()
    {
        // given
        className = "weka.classifiers.functions.MultilayerPerceptron";
        options = "-L 0.3 -M 0.2 -N 500 -V 33 -S 1 -E 20 -H a";

        // when
        classifier = methodHelper.build(className, options);

        // then
        assertThat(classifier, notNullValue());
        assertThat(classifier.getOptions(), notNullValue());
    }
}
