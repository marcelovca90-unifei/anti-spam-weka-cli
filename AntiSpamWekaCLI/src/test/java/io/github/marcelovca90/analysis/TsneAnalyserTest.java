package io.github.marcelovca90.analysis;

import static org.mockito.Mockito.when;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.marcelovca90.data.DatasetMetadata;
import weka.core.Instances;
import weka.core.Utils;
import weka.datagenerators.classifiers.classification.RDG1;

@RunWith(MockitoJUnitRunner.class)
public class TsneAnalyserTest
{
    @Mock
    private DatasetMetadata metadata;

    @InjectMocks
    private TsneAnalyser tsneAnalyser;

    @Test
    public void test() throws Exception
    {
        // given
        when(metadata.getFolder()).thenReturn(FilenameUtils.separatorsToSystem("dataset/method/8"));

        // when
        tsneAnalyser.run(metadata, generateRandomDataset());
    }

    private Instances generateRandomDataset() throws Exception
    {
        RDG1 rdg1 = new RDG1();

        rdg1.setOptions(Utils.splitOptions("-r weka.datagenerators.classifiers.classification.RDG1-S_1_-n_100_-a_10_-c_2_-N_0_-I_0_-M_1_-R_10 -S 1 -n 100 -a 10 -c 2 -N 0 -I 0 -M 1 -R 10"));
        rdg1.defineDataFormat();

        return rdg1.generateExamples();
    }
}
