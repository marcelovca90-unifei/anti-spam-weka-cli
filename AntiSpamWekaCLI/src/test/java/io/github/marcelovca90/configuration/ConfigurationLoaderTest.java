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
public class ConfigurationLoaderTest
{
    private final ClassLoader classLoader = getClass().getClassLoader();

    @InjectMocks
    private ConfigurationLoader configurationLoader;

    @Test
    public void load_defaultProperties_shouldReturnNotNullConfiguration() throws URISyntaxException
    {
        // when
        Configuration configuration = configurationLoader.load();

        // then
        assertThat(configuration, notNullValue());
    }

    @Test
    public void load_customValidProperties_shouldReturnNotNullConfiguration() throws URISyntaxException
    {
        // given
        String filename = Paths.get(classLoader.getResource("properties/valid.properties").toURI()).toFile().getAbsolutePath();

        // when
        Configuration configuration = configurationLoader.load(filename);

        // then
        assertThat(configuration, notNullValue());
    }

    @Test
    public void load_customInvalidProperties_shouldReturnNullConfiguration() throws URISyntaxException
    {
        // given
        String filename = Paths.get(classLoader.getResource("properties/invalid.properties").toURI()).toFile().getAbsolutePath();

        // when
        Configuration configuration = configurationLoader.load(filename);

        // then
        assertThat(configuration, nullValue());
    }
}
