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

/**
 * Validate properties files for completness accross languages and valid encoding.<br/>
 * Call <code>mvn properties:verify</code> to validate.
 */
@Mojo(name = "verify", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class PropertiesInfoPlugin extends AbstractMojo {

    @Parameter(required = true, defaultValue = "ISO-8859-1", readonly = true)
    private String encoding;

    @Parameter(required = true, defaultValue = "true", readonly = true)
    private boolean failBuild;

    @Parameter(required = true, defaultValue = "src/main/resources/messages.properties", readonly = true)
    private File master;

    @Parameter(required = true, defaultValue = "src/main/resources/messages*.properties", readonly = true)
    private List<String> messagePropertiesPatterns;

    @Parameter(required = true, defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if ((messagePropertiesPatterns == null) || messagePropertiesPatterns.isEmpty()) {
            // nothing to
            return;
        }

        boolean fail = false;
        try {
            final File basedir = project.getBasedir();

            final Set<File> propertyFiles = findPropertiesForPatterns(basedir, messagePropertiesPatterns);

            final Log log = getLog();

            final ValidateEncoding validateEncoding = new ValidateEncoding(encoding);
            final List<File> encodingResult = validateEncoding.validate(propertyFiles);
            for (final File file : encodingResult) {
                fail = true;
                log.warn("file " + file + " is not encoded in " + encoding);
            }

            if (master != null) {
                final ValidateKeys validateKeys = new ValidateKeys(master);
                final List<ValidateKeys.KeysValidationResult> keysResult = validateKeys.validate(propertyFiles);
                for (final ValidateKeys.KeysValidationResult result : keysResult) {
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
        } catch (final IOException e) {
            throw new MojoExecutionException("can not validate", e);
        }

        if (failBuild && fail) {
            throw new MojoFailureException("invalid properties files found");
        }
    }

    private Set<File> findPropertiesForPatterns(final File basedir, final List<String> patterns) throws IOException {
        final Set<File> propertiesFiles = new TreeSet<>();
        for (final String pattern : patterns) {
            final List<File> files = FileUtils.getFiles(basedir, pattern, null);
            propertiesFiles.addAll(files);
        }
        return propertiesFiles;
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

    public void setMessagePropertiesPatterns(final List<String> messagePropertiesPatterns) {
        this.messagePropertiesPatterns = messagePropertiesPatterns;
    }

    public void setProject(final MavenProject project) {
        this.project = project;
    }
}
