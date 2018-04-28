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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.shadowhunt.maven.plugins.properties.ValidateKeys.KeysValidationResult;

public class ValidateKeysTest {

    public static final File _BC = new File("src/test/resources/keys/_bc.properties");

    public static final File A_C_E = new File("src/test/resources/keys/a_c_e.properties");

    public static final File ABCD = new File("src/test/resources/keys/abcd.properties");

    public static final File CBA = new File("src/test/resources/keys/cba.properties");

    public static final File MASTER = new File("src/test/resources/keys/abc.properties");

    @Test
    public void testAdditional() throws Exception {
        final ValidateKeys validation = new ValidateKeys(MASTER);

        final List<KeysValidationResult> result = validation.validate(Collections.singletonList(ABCD));
        Assert.assertNotNull("result must not be null", result);
        Assert.assertEquals("result size must match", 1, result.size());

        final KeysValidationResult entry = result.get(0);
        Assert.assertNotNull("entry must not be null", entry);
        Assert.assertEquals("additional must match", 1, entry.getAdditional().size());
        Assert.assertEquals("misssing must match", 0, entry.getMissing().size());
        Assert.assertEquals("file must match", ABCD, entry.getFile());
    }

    @Test
    public void testAdditionalAndMissing() throws Exception {
        final ValidateKeys validation = new ValidateKeys(MASTER);

        final List<KeysValidationResult> result = validation.validate(Collections.singletonList(A_C_E));
        Assert.assertNotNull("result must not be null", result);
        Assert.assertEquals("result size must match", 1, result.size());

        final KeysValidationResult entry = result.get(0);
        Assert.assertNotNull("entry must not be null", entry);
        Assert.assertEquals("additional must match", 1, entry.getAdditional().size());
        Assert.assertEquals("misssing must match", 1, entry.getMissing().size());
        Assert.assertEquals("file must match", A_C_E, entry.getFile());
    }

    @Test
    public void testMatching() throws Exception {
        final ValidateKeys validation = new ValidateKeys(MASTER);

        final List<KeysValidationResult> result = validation.validate(Arrays.asList(MASTER, CBA));
        Assert.assertNotNull("result must not be null", result);
        Assert.assertEquals("result size must match", 0, result.size());
    }

    @Test
    public void testMissing() throws Exception {
        final ValidateKeys validation = new ValidateKeys(MASTER);

        final List<KeysValidationResult> result = validation.validate(Collections.singletonList(_BC));
        Assert.assertNotNull("result must not be null", result);
        Assert.assertEquals("result size must match", 1, result.size());

        final KeysValidationResult entry = result.get(0);
        Assert.assertNotNull("entry must not be null", entry);
        Assert.assertEquals("additional must match", 0, entry.getAdditional().size());
        Assert.assertEquals("misssing must match", 1, entry.getMissing().size());
        Assert.assertEquals("file must match", _BC, entry.getFile());
    }
}
