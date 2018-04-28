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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class ValidateKeys {

    static class KeysValidationResult {

        private final Set<String> additional;

        private final File file;

        private final Set<String> missing;

        public KeysValidationResult(final File file, final Set<String> additional, final Set<String> missing) {
            this.file = file;
            this.additional = additional;
            this.missing = missing;
        }

        public Set<String> getAdditional() {
            return additional;
        }

        public File getFile() {
            return file;
        }

        public Set<String> getMissing() {
            return missing;
        }
    }

    private static Set<String> getKeys(final File file) throws IOException {
        final Properties properties = new Properties();
        try (InputStream input = new FileInputStream(file)) {
            properties.load(input);
        }

        final Set<String> keys = new TreeSet<>();
        for (final Object key : properties.keySet()) {
            keys.add(key.toString());
        }
        return keys;
    }

    private final Set<String> masterKeys;

    public ValidateKeys(final File master) throws IOException {
        masterKeys = getKeys(master);
    }

    public List<KeysValidationResult> validate(final Collection<File> files) throws IOException {
        final List<KeysValidationResult> result = new ArrayList<>();

        for (final File file : files) {
            final Set<String> keys = getKeys(file);

            final Set<String> missing = new TreeSet<>(masterKeys);
            final Set<String> additional = new TreeSet<>(keys);
            missing.removeAll(keys);
            additional.removeAll(masterKeys);

            if (!(additional.isEmpty() && missing.isEmpty())) {
                result.add(new KeysValidationResult(file, additional, missing));
            }
        }
        return result;
    }
}
