package io.github.marcelovca90.data;

import java.io.File;
import java.util.regex.Pattern;

public class DatasetMetadata
{
    private String folder;
    private String arffFilename;
    private String name;
    private String method;
    private int noFeaturesBefore;
    private int noFeaturesAfter;
    private int emptyHamAmount;
    private int emptySpamAmount;

    public String getFolder()
    {
        return folder;
    }

    public String getArffFilename()
    {
        return arffFilename;
    }

    public String getName()
    {
        return name;
    }

    public String getMethod()
    {
        return method;
    }

    public int getNoFeaturesBefore()
    {
        return noFeaturesBefore;
    }

    public int getNoFeaturesAfter()
    {
        return noFeaturesAfter;
    }

    public void setNoFeaturesAfter(int noFeaturesAfter)
    {
        this.noFeaturesAfter = noFeaturesAfter;
    }

    public int getEmptyHamAmount()
    {
        return emptyHamAmount;
    }

    public int getEmptySpamAmount()
    {
        return emptySpamAmount;
    }

    public DatasetMetadata(String folder, int emptyHamAmount, int emptySpamAmount)
    {
        this.folder = folder;
        this.arffFilename = folder + File.separator + "data.arff";
        this.emptyHamAmount = emptyHamAmount;
        this.emptySpamAmount = emptySpamAmount;

        String[] parts = folder.split(Pattern.quote(File.separator));
        this.name = parts[parts.length - 3];
        this.method = parts[parts.length - 2];
        this.noFeaturesBefore = Integer.parseInt(parts[parts.length - 1]);
        this.noFeaturesAfter = Integer.MIN_VALUE;
    }
}
