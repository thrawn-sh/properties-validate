/**
 * Maven properties Plugin - Validates properties files
 * Copyright © 2017 - 2017 shadowhunt (dev@shadowhunt.de)
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
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import de.shadowhunt.maven.plugins.properties.ValidateKeys.KeysValidationResult;

/**
 * Validate properties files for completness accross languages and valid encoding.<br/>
 * Call <code>mvn properties:validate</code> to validate.
 */
@Mojo(name = "validate", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true)
public class PropertiesInfoPlugin extends AbstractMojo {

    @Parameter(required = true, defaultValue = "ISO-8859-1", readonly = true)
    private String encoding;

    @Parameter(required = true, defaultValue = "true", readonly = true)
    private boolean failBuild;

    @Parameter(required = true, defaultValue = "src/main/resources/messages.properties", readonly = true)
    private File master;

    @Parameter(required = true, defaultValue = "src/main/resources/messages*.properties", readonly = true)
    private List<String> messagePropertiesPattern;

    @Parameter(required = true, defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final List<String> patterns = getMessagePropertiesPattern();
        if ((patterns == null) || patterns.isEmpty()) {
            // nothing to
            return;
        }

        boolean fail = false;
        try {
            final MavenProject current = getProject();
            final File basedir = current.getBasedir();

            final Set<File> propertyFiles = findPropertiesForPatterns(basedir, patterns);

            final Log log = getLog();

            final ValidateEncoding validateEncoding = new ValidateEncoding(getEncoding());
            final List<File> encodingResult = validateEncoding.validate(propertyFiles);
            for (final File file : encodingResult) {
                fail = true;
                log.warn("file " + file + " is not encoded in " + getEncoding());
            }

            if (getMaster() != null) {
                final ValidateKeys validateKeys = new ValidateKeys(getMaster());
                final List<KeysValidationResult> keysResult = validateKeys.validate(propertyFiles);
                for (final KeysValidationResult result : keysResult) {
                    final File file = result.getFile();

                    final Set<String> additional = result.getAdditional();
                    for (final String key : additional) {
                        fail = true;
                        log.warn("file " + file + " has additional property with key " + key);
                    }

                    final Set<String> missing = result.getMissing();
                    for (final String key : missing) {
                        fail = true;
                        log.warn("file " + file + " is missing property with key " + key);
                    }
                }
            }
        } catch (final Exception e) {
            throw new MojoExecutionException("can not validate", e);
        }

        if (isFailBuild() && fail) {
            throw new MojoFailureException("invalid properties files found");
        }
    }

    private Set<File> findPropertiesForPatterns(final File basedir, final List<String> patterns) throws IOException {
        final Set<File> propertiesFiles = new TreeSet<File>();
        for (final String pattern : patterns) {

            @SuppressWarnings("unchecked")
            final List<File> files = FileUtils.getFiles(basedir, pattern, null);
            propertiesFiles.addAll(files);

        }
        return propertiesFiles;
    }

    public String getEncoding() {
        return encoding;
    }

    public File getMaster() {
        return master;
    }

    public List<String> getMessagePropertiesPattern() {
        return messagePropertiesPattern;
    }

    public MavenProject getProject() {
        return project;
    }

    public boolean isFailBuild() {
        return failBuild;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public void setFailBuild(final boolean failBuild) {
        this.failBuild = failBuild;
    }

    public void setMaster(final File master) {
        this.master = master;
    }

    public void setMessagePropertiesPattern(final List<String> messagePropertiesPattern) {
        this.messagePropertiesPattern = messagePropertiesPattern;
    }

    public void setProject(final MavenProject project) {
        this.project = project;
    }
}
