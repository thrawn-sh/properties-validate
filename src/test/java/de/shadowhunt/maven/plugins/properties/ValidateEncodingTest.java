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
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ValidateEncodingTest {

    private static final File ISO_PROPERTIES = new File("src/test/resources/encoding/iso-8859-1.properties");

    public static final String PROPERTIES_ENCODING = "ISO-8859-1";

    private static final File UTF8_PROPERTIES = new File("src/test/resources/encoding/utf-8.properties");

    @Test
    public void testISO() throws Exception {
        final ValidateEncoding validation = new ValidateEncoding(PROPERTIES_ENCODING);

        final List<File> result = validation.validate(Collections.singletonList(ISO_PROPERTIES));
        Assert.assertNotNull("result must not be null", result);
        Assert.assertEquals("result size must match", 0, result.size());
    }

    @Test
    public void testUTF() throws Exception {
        final ValidateEncoding validation = new ValidateEncoding(PROPERTIES_ENCODING);

        final List<File> result = validation.validate(Collections.singletonList(UTF8_PROPERTIES));
        Assert.assertNotNull("result must not be null", result);
        Assert.assertEquals("result size must match", 1, result.size());
    }
}
