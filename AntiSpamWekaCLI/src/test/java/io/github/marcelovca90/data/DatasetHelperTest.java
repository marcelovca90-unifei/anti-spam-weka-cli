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
package io.github.marcelovca90.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;

@RunWith(MockitoJUnitRunner.class)
public class DatasetHelperTest
{
    private final ClassLoader classLoader = getClass().getClassLoader();
    private final DatasetMetadata metadata8 = buildMetadata("dataset/method/8", 0, 19);
    private final DatasetMetadata metadata16 = buildMetadata("dataset/method/16", 0, 17);
    private final DatasetMetadata metadata32 = buildMetadata("dataset/method/32", 0, 3);

    @InjectMocks
    private DatasetHelper datasetHelper;

    @Test
    public void loadMetadata_invalidFilename_shouldReturnEmptyMetadata()
    {
        // given
        String filename = "foo_metadata_bar.txt";

        // when
        Set<DatasetMetadata> metadata = datasetHelper.loadMetadata(filename);

        // then
        assertThat(metadata, notNullValue());
        assertThat(metadata.isEmpty(), equalTo(true));
    }

    @Test
    public void loadMetadata_validFilename_shouldReturnNotEmptyMetadata() throws URISyntaxException
    {
        // given
        String filename = Paths.get(classLoader.getResource("metadata.txt").toURI()).toFile().getAbsolutePath();

        // when
        Set<DatasetMetadata> metadata = datasetHelper.loadMetadata(filename);

        // then
        assertThat(metadata, notNullValue());
        assertThat(metadata.size(), equalTo(2));

        Iterator<DatasetMetadata> iterator = metadata.iterator();

        DatasetMetadata next = iterator.next();
        assertThat(next.getFolder(), equalTo(FilenameUtils.separatorsToSystem("dataset/method/8")));
        assertThat(next.getNumEmptyHams(), equalTo(0));
        assertThat(next.getNumEmptySpams(), equalTo(19));

        next = iterator.next();
        assertThat(next.getFolder(), equalTo(FilenameUtils.separatorsToSystem("dataset/method/32")));
        assertThat(next.getNumEmptyHams(), equalTo(0));
        assertThat(next.getNumEmptySpams(), equalTo(3));
    }

    @Test
    public void loadDataset_doSearchArffAndAvailableArff_shouldReturnNotNullInstances() throws Exception
    {
        // when
        Instances dataset = datasetHelper.loadDataset(metadata16, true);

        // then
        assertThat(dataset, notNullValue());
        assertThat(dataset.isEmpty(), equalTo(false));
    }

    @Test
    public void loadDataset_doSearchArffAndUnavailableArff_shouldDelegateAndReturnNotNullInstances() throws URISyntaxException
    {
        // when
        Instances dataset = datasetHelper.loadDataset(metadata32, true);

        // then
        assertThat(dataset, notNullValue());
        assertThat(dataset.isEmpty(), equalTo(false));
    }

    @Test
    public void loadDataset_doSearchArffAndCorruptArff_shouldDelegateAndReturnNotNullInstances() throws URISyntaxException, IOException
    {
        // given
        String folder = Paths.get(classLoader.getResource("dataset/method/16").toURI()).toFile().getAbsolutePath();

        FileUtils.moveFile(new File(folder + File.separator + "data.arff"), new File(folder + File.separator + "data.arff.bkp"));
        FileUtils.moveFile(new File(folder + File.separator + "lipsum.arff"), new File(folder + File.separator + "data.arff"));

        // when
        Instances dataset = datasetHelper.loadDataset(metadata16, true);

        // then
        assertThat(dataset, notNullValue());
        assertThat(dataset.isEmpty(), equalTo(false));

        FileUtils.moveFile(new File(folder + File.separator + "data.arff"), new File(folder + File.separator + "lipsum.arff"));
        FileUtils.moveFile(new File(folder + File.separator + "data.arff.bkp"), new File(folder + File.separator + "data.arff"));
    }

    @Test
    public void loadDataset_doNotSearchArffAndAvailableFile_shouldReturnNotNullInstances() throws URISyntaxException
    {
        // when
        Instances dataset = datasetHelper.loadDataset(metadata8, false);

        // then
        assertThat(dataset, notNullValue());
        assertThat(dataset.isEmpty(), equalTo(false));
    }

    @Test
    public void loadDataset_doNotSearchArffAndUnavailableFile_shouldReturnNullInstances() throws URISyntaxException, IOException
    {
        // given
        String folder = Paths.get(classLoader.getResource("dataset/method/8").toURI()).toFile().getAbsolutePath();

        FileUtils.moveFile(new File(folder + File.separator + "ham"), new File(folder + File.separator + "ham.bkp"));
        FileUtils.moveFile(new File(folder + File.separator + "spam"), new File(folder + File.separator + "spam.bkp"));

        // when
        Instances dataset = datasetHelper.loadDataset(metadata8, false);

        // then
        assertThat(dataset, nullValue());

        FileUtils.moveFile(new File(folder + File.separator + "ham.bkp"), new File(folder + File.separator + "ham"));
        FileUtils.moveFile(new File(folder + File.separator + "spam.bkp"), new File(folder + File.separator + "spam"));
    }

    @Test
    public void loadDataset_doNotSearchArffAndCorruptFile_shouldReturnNullInstances() throws URISyntaxException, IOException
    {
        // given
        String folder = Paths.get(classLoader.getResource("dataset/method/8").toURI()).toFile().getAbsolutePath();

        FileUtils.moveFile(new File(folder + File.separator + "ham"), new File(folder + File.separator + "ham.bkp"));
        FileUtils.copyFile(new File(folder + File.separator + "empty"), new File(folder + File.separator + "ham"));
        FileUtils.moveFile(new File(folder + File.separator + "spam"), new File(folder + File.separator + "spam.bkp"));
        FileUtils.copyFile(new File(folder + File.separator + "empty"), new File(folder + File.separator + "spam"));

        // when
        Instances dataset = datasetHelper.loadDataset(metadata8, false);

        // then
        assertThat(dataset, nullValue());

        FileUtils.forceDelete(new File(folder + File.separator + "ham"));
        FileUtils.moveFile(new File(folder + File.separator + "ham.bkp"), new File(folder + File.separator + "ham"));
        FileUtils.forceDelete(new File(folder + File.separator + "spam"));
        FileUtils.moveFile(new File(folder + File.separator + "spam.bkp"), new File(folder + File.separator + "spam"));
    }

    @Test
    public void addEmptyInstance_shouldReturnDatasetWithNewEmptyInstances() throws Exception
    {
        // given
        Instances dataset = datasetHelper.loadDataset(metadata8, true);
        int hamCountBefore = (int) dataset.stream().filter(i -> i.classValue() == ClassType.HAM.ordinal()).count();
        int spamCountBefore = (int) dataset.stream().filter(i -> i.classValue() == ClassType.SPAM.ordinal()).count();

        // when
        datasetHelper.addEmptyInstances(dataset, metadata8);

        // then
        assertThat(dataset, notNullValue());
        assertThat(dataset.isEmpty(), equalTo(false));
        int hamCountAfter = (int) dataset.stream().filter(i -> i.classValue() == ClassType.HAM.ordinal()).count();
        int spamCountAfter = (int) dataset.stream().filter(i -> i.classValue() == ClassType.SPAM.ordinal()).count();
        assertThat(hamCountAfter, equalTo(hamCountBefore + metadata8.getNumEmptyHams()));
        assertThat(spamCountAfter, equalTo(spamCountBefore + metadata8.getNumEmptySpams()));
        assertThat(dataset.size(), equalTo(hamCountAfter + spamCountAfter));
    }

    @Test
    public void balance_datasetShouldHaveSameAmountsOfEachClass() throws URISyntaxException
    {
        // given
        Instances dataset = datasetHelper.loadDataset(metadata8, false);
        int hamCountBefore = countMessagesByType(dataset, ClassType.HAM);
        int spamCountBefore = countMessagesByType(dataset, ClassType.SPAM);

        // when
        datasetHelper.balance(dataset, 0);

        // then
        assertThat(dataset, notNullValue());
        int hamCountAfter = countMessagesByType(dataset, ClassType.HAM);
        int spamCountAfter = countMessagesByType(dataset, ClassType.SPAM);
        assertThat(hamCountAfter, equalTo(Math.max(hamCountBefore, spamCountBefore)));
        assertThat(spamCountAfter, equalTo(Math.max(hamCountBefore, spamCountBefore)));
    }

    @Test
    public void selectAttributes_withValidDataset_shouldReturnFilteredDataset() throws URISyntaxException
    {
        // given
        Instances dataset = datasetHelper.loadDataset(metadata8, false);

        // when
        Instances filteredDataset = datasetHelper.selectAttributes(dataset);

        // then
        assertThat(filteredDataset, notNullValue());
        assertThat(filteredDataset.numAttributes(), lessThan(dataset.numAttributes()));
    }

    @Test
    public void selectAttributes_withInvalidDataset_shouldReturnNull() throws URISyntaxException
    {
        // when
        Instances filteredDataset = datasetHelper.selectAttributes(null);

        // then
        assertThat(filteredDataset, nullValue());
    }

    @Test
    public void shuffle_firstElementMustHaveChanged() throws URISyntaxException
    {
        // given
        Instances dataset = datasetHelper.loadDataset(metadata8, false);

        // when
        Instance firstBeforeShuffle = dataset.get(0);
        datasetHelper.shuffle(dataset, 0);
        Instance firstAfterShuffle = dataset.get(0);

        // then
        assertThat(firstBeforeShuffle, notNullValue());
        assertThat(dataset, notNullValue());
        assertThat(firstAfterShuffle, notNullValue());
        assertThat(firstBeforeShuffle.hashCode(), not(equalTo(firstAfterShuffle.hashCode())));
    }

    @Test
    public void split_shouldReturnTwoSetsWhoseSumMatchesOriginalSetSize() throws URISyntaxException
    {
        // given
        Instances dataset = datasetHelper.loadDataset(metadata8, false);

        // when
        Pair<Instances, Instances> splitDataset = datasetHelper.split(dataset, 0.5);

        // then
        assertThat(splitDataset, notNullValue());
        assertThat(splitDataset.getLeft(), notNullValue());
        assertThat(splitDataset.getLeft().isEmpty(), equalTo(false));
        assertThat(splitDataset.getRight(), notNullValue());
        assertThat(splitDataset.getRight().isEmpty(), equalTo(false));
        assertThat(splitDataset.getLeft().size() + splitDataset.getRight().size(), equalTo(dataset.size()));
    }

    @Test
    public void saveToArff_null() throws URISyntaxException
    {
        // given
        Instances dataset = datasetHelper.loadDataset(metadata8, false);

        // when
        datasetHelper.saveToArff(metadata8, dataset);

        // then
        assertThat(Files.exists(Paths.get(metadata8.getArffFilename())), equalTo(true));
    }

    @Test
    public void saveToArff_notNull() throws URISyntaxException
    {
        // given
        Instances dataset = datasetHelper.loadDataset(metadata32, false);

        // when
        datasetHelper.saveToArff(metadata32, dataset);

        // then
        assertThat(Files.exists(Paths.get(metadata32.getArffFilename())), equalTo(true));

        // tear down
        FileUtils.deleteQuietly(Paths.get(metadata32.getArffFilename()).toFile());
        assertThat(Files.exists(Paths.get(metadata32.getArffFilename())), equalTo(false));
    }

    @Test
    public void saveModel_invalidClassifier_shouldNotPersistModel() throws URISyntaxException
    {
        // given
        Classifier classifier = mock(Classifier.class);
        int seed = 7919;

        // when
        datasetHelper.saveModel(metadata8, classifier, seed);

        // then
        String filename = metadata8.getFolder() + File.separator + "Classifier_7919.model";
        assertThat(Files.exists(Paths.get(filename)), equalTo(false));
    }

    @Test
    public void saveModel_validClassifier_shouldPersistModel() throws URISyntaxException
    {
        // given
        Classifier classifier = new MultilayerPerceptron();
        int seed = 7919;

        // when
        datasetHelper.saveModel(metadata8, classifier, seed);

        // then
        String filename = metadata8.getFolder() + File.separator + "MultilayerPerceptron_7919.model";
        assertThat(Files.exists(Paths.get(filename)), equalTo(true));
    }

    private int countMessagesByType(Instances dataset, ClassType classType)
    {
        return (int) dataset.stream().filter(i -> i.classValue() == classType.ordinal()).count();
    }

    private DatasetMetadata buildMetadata(String resourcePath, int hamCount, int spamCount)
    {
        String folder;
        DatasetMetadata metadata = null;
        try
        {
            folder = Paths.get(classLoader.getResource(resourcePath).toURI()).toFile().getAbsolutePath();
            metadata = new DatasetMetadata(folder, hamCount, spamCount);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        return metadata;
    }
}
