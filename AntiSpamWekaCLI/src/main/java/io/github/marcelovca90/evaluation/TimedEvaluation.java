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
package io.github.marcelovca90.evaluation;

import weka.classifiers.Evaluation;
import weka.core.Instances;

public class TimedEvaluation extends Evaluation
{
    private static final long serialVersionUID = 9104672475069946341L;

    private long trainStart;
    private long trainEnd;
    private long testStart;
    private long testEnd;

    public TimedEvaluation(Instances data) throws Exception
    {
        super(data);
    }

    public TimedEvaluation(Instances data, long trainStart, long trainEnd, long testStart, long testEnd) throws Exception
    {
        super(data);
        this.trainStart = trainStart;
        this.trainEnd = trainEnd;
        this.testStart = testStart;
        this.testEnd = testEnd;
    }

    public void setTrainStart(long trainStart)
    {
        this.trainStart = trainStart;
    }

    public void setTrainEnd(long trainEnd)
    {
        this.trainEnd = trainEnd;
    }

    public void setTestStart(long testStart)
    {
        this.testStart = testStart;
    }

    public void setTestEnd(long testEnd)
    {
        this.testEnd = testEnd;
    }

    public long trainingTime()
    {
        return Math.abs(trainEnd - trainStart);
    }

    public long testingTime()
    {
        return Math.abs(testEnd - testStart);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (int) (testEnd ^ (testEnd >>> 32));
        result = prime * result + (int) (testStart ^ (testStart >>> 32));
        result = prime * result + (int) (trainEnd ^ (trainEnd >>> 32));
        result = prime * result + (int) (trainStart ^ (trainStart >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        TimedEvaluation other = (TimedEvaluation) obj;
        if (testEnd != other.testEnd) return false;
        if (testStart != other.testStart) return false;
        if (trainEnd != other.trainEnd) return false;
        if (trainStart != other.trainStart) return false;
        return super.equals(obj);
    }
}
