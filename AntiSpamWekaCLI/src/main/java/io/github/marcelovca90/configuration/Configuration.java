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

import io.github.marcelovca90.mail.CryptoProtocol;

public class Configuration
{
    // anti spam settings
    private String metadataPath;
    private List<Pair<String, String>> classNamesAndOptions;
    private int runs;

    // run settings
    private boolean skipTrain;
    private boolean skipTest;
    private boolean shrinkFeatures;
    private boolean balanceClasses;
    private boolean includeEmpty;
    private boolean removeOutliers;
    private boolean saveArff;
    private boolean saveModel;
    private boolean saveSets;
    private boolean mailResults;

    // e-mail settings
    private String sender;
    private String recipients;
    private String host;
    private CryptoProtocol protocol;
    private String username;
    private String password;

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

    public boolean isSkipTrain()
    {
        return skipTrain;
    }

    public void setSkipTrain(boolean skipTrain)
    {
        this.skipTrain = skipTrain;
    }

    public boolean isSkipTest()
    {
        return skipTest;
    }

    public void setSkipTest(boolean skipTest)
    {
        this.skipTest = skipTest;
    }

    public boolean isShrinkFeatures()
    {
        return shrinkFeatures;
    }

    public void setShrinkFeatures(boolean shrinkFeatures)
    {
        this.shrinkFeatures = shrinkFeatures;
    }

    public boolean isBalanceClasses()
    {
        return balanceClasses;
    }

    public void setBalanceClasses(boolean balanceClasses)
    {
        this.balanceClasses = balanceClasses;
    }

    public boolean isIncludeEmpty()
    {
        return includeEmpty;
    }

    public void setIncludeEmpty(boolean includeEmpty)
    {
        this.includeEmpty = includeEmpty;
    }

    public boolean isRemoveOutliers()
    {
        return removeOutliers;
    }

    public void setRemoveOutliers(boolean removeOutliers)
    {
        this.removeOutliers = removeOutliers;
    }

    public boolean isSaveArff()
    {
        return saveArff;
    }

    public void setSaveArff(boolean saveArff)
    {
        this.saveArff = saveArff;
    }

    public boolean isSaveModel()
    {
        return saveModel;
    }

    public void setSaveModel(boolean saveModel)
    {
        this.saveModel = saveModel;
    }

    public boolean isSaveSets()
    {
        return saveSets;
    }

    public void setSaveSets(boolean saveSets)
    {
        this.saveSets = saveSets;
    }

    public boolean isMailResults()
    {
        return mailResults;
    }

    public void setMailResults(boolean mailResults)
    {
        this.mailResults = mailResults;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public String getRecipients()
    {
        return recipients;
    }

    public void setRecipients(String recipients)
    {
        this.recipients = recipients;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public CryptoProtocol getProtocol()
    {
        return protocol;
    }

    public void setProtocol(CryptoProtocol protocol)
    {
        this.protocol = protocol;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
