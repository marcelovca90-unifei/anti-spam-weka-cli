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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.arturmkrtchyan.sizeof4j.SizeOf;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.MultiObjectiveEvolutionarySearch;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class DatasetHelper
{
    private static final Logger LOGGER = LogManager.getLogger(DatasetHelper.class);
    private static final int SIZE_INT = SizeOf.intSize();
    private static final int SIZE_DOUBLE = SizeOf.doubleSize();

    public Set<DatasetMetadata> loadMetadata(String filename)
    {
        Set<DatasetMetadata> metadata = new LinkedHashSet<>();
        String systemFilename = FilenameUtils.separatorsToSystem(filename);

        try
        {
            Files.readAllLines(Paths.get(systemFilename)).stream().filter(line -> !line.startsWith("#")).forEach(line ->
            {
                // replaces the user home symbol (~) with the actual folder path
                line = line.replace("~", System.getProperty("user.home"));
                String[] parts = line.split(",");
                String folder = FilenameUtils.separatorsToSystem(parts[0]);
                Integer emptyHamAmount = Integer.parseInt(parts[1]);
                Integer emptySpamAmount = Integer.parseInt(parts[2]);

                // add triple to metadata set
                metadata.add(new DatasetMetadata(folder, emptyHamAmount, emptySpamAmount));
            });
        }
        catch (IOException e)
        {
            LOGGER.error("Could not read file " + filename + ".", e);
        }

        return metadata;
    }

    public Instances loadDataset(DatasetMetadata metadata, boolean lookForArff)
    {
        Instances dataset = null;
        String arffFilename = metadata.getArffFilename();

        if (lookForArff)
        {
            ArffReader arffReader = null;
            try (FileReader fileReader = new FileReader(new File(arffFilename)))
            {
                arffReader = new ArffReader(fileReader);
                dataset = arffReader.getData();
                dataset.setClassIndex(dataset.numAttributes() - 1);
            }
            catch (FileNotFoundException e)
            {
                LOGGER.error("Could not find file " + arffFilename + ".", e);
                return loadDataset(metadata, false);
            }
            catch (IOException e)
            {
                LOGGER.error("Could not read file " + arffFilename + ".", e);
                return loadDataset(metadata, false);
            }
        }
        else
        {
            Instances ham = loadRawDataset(metadata.getFolder() + File.separator + "ham", ClassType.HAM);
            Instances spam = loadRawDataset(metadata.getFolder() + File.separator + "spam", ClassType.SPAM);
            dataset = merge(ham, spam);
        }

        if (dataset != null)
        {
            metadata.setNumClasses(dataset.numClasses());
            metadata.setNumInstances(dataset.numInstances());
        }

        return dataset;
    }

    public void addEmptyInstances(Instances dataset, DatasetMetadata metadata)
    {
        dataset.addAll(createEmptyInstances(metadata.getNumFeaturesAfterReduction(), metadata.getNumEmptyHams(), ClassType.HAM));
        dataset.addAll(createEmptyInstances(metadata.getNumFeaturesAfterReduction(), metadata.getNumEmptySpams(), ClassType.SPAM));
    }

    public void balance(Instances dataset, int seed)
    {
        Random random = new Random(seed);

        int hamCount = (int) dataset.stream().filter(i -> i.classValue() == ClassType.HAM.ordinal()).count();
        int spamCount = (int) dataset.stream().filter(i -> i.classValue() == ClassType.SPAM.ordinal()).count();

        if (hamCount < spamCount)
            for (int i = 0; i < spamCount - hamCount; i++)
                dataset.add(dataset.get(random.nextInt(hamCount)));
        else if (spamCount < hamCount)
            for (int i = 0; i < hamCount - spamCount; i++)
            dataset.add(dataset.get(hamCount + random.nextInt(spamCount)));
    }

    public Instances selectAttributes(Instances dataset)
    {
        int noCores = Runtime.getRuntime().availableProcessors();
        String evaluationOptions = String.format("-Z -P %d -E %d", noCores, noCores);
        String searchOptions = "-generations 10 -population-size 100 -seed 1 -a 0";
        Filter filter = null;
        Instances filteredDataset = null;

        try
        {
            ASEvaluation evaluator = new CfsSubsetEval();
            ((OptionHandler) evaluator).setOptions(Utils.splitOptions(evaluationOptions));

            ASSearch search = new MultiObjectiveEvolutionarySearch();
            ((OptionHandler) search).setOptions(Utils.splitOptions(searchOptions));

            filter = new AttributeSelection();
            filter.setInputFormat(dataset);
            ((AttributeSelection) filter).setEvaluator(evaluator);
            ((AttributeSelection) filter).setSearch(search);

            filteredDataset = Filter.useFilter(dataset, filter);
        }
        catch (Exception e)
        {
            LOGGER.error("Unable to create attribute selection filter.", e);
        }

        return filteredDataset;
    }

    public void shuffle(Instances dataset, int seed)
    {
        Random random = new Random(seed);

        int numberOfInstances = dataset.size();
        for (int i = 0; i < numberOfInstances; i++)
        {
            int j = random.nextInt(numberOfInstances);
            Instance a = dataset.get(i);
            Instance b = dataset.get(j);
            dataset.set(i, b);
            dataset.set(j, a);
        }
    }

    public Pair<Instances, Instances> split(Instances dataset, double splitPercent)
    {
        int numberOfAttributes = dataset.numAttributes();
        int numberOfInstances = dataset.size();
        ArrayList<Attribute> attributes = Collections.list(dataset.enumerateAttributes());
        attributes.add(new Attribute("y", Arrays.asList(ClassType.HAM.name(), ClassType.SPAM.name())));

        Instances trainSet = new Instances("trainSet", attributes, (int) (splitPercent * numberOfInstances));
        for (int i = 0; i < (int) (splitPercent * numberOfInstances); i++)
            trainSet.add(dataset.get(i));
        trainSet.setClassIndex(numberOfAttributes - 1);

        Instances testSet = new Instances("testSet", attributes, (int) ((1 - splitPercent) * numberOfInstances));
        for (int i = (int) (splitPercent * numberOfInstances); i < numberOfInstances; i++)
            testSet.add(dataset.get(i));
        testSet.setClassIndex(numberOfAttributes - 1);

        return Pair.of(trainSet, testSet);
    }

    public void saveToArff(DatasetMetadata metadata, Instances instances)
    {
        String filename = metadata.getArffFilename();
        File outputFile = new File(filename);

        if (!outputFile.exists())
        {
            try
            {
                ArffSaver arffSaver = new ArffSaver();
                arffSaver.setInstances(instances);
                arffSaver.setFile(outputFile);
                arffSaver.writeBatch();
            }
            catch (IOException e)
            {
                LOGGER.error("Unable to save ARFF file " + filename + ".", e);
            }
        }
    }

    public void saveModel(DatasetMetadata metadata, Classifier classifier, int seed)
    {
        String filename = String.format("%s/%s_%s.model", metadata.getFolder(), classifier.getClass().getSimpleName(), seed);
        try
        {
            weka.core.SerializationHelper.write(filename, classifier);
        }
        catch (Exception e)
        {
            LOGGER.error("Unable to save model file " + filename + ".", e);
        }
    }

    private ArrayList<Attribute> createAttributes(long featureAmount)
    {
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (long i = 0; i < featureAmount; i++)
            attributes.add(new Attribute("x" + i));
        attributes.add(new Attribute("y", Arrays.asList(ClassType.HAM.name(), ClassType.SPAM.name())));

        return attributes;
    }

    private Instances createEmptyInstances(int featureAmount, int count, ClassType type)
    {
        Instance emptyInstance;
        ArrayList<Attribute> emptyAttributes = createAttributes(featureAmount);
        Instances emptyDataset = new Instances(type.name(), emptyAttributes, count);
        emptyDataset.setClassIndex(emptyAttributes.size() - 1);

        for (int i = 0; i < count; i++)
        {
            emptyInstance = new DenseInstance(featureAmount + 1);
            emptyInstance.setDataset(emptyDataset);
            for (int j = 0; j < featureAmount; j++)
                emptyInstance.setValue(j, 0.0);
            emptyInstance.setClassValue(type.name());
            emptyDataset.add(emptyInstance);
        }

        return emptyDataset;
    }

    private Instances loadRawDataset(String filename, ClassType classType)
    {
        Instances dataset = null;

        try (InputStream inputStream = new FileInputStream(filename))
        {
            byte[] byteBufferA = new byte[SIZE_INT];
            if (inputStream.read(byteBufferA) != SIZE_INT)
                throw new IOException("Could not read number of instances (not enough bytes were read).");
            int numberOfInstances = ByteBuffer.wrap(byteBufferA).getInt();

            byte[] byteBufferB = new byte[SIZE_INT];
            if (inputStream.read(byteBufferB) != SIZE_INT)
                throw new IOException("Could not read number of attributes (not enough bytes were read).");
            int numberOfAttributes = ByteBuffer.wrap(byteBufferB).getInt();

            // create attributes
            ArrayList<Attribute> attributes = createAttributes(numberOfAttributes);

            // create data set
            dataset = new Instances("dataSet", attributes, numberOfInstances);
            dataset.setClassIndex(numberOfAttributes);

            // create instance placeholder
            Instance instance = new DenseInstance(numberOfAttributes + 1);
            instance.setDataset(dataset);
            instance.setClassValue(classType.ordinal());

            byte[] byteBufferC = new byte[SIZE_DOUBLE];
            DoubleBuffer doubleBuffer = DoubleBuffer.allocate(numberOfAttributes);

            while (inputStream.read(byteBufferC) != -1)
            {
                doubleBuffer.put(ByteBuffer.wrap(byteBufferC).getDouble());
                if (!doubleBuffer.hasRemaining())
                {
                    double[] values = doubleBuffer.array();
                    for (int j = 0; j < numberOfAttributes; j++)
                        instance.setValue(j, values[j]);
                    dataset.add(instance);
                    doubleBuffer.clear();
                }
            }

            inputStream.close();
        }
        catch (FileNotFoundException e)
        {
            LOGGER.error("Could not find file " + filename + ".", e);
        }
        catch (IOException e)
        {
            LOGGER.error("Could not read file " + filename + ".", e);
        }

        return dataset;
    }

    private Instances merge(Instances... datasets)
    {
        Instances mergedSet = null;

        if (Arrays.stream(datasets).allMatch(Objects::nonNull))
        {
            int numberOfAttributes = datasets[0].numAttributes();
            int numberOfInstances = Arrays.stream(datasets).collect(Collectors.summingInt(Instances::size));
            ArrayList<Attribute> attributes = createAttributes(numberOfAttributes - 1);

            mergedSet = new Instances("mergedDataSet", attributes, numberOfInstances);
            mergedSet.setClassIndex(numberOfAttributes - 1);

            Arrays.stream(datasets).forEach(mergedSet::addAll);
        }

        return mergedSet;
    }
}
