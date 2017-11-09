package io.github.marcelovca90.execution;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutionHelperTest
{
    private final ClassLoader classLoader = getClass().getClassLoader();

    @InjectMocks
    private ExecutionHelper executionHelper;

    @Test
    public void loadConfiguration_validProperties_shouldReturnSuccess() throws URISyntaxException
    {
        // given
        String filename = Paths.get(classLoader.getResource("properties/valid.properties").toURI()).toFile().getAbsolutePath();

        // when
        executionHelper.loadConfiguration(filename);
    }

    @Test
    public void loadConfiguration_invalidCounts_shouldReturnSuccess() throws URISyntaxException
    {
        // given
        String filename = Paths.get(classLoader.getResource("properties/invalidCounts.properties").toURI()).toFile().getAbsolutePath();

        // when
        executionHelper.loadConfiguration(filename);
    }

    @Test
    public void loadConfiguration_invalidClass_shouldReturnSuccess() throws URISyntaxException
    {
        // given
        String filename = Paths.get(classLoader.getResource("properties/invalidClass.properties").toURI()).toFile().getAbsolutePath();

        // when
        executionHelper.loadConfiguration(filename);
    }
}
