//**********************************************************************
// Copyright (c) 2017 Telefonaktiebolaget LM Ericsson, Sweden.
// All rights reserved.
// The Copyright to the computer program(s) herein is the property of
// Telefonaktiebolaget LM Ericsson, Sweden.
// The program(s) may be used and/or copied with the written permission
// from Telefonaktiebolaget LM Ericsson or in accordance with the terms
// and conditions stipulated in the agreement/contract under which the
// program(s) have been supplied.
// **********************************************************************
package io.github.marcelovca90.helper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

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
public class DatasetHelperTest
{
    private final ClassLoader classLoader = getClass().getClassLoader();

    @InjectMocks
    private DatasetHelper datasetHelper;

    @Test
    public void loadMetadata_shouldReturnNotNullMetadata() throws IOException
    {
        // given
        String filename = classLoader.getResource("metadata.txt").getFile().substring(1);
        String systemFilename = FilenameUtils.separatorsToSystem(filename);

        // when
        Set<Triple<String, Integer, Integer>> metadata = datasetHelper.loadMetadata(systemFilename);

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
    public void load_doNotSearchArffAndAvailableArff_shouldReturnNotNullInstances() throws Exception
    {
        // given
        String folder = classLoader.getResource("data/8").getFile().substring(1);
        String systemFolder = FilenameUtils.separatorsToSystem(folder);
        Triple<String, Integer, Integer> metadatum = Triple.of(systemFolder, 0, 19);

        // when
        Instances dataset = datasetHelper.load(metadatum, false);

        // then
        assertThat(dataset, notNullValue());
        assertThat(dataset.isEmpty(), equalTo(false));
    }

    @Test
    public void load_doSearchArffAndAvailableArff_shouldReturnNotNullInstances() throws Exception
    {
        // given
        String folder = classLoader.getResource("data/16").getFile().substring(1);
        String systemFolder = FilenameUtils.separatorsToSystem(folder);
        Triple<String, Integer, Integer> metadatum = Triple.of(systemFolder, 0, 17);

        // when
        Instances dataset = datasetHelper.load(metadatum, true);

        // then
        assertThat(dataset, notNullValue());
        assertThat(dataset.isEmpty(), equalTo(false));
    }

    @Test
    public void load_doSearchArffAndUnavailableArff_shouldReturnNotNullInstances() throws Exception
    {
        // given
        String folder = classLoader.getResource("data/32").getFile().substring(1);
        String systemFolder = FilenameUtils.separatorsToSystem(folder);
        Triple<String, Integer, Integer> metadatum = Triple.of(systemFolder, 0, 3);

        // when
        Instances dataset = datasetHelper.load(metadatum, true);

        // then
        assertThat(dataset, notNullValue());
        assertThat(dataset.isEmpty(), equalTo(false));
    }

    @Test
    public void shuffle_firstElementMustHaveChanged() throws Exception
    {
        // given
        String folder = classLoader.getResource("data/8").getFile().substring(1);
        String systemFolder = FilenameUtils.separatorsToSystem(folder);
        Triple<String, Integer, Integer> metadatum = Triple.of(systemFolder, 0, 19);
        Instances dataset = datasetHelper.load(metadatum, false);

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
    public void split_shouldReturnTwoSetsWhoseSumMatchesOriginalSetSize() throws Exception
    {
        // given
        String folder = classLoader.getResource("data/8").getFile().substring(1);
        String systemFolder = FilenameUtils.separatorsToSystem(folder);
        Triple<String, Integer, Integer> metadatum = Triple.of(systemFolder, 0, 19);
        Instances dataset = datasetHelper.load(metadatum, false);

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
}
