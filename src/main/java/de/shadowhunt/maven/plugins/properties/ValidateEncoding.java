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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class ValidateEncoding {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final CharsetEncoder encoder;

    public ValidateEncoding(final String encoding) {
        final Charset charset = Charset.forName(encoding);
        encoder = charset.newEncoder();
    }

    public List<File> validate(final Collection<File> files) throws IOException {
        final List<File> result = new ArrayList<>();

        for (final File file : files) {
            try (InputStream input = new FileInputStream(file)) {
                final String content = IOUtils.toString(input, UTF8);
                if (!encoder.canEncode(content)) {
                    result.add(file);
                }
            }
        }

        return result;
    }
}
