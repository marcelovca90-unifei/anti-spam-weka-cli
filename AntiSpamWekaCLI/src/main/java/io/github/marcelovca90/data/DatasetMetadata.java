package io.github.marcelovca90.data;

import java.io.File;
import java.util.regex.Pattern;

public class DatasetMetadata
{
    private String folder;
    private String arffFilename;
    private String tsneFilename;
    private String name;
    private String featureSelecion;
    private int numClasses;
    private int numInstances;
    private int numFeaturesBeforeReduction;
    private int numFeaturesAfterReduction;
    private int numEmptyHams;
    private int numEmptySpams;

    public String getFolder()
    {
        return folder;
    }

    public String getArffFilename()
    {
        return arffFilename;
    }

    public String getTsneFilename()
    {
        return tsneFilename;
    }

    public String getName()
    {
        return name;
    }

    public String getFeatureSelecion()
    {
        return featureSelecion;
    }

    public int getNumClasses()
    {
        return numClasses;
    }

    public void setNumClasses(int numClasses)
    {
        this.numClasses = numClasses;
    }

    public int getNumInstances()
    {
        return numInstances;
    }

    public void setNumInstances(int numInstances)
    {
        this.numInstances = numInstances;
    }

    public int getNumFeaturesBeforeReduction()
    {
        return numFeaturesBeforeReduction;
    }

    public int getNumFeaturesAfterReduction()
    {
        return numFeaturesAfterReduction;
    }

    public void setNumFeaturesAfterReduction(int numFeaturesAfterReduction)
    {
        this.numFeaturesAfterReduction = numFeaturesAfterReduction;
    }

    public int getNumEmptyHams()
    {
        return numEmptyHams;
    }

    public int getNumEmptySpams()
    {
        return numEmptySpams;
    }

    public DatasetMetadata(String folder, int emptyHamAmount, int emptySpamAmount)
    {
        this.folder = folder;
        this.arffFilename = folder + File.separator + "data.arff";
        this.tsneFilename = folder + File.separator + "t-SNE.png";
        this.numEmptyHams = emptyHamAmount;
        this.numEmptySpams = emptySpamAmount;

        String[] parts = folder.split(Pattern.quote(File.separator));
        this.name = parts[parts.length - 3];
        this.featureSelecion = parts[parts.length - 2];
        this.numFeaturesBeforeReduction = Integer.parseInt(parts[parts.length - 1]);
        this.numFeaturesAfterReduction = this.numFeaturesBeforeReduction;
    }
}
