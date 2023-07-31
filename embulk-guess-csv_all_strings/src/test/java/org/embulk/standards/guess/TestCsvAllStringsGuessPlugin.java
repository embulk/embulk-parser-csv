/*
 * Copyright 2017 The Embulk project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.embulk.standards.guess;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.Properties;
import org.embulk.EmbulkSystemProperties;
import org.embulk.config.ConfigDiff;
import org.embulk.config.ConfigSource;
import org.embulk.config.DataSource;
import org.embulk.formatter.csv.CsvFormatterPlugin;
import org.embulk.guess.bzip2.Bzip2GuessPlugin;
import org.embulk.guess.csv.CsvGuessPlugin;
import org.embulk.guess.csv_all_strings.CsvAllStringsGuessPlugin;
import org.embulk.guess.gzip.GzipGuessPlugin;
import org.embulk.guess.json.JsonGuessPlugin;
import org.embulk.input.file.LocalFileInputPlugin;
import org.embulk.output.file.LocalFileOutputPlugin;
import org.embulk.parser.csv.CsvParserPlugin;
import org.embulk.spi.FileInputPlugin;
import org.embulk.spi.FileOutputPlugin;
import org.embulk.spi.FormatterPlugin;
import org.embulk.spi.GuessPlugin;
import org.embulk.spi.ParserPlugin;
import org.embulk.test.TestingEmbulk;
import org.junit.Rule;
import org.junit.Test;

public class TestCsvAllStringsGuessPlugin {
    private static final String RESOURCE_NAME_PREFIX = "/org/embulk/standards/guess/csv_all_strings/test/";

    private static final EmbulkSystemProperties EMBULK_SYSTEM_PROPERTIES;

    static {
        final Properties properties = new Properties();
        properties.setProperty("default_guess_plugins", "gzip,bzip2,json,csv");
        EMBULK_SYSTEM_PROPERTIES = EmbulkSystemProperties.of(properties);
    }

    @Rule
    public TestingEmbulk embulk = TestingEmbulk.builder()
            .setEmbulkSystemProperties(EMBULK_SYSTEM_PROPERTIES)
            .registerPlugin(FormatterPlugin.class, "csv", CsvFormatterPlugin.class)
            .registerPlugin(FileInputPlugin.class, "file", LocalFileInputPlugin.class)
            .registerPlugin(FileOutputPlugin.class, "file", LocalFileOutputPlugin.class)
            .registerPlugin(ParserPlugin.class, "csv", CsvParserPlugin.class)
            .registerPlugin(GuessPlugin.class, "bzip2", Bzip2GuessPlugin.class)
            .registerPlugin(GuessPlugin.class, "csv", CsvGuessPlugin.class)
            .registerPlugin(GuessPlugin.class, "csv_all_strings", CsvAllStringsGuessPlugin.class)
            .registerPlugin(GuessPlugin.class, "gzip", GzipGuessPlugin.class)
            .registerPlugin(GuessPlugin.class, "json", JsonGuessPlugin.class)
            .build();

    @Test
    public void testSimple() throws Exception {
        ConfigSource exec = embulk.newConfig()
                .set("guess_plugins", ImmutableList.of("csv_all_strings"))
                .set("exclude_guess_plugins", ImmutableList.of("csv"));

        ConfigDiff guessed =
                embulk.parserBuilder()
                        .exec(exec)
                        .inputResource(RESOURCE_NAME_PREFIX + "test_simple.csv")
                        .guess();

        assertThat(guessed, is((DataSource) embulk.loadYamlResource(RESOURCE_NAME_PREFIX + "test_simple_guessed.yml")));
    }
}
