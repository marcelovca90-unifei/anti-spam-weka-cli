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
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import weka.core.Instance;
import weka.core.Instances;

@RunWith (MockitoJUnitRunner.class)
public class DataHelperTest
{
    private final ClassLoader classLoader = getClass().getClassLoader();

    @InjectMocks
    private DataHelper dataHelper;

    @Test
    public void loadMetadata_invalidFilename_shouldReturnEmptyMetadata()
    {
        // given
        String filename = "foo_metadata_bar.txt";

        // when
        Set<Triple<String, Integer, Integer>> metadata = dataHelper.loadMetadata(filename);

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
        Set<Triple<String, Integer, Integer>> metadata = dataHelper.loadMetadata(filename);

        // then
        assertThat(metadata, notNullValue());
        assertThat(metadata.size(), equalTo(2));

        Iterator<Triple<String, Integer, Integer>> iterator = metadata.iterator();

        Triple<String, Integer, Integer> next = iterator.next();
        assertThat(next.getLeft(), equalTo(FilenameUtils.separatorsToSystem("data/8")));
        assertThat(next.getMiddle(), equalTo(0));
        assertThat(next.getRight(), equalTo(19));

        next = iterator.next();
        assertThat(next.getLeft(), equalTo(FilenameUtils.separatorsToSystem("data/32")));
        assertThat(next.getMiddle(), equalTo(0));
        assertThat(next.getRight(), equalTo(3));
    }

    @Test
    public void loadDataset_doSearchArffAndAvailableArff_shouldReturnNotNullInstances() throws Exception
    {
        // given
        String folder = Paths.get(classLoader.getResource("data/16").toURI()).toFile().getAbsolutePath();
        Triple<String, Integer, Integer> metadatum = Triple.of(folder, 0, 17);

        // when
        Instances dataset = dataHelper.loadDataset(metadatum, true);

        // then
        assertThat(dataset, notNullValue());
        assertThat(dataset.isEmpty(), equalTo(false));
    }

    @Test
    public void loadDataset_doSearchArffAndUnavailableArff_shouldDelegateAndReturnNotNullInstances() throws URISyntaxException
    {
        // given
        String folder = Paths.get(classLoader.getResource("data/32").toURI()).toFile().getAbsolutePath();
        Triple<String, Integer, Integer> metadatum = Triple.of(folder, 0, 3);

        // when
        Instances dataset = dataHelper.loadDataset(metadatum, true);

        // then
        assertThat(dataset, notNullValue());
        assertThat(dataset.isEmpty(), equalTo(false));
    }

    @Test
    public void loadDataset_doSearchArffAndCorruptArff_shouldDelegateAndReturnNotNullInstances() throws URISyntaxException, IOException
    {
        // given
        String folder = Paths.get(classLoader.getResource("data/16").toURI()).toFile().getAbsolutePath();
        Triple<String, Integer, Integer> metadatum = Triple.of(folder, 0, 17);

        FileUtils.moveFile(new File(folder + File.separator + "data.arff"), new File(folder + File.separator + "data.arff.bkp"));
        FileUtils.moveFile(new File(folder + File.separator + "lipsum.arff"), new File(folder + File.separator + "data.arff"));

        // when
        Instances dataset = dataHelper.loadDataset(metadatum, true);

        // then
        assertThat(dataset, notNullValue());
        assertThat(dataset.isEmpty(), equalTo(false));

        FileUtils.moveFile(new File(folder + File.separator + "data.arff"), new File(folder + File.separator + "lipsum.arff"));
        FileUtils.moveFile(new File(folder + File.separator + "data.arff.bkp"), new File(folder + File.separator + "data.arff"));
    }

    @Test
    public void loadDataset_doNotSearchArffAndAvailableFile_shouldReturnNotNullInstances() throws URISyntaxException
    {
        // given
        String folder = Paths.get(classLoader.getResource("data/8").toURI()).toFile().getAbsolutePath();
        Triple<String, Integer, Integer> metadatum = Triple.of(folder, 0, 19);

        // when
        Instances dataset = dataHelper.loadDataset(metadatum, false);

        // then
        assertThat(dataset, notNullValue());
        assertThat(dataset.isEmpty(), equalTo(false));
    }

    @Test
    public void loadDataset_doNotSearchArffAndUnavailableFile_shouldReturnNullInstances() throws URISyntaxException, IOException
    {
        // given
        String folder = Paths.get(classLoader.getResource("data/8").toURI()).toFile().getAbsolutePath();
        Triple<String, Integer, Integer> metadatum = Triple.of(folder, 0, 19);

        FileUtils.moveFile(new File(folder + File.separator + "ham"), new File(folder + File.separator + "ham.bkp"));
        FileUtils.moveFile(new File(folder + File.separator + "spam"), new File(folder + File.separator + "spam.bkp"));

        // when
        Instances dataset = dataHelper.loadDataset(metadatum, false);

        // then
        assertThat(dataset, nullValue());

        FileUtils.moveFile(new File(folder + File.separator + "ham.bkp"), new File(folder + File.separator + "ham"));
        FileUtils.moveFile(new File(folder + File.separator + "spam.bkp"), new File(folder + File.separator + "spam"));
    }

    @Test
    public void loadDataset_doNotSearchArffAndCorruptFile_shouldReturnNullInstances() throws URISyntaxException, IOException
    {
        // given
        String folder = Paths.get(classLoader.getResource("data/8").toURI()).toFile().getAbsolutePath();
        Triple<String, Integer, Integer> metadatum = Triple.of(folder, 0, 19);

        FileUtils.moveFile(new File(folder + File.separator + "ham"), new File(folder + File.separator + "ham.bkp"));
        FileUtils.copyFile(new File(folder + File.separator + "empty"), new File(folder + File.separator + "ham"));
        FileUtils.moveFile(new File(folder + File.separator + "spam"), new File(folder + File.separator + "spam.bkp"));
        FileUtils.copyFile(new File(folder + File.separator + "empty"), new File(folder + File.separator + "spam"));

        // when
        Instances dataset = dataHelper.loadDataset(metadatum, false);

        // then
        assertThat(dataset, nullValue());

        FileUtils.forceDelete(new File(folder + File.separator + "ham"));
        FileUtils.moveFile(new File(folder + File.separator + "ham.bkp"), new File(folder + File.separator + "ham"));
        FileUtils.forceDelete(new File(folder + File.separator + "spam"));
        FileUtils.moveFile(new File(folder + File.separator + "spam.bkp"), new File(folder + File.separator + "spam"));
    }

    @Test
    public void balance_datasetShouldHaveSameAmountsOfEachClass() throws URISyntaxException
    {
        // given
        String folder = Paths.get(classLoader.getResource("data/8").toURI()).toFile().getAbsolutePath();
        Triple<String, Integer, Integer> metadatum = Triple.of(folder, 0, 19);
        Instances dataset = dataHelper.loadDataset(metadatum, false);
        int hamCountBefore = count(dataset, ClassType.HAM);
        int spamCountBefore = count(dataset, ClassType.SPAM);

        // when
        dataHelper.balance(dataset, 0);

        // then
        assertThat(dataset, notNullValue());
        int hamCountAfter = count(dataset, ClassType.HAM);
        int spamCountAfter = count(dataset, ClassType.SPAM);
        assertThat(hamCountAfter, equalTo(Math.max(hamCountBefore, spamCountBefore)));
        assertThat(spamCountAfter, equalTo(Math.max(hamCountBefore, spamCountBefore)));
    }

    private int count(Instances dataset, ClassType classType)
    {
        return (int) dataset.stream().filter(i -> i.classValue() == classType.ordinal()).count();
    }

    @Test
    public void shuffle_firstElementMustHaveChanged() throws URISyntaxException
    {
        // given
        String folder = Paths.get(classLoader.getResource("data/8").toURI()).toFile().getAbsolutePath();
        Triple<String, Integer, Integer> metadatum = Triple.of(folder, 0, 19);
        Instances dataset = dataHelper.loadDataset(metadatum, false);

        // when
        Instance firstBeforeShuffle = dataset.get(0);
        dataHelper.shuffle(dataset, 0);
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
        String folder = Paths.get(classLoader.getResource("data/8").toURI()).toFile().getAbsolutePath();
        Triple<String, Integer, Integer> metadatum = Triple.of(folder, 0, 19);
        Instances dataset = dataHelper.loadDataset(metadatum, false);

        // when
        Pair<Instances, Instances> splitDataset = dataHelper.split(dataset, 0.5);

        // then
        assertThat(splitDataset, notNullValue());
        assertThat(splitDataset.getLeft(), notNullValue());
        assertThat(splitDataset.getLeft().isEmpty(), equalTo(false));
        assertThat(splitDataset.getRight(), notNullValue());
        assertThat(splitDataset.getRight().isEmpty(), equalTo(false));
        assertThat(splitDataset.getLeft().size() + splitDataset.getRight().size(), equalTo(dataset.size()));
    }
}
