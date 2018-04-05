package io.github.marcelovca90.analysis;

import java.awt.Color;
import java.io.File;

import javax.swing.JFrame;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.math.plot.FrameView;
import org.math.plot.Plot2DPanel;
import org.math.plot.plots.ScatterPlot;

import com.jujutsu.tsne.TSneConfiguration;
import com.jujutsu.tsne.barneshut.BHTSne;
import com.jujutsu.tsne.barneshut.BarnesHutTSne;
import com.jujutsu.tsne.barneshut.ParallelBHTsne;
import com.jujutsu.utils.TSneUtils;

import io.github.marcelovca90.data.ClassType;
import io.github.marcelovca90.data.DatasetMetadata;
import weka.core.Instance;
import weka.core.Instances;

public class TsneAnalyser
{
    private static final Logger LOGGER = LogManager.getLogger(TsneAnalyser.class);

    private double[][] X;
    private ClassType[] labels;
    private int hamCount;
    private int spamCount;

    public void run(DatasetMetadata metadata, Instances dataset, boolean multithread)
    {
        // initialize variables
        X = new double[dataset.numInstances()][];
        labels = new ClassType[dataset.numInstances()];
        hamCount = 0;
        spamCount = 0;

        // initialize hyperparameters
        int initial_dims = dataset.numAttributes();
        double perplexity = 25.0;
        int max_iter = 1000;

        // prepare t-SNE
        prepareInputAndLabels(dataset);
        BarnesHutTSne tsne = multithread ? new ParallelBHTsne() : new BHTSne();
        TSneConfiguration config = TSneUtils.buildConfig(X, 2, initial_dims, perplexity, max_iter);

        // run t-SNE
        double[][] Y = tsne.tsne(config);

        // prepare data for plotting
        double[][] ham = new double[hamCount][];
        String[] hamNames = new String[hamCount];
        double[][] spam = new double[spamCount][];
        String[] spamNames = new String[spamCount];

        for (int i = 0, hamIndex = 0, spamIndex = 0; i < Y.length; i++)
        {
            if (labels[i] == ClassType.HAM)
            {
                ham[hamIndex] = Y[i];
                hamNames[hamIndex] = "ham";
                hamIndex++;
            }
            else
            {
                spam[spamIndex] = Y[i];
                spamNames[spamIndex] = "spam";
                spamIndex++;
            }
        }

        // initialize parent plot panel
        Plot2DPanel plot = new Plot2DPanel();
        plot.addLegend("EAST");

        // prepare ham plot
        Color hamColor = new Color(0, 0, 255, 127);
        ScatterPlot hamPlot = new ScatterPlot("ham", hamColor, ham);
        hamPlot.setTags(hamNames);

        // prepare spam plot
        Color spamColor = new Color(255, 0, 0, 127);
        ScatterPlot spamPlot = new ScatterPlot("spam", spamColor, spam);
        spamPlot.setTags(spamNames);

        // add plots to canvas
        plot.plotCanvas.setNotable(true);
        plot.plotCanvas.setNoteCoords(true);
        plot.plotCanvas.addPlot(hamPlot);
        plot.plotCanvas.addPlot(spamPlot);

        // display plot frame
        FrameView plotframe = new FrameView(plot);
        plotframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        plotframe.setVisible(false);

        // save plot image to filesystem
        File outputFile = null;
        try
        {
            outputFile = new File(metadata.getTsneFilename());
            plot.toGraphicFile(outputFile);
        }
        catch (Exception e)
        {
            LOGGER.error("Could not save graphic file " + outputFile.getAbsolutePath() + ".", e);
        }
    }

    private void prepareInputAndLabels(Instances dataset)
    {
        int endIndexExclusive = dataset.numAttributes() - 1;
        for (int i = 0; i < dataset.size(); i++)
        {
            Instance instance = dataset.get(i);
            X[i] = ArrayUtils.subarray(instance.toDoubleArray(), 0, endIndexExclusive);
            if (Double.compare(instance.classValue(), 0.0) == 0)
            {
                labels[i] = ClassType.HAM;
                hamCount++;
            }
            else
            {
                labels[i] = ClassType.SPAM;
                spamCount++;
            }
        }
    }
}
