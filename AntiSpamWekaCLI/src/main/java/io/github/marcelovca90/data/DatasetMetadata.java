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
package io.github.marcelovca90.data;

import java.io.File;
import java.util.regex.Pattern;

public class DatasetMetadata
{
    private String folder;
    private String arffFilename;
    private String tsneFilename;
    private String name;
    private String featureSelection;
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

    public String getFeatureSelection()
    {
        return featureSelection;
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
        this.featureSelection = parts[parts.length - 2];
        this.numFeaturesBeforeReduction = Integer.parseInt(parts[parts.length - 1]);
        this.numFeaturesAfterReduction = this.numFeaturesBeforeReduction;
    }
}
