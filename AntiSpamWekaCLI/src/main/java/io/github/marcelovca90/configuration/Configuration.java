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
package io.github.marcelovca90.configuration;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class Configuration
{
    // anti spam settings
    private String metadataPath;
    private List<Pair<String, String>> classNamesAndOptions;
    private int runs;

    // run settings
    private boolean tsneAnalysis;
    private boolean loadArff;
    private boolean shrinkFeatures;
    private boolean balanceClasses;
    private boolean includeEmpty;
    private boolean saveModel;
    private boolean saveArff;

    public String getMetadataPath()
    {
        return metadataPath;
    }

    public void setMetadataPath(String metadataPath)
    {
        this.metadataPath = metadataPath;
    }

    public List<Pair<String, String>> getClassNamesAndOptions()
    {
        return classNamesAndOptions;
    }

    public void setClassNamesAndOptions(List<Pair<String, String>> classNamesAndOptions)
    {
        this.classNamesAndOptions = classNamesAndOptions;
    }

    public int getRuns()
    {
        return runs;
    }

    public void setRuns(int runs)
    {
        this.runs = runs;
    }

    public boolean isTsneAnalysis()
    {
        return tsneAnalysis;
    }

    public void setTsneAnalysis(boolean tsneAnalysis)
    {
        this.tsneAnalysis = tsneAnalysis;
    }

    public boolean shouldLoadArff()
    {
        return loadArff;
    }

    public void setLoadArff(boolean loadArff)
    {
        this.loadArff = loadArff;
    }

    public boolean shouldShrinkFeatures()
    {
        return shrinkFeatures;
    }

    public void setShrinkFeatures(boolean shrinkFeatures)
    {
        this.shrinkFeatures = shrinkFeatures;
    }

    public boolean shouldBalanceClasses()
    {
        return balanceClasses;
    }

    public void setBalanceClasses(boolean balanceClasses)
    {
        this.balanceClasses = balanceClasses;
    }

    public boolean shouldIncludeEmpty()
    {
        return includeEmpty;
    }

    public void setIncludeEmpty(boolean includeEmpty)
    {
        this.includeEmpty = includeEmpty;
    }

    public boolean shouldSaveModel()
    {
        return saveModel;
    }

    public void setSaveModel(boolean saveModel)
    {
        this.saveModel = saveModel;
    }

    public boolean shouldSaveArff()
    {
        return saveArff;
    }

    public void setSaveArff(boolean saveArff)
    {
        this.saveArff = saveArff;
    }
}
