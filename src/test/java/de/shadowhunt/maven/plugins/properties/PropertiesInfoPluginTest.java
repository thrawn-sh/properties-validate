/**
 * Maven properties Plugin - Validates properties files
 * Copyright © 2017-2018 shadowhunt (dev@shadowhunt.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.shadowhunt.maven.plugins.properties;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PropertiesInfoPluginTest {

    private MavenProject projectMock;

    @Before
    public void setup() throws Exception {
        projectMock = Mockito.mock(MavenProject.class);
        Mockito.when(projectMock.getBasedir()).thenReturn(new File("."));
    }

    @Test(expected = MojoExecutionException.class)
    public void testError() throws Exception {
        final PropertiesInfoPlugin plugin = new PropertiesInfoPlugin();
        plugin.setProject(projectMock);
        plugin.setMessagePropertiesPatterns(Collections.singletonList((String) null));
        plugin.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void testFailEncoding() throws Exception {
        final PropertiesInfoPlugin plugin = new PropertiesInfoPlugin();
        plugin.setProject(projectMock);
        plugin.setEncoding(ValidateEncodingTest.PROPERTIES_ENCODING);
        plugin.setFailBuild(true);
        plugin.setMessagePropertiesPatterns(Collections.singletonList("src/test/resources/encoding/*.properties"));
        plugin.execute();
        Assert.fail("must not complete");
    }

    @Test(expected = MojoFailureException.class)
    public void testFailKeys() throws Exception {
        final PropertiesInfoPlugin plugin = new PropertiesInfoPlugin();
        plugin.setProject(projectMock);
        plugin.setEncoding(ValidateEncodingTest.PROPERTIES_ENCODING);
        plugin.setFailBuild(true);
        plugin.setMaster(ValidateKeysTest.MASTER);
        plugin.setMessagePropertiesPatterns(Collections.singletonList("src/test/resources/keys/*.properties"));
        plugin.execute();
        Assert.fail("must not complete");
    }

    @Test
    public void testNoFailEncoding() throws Exception {
        final PropertiesInfoPlugin plugin = new PropertiesInfoPlugin();
        plugin.setProject(projectMock);
        plugin.setEncoding(ValidateEncodingTest.PROPERTIES_ENCODING);
        plugin.setFailBuild(false);
        plugin.setMessagePropertiesPatterns(Collections.singletonList("src/test/resources/encoding/*.properties"));
        plugin.execute();

    }

    @Test
    public void testNoFailKeys() throws Exception {
        final PropertiesInfoPlugin plugin = new PropertiesInfoPlugin();
        plugin.setProject(projectMock);
        plugin.setEncoding(ValidateEncodingTest.PROPERTIES_ENCODING);
        plugin.setMaster(ValidateKeysTest.MASTER);
        plugin.setFailBuild(false);
        plugin.setMessagePropertiesPatterns(Collections.singletonList("src/test/resources/keys/*.properties"));
        plugin.execute();
    }

    @Test
    public void testPatternsEmpty() throws Exception {
        final PropertiesInfoPlugin plugin = new PropertiesInfoPlugin();
        plugin.setProject(projectMock);
        final List<String> empty = Collections.emptyList();
        plugin.setMessagePropertiesPatterns(empty);
        plugin.execute();
    }

    @Test
    public void testPatternsNone() throws Exception {
        final PropertiesInfoPlugin plugin = new PropertiesInfoPlugin();
        plugin.setProject(projectMock);
        plugin.setMessagePropertiesPatterns(null);
        plugin.execute();
    }
}
