package io.github.marcelovca90.configuration;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith (MockitoJUnitRunner.class)
public class ConfigurationHelperTest
{
    private final ClassLoader classLoader = getClass().getClassLoader();

    @InjectMocks
    private ConfigurationHelper configurationHelper;

    @Test
    public void load_validProperties_shouldReturnNotNullConfiguration() throws URISyntaxException
    {
        // given
        String filename = Paths.get(classLoader.getResource("properties/valid.properties").toURI()).toFile().getAbsolutePath();

        // when
        Configuration configuration = configurationHelper.load(filename);

        // then
        assertThat(configuration, notNullValue());
    }

    @Test
    public void load_invalidCounts_shouldReturnNullConfiguration() throws URISyntaxException
    {
        // given
        String filename = Paths.get(classLoader.getResource("properties/invalidCounts.properties").toURI()).toFile().getAbsolutePath();

        // when
        Configuration configuration = configurationHelper.load(filename);

        // then
        assertThat(configuration, nullValue());
    }

    @Test
    public void load_invalidClass_shouldReturnNullConfiguration() throws URISyntaxException
    {
        // given
        String filename = Paths.get(classLoader.getResource("properties/invalidClass.properties").toURI()).toFile().getAbsolutePath();

        // when
        Configuration configuration = configurationHelper.load(filename);

        // then
        assertThat(configuration, nullValue());
    }
}
