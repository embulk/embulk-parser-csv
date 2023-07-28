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
import java.io.IOException;
import java.util.Properties;
import org.embulk.EmbulkSystemProperties;
import org.embulk.config.ConfigDiff;
import org.embulk.config.ConfigSource;
import org.embulk.config.DataSource;
import org.embulk.formatter.csv.CsvFormatterPlugin;
import org.embulk.guess.bzip2.Bzip2GuessPlugin;
import org.embulk.guess.csv.CsvGuessPlugin;
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

public class TestCsvGuessPlugin {
    private static final String RESOURCE_NAME_PREFIX = "/org/embulk/standards/guess/csv/test/";

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
            .registerPlugin(GuessPlugin.class, "gzip", GzipGuessPlugin.class)
            .registerPlugin(GuessPlugin.class, "json", JsonGuessPlugin.class)
            .build();

    @Test
    public void testSimple() throws Exception {
        assertGuessByResource(embulk,
                "test_simple_seed.yml", "test_simple.csv",
                "test_simple_guessed.yml");
    }

    @Test
    public void testComplex() throws Exception {
        assertGuessByResource(embulk,
                "test_complex_seed.yml", "test_complex.csv",
                "test_complex_guessed.yml");
    }

    @Test
    public void testMergeTimeFormats() throws Exception {
        assertGuessByResource(embulk,
                "test_merge_time_formats_seed.yml", "test_merge_time_formats.csv",
                "test_merge_time_formats_guessed.yml");
    }

    @Test
    public void testFor1Rows() throws Exception {
        assertGuessByResource(embulk,
                "test_1_rows_seed.yml", "test_1_rows.csv",
                "test_1_rows_guessed.yml");
    }

    @Test
    public void testFor1RowsWithTrimNeeded() throws Exception {
        assertGuessByResource(embulk,
                "test_1_rows_with_trim_needed_seed.yml", "test_1_rows_with_trim_needed.csv",
                "test_1_rows_with_trim_needed_guessed.yml");
    }

    @Test
    public void testFor1RowsAndHeader() throws Exception {
        assertGuessByResource(embulk,
                "test_1_rows_and_header_seed.yml", "test_1_rows_and_header.csv",
                "test_1_rows_and_header_guessed.yml");
    }

    @Test
    public void testFor1RowsAndHeaderWithTrimNeeded() throws Exception {
        assertGuessByResource(embulk,
                "test_1_rows_and_header_with_trim_needed_seed.yml", "test_1_rows_and_header_with_trim_needed.csv",
                "test_1_rows_and_header_with_trim_needed_guessed.yml");
    }

    @Test
    public void testFor2Rows() throws Exception {
        assertGuessByResource(embulk,
                "test_2_rows_seed.yml", "test_2_rows.csv",
                "test_2_rows_guessed.yml");
    }

    @Test
    public void testFor2RowsAndHeader() throws Exception {
        assertGuessByResource(embulk,
                "test_2_rows_and_header_seed.yml", "test_2_rows_and_header.csv",
                "test_2_rows_and_header_guessed.yml");
    }

    @Test
    public void testFor1IntSingleColumnRow() throws Exception {
        assertGuessByResource(embulk,
                "test_1_int_single_column_row_seed.yml", "test_1_int_single_column_row.csv",
                "test_1_int_single_column_row_guessed.yml");
    }

    @Test
    public void testFor1StringSingleColumnRow() throws Exception {
        assertGuessByResource(embulk,
                "test_1_string_single_column_row_seed.yml", "test_1_string_single_column_row.csv",
                "test_1_string_single_column_row_guessed.yml");
    }

    @Test
    public void testFor2StringSingleColumnRows() throws Exception {
        assertGuessByResource(embulk,
                "test_2_string_single_column_rows_seed.yml", "test_2_string_single_column_rows.csv",
                "test_2_string_single_column_rows_guessed.yml");
    }

    @Test
    public void testFor1StringSingleColumnAndHeader() throws Exception {
        assertGuessByResource(embulk,
                "test_1_string_single_column_row_and_header_seed.yml", "test_1_string_single_column_row_and_header.csv",
                "test_1_string_single_column_row_and_header_guessed.yml");
    }

    @Test
    public void testFor2IntSingleColumnRows() throws Exception {
        assertGuessByResource(embulk,
                "test_2_int_single_column_rows_seed.yml", "test_2_int_single_column_rows.csv",
                "test_2_int_single_column_rows_guessed.yml");
    }

    @Test
    public void testFor1IntSingleColumnAndHeader() throws Exception {
        assertGuessByResource(embulk,
                "test_1_int_single_column_row_and_header_seed.yml", "test_1_int_single_column_row_and_header.csv",
                "test_1_int_single_column_row_and_header_guessed.yml");
    }

    @Test
    public void testIntSingleColumnWithHeader() throws Exception {
        assertGuessByResource(embulk,
                "test_int_single_column_with_header_seed.yml", "test_int_single_column_with_header.csv",
                "test_int_single_column_with_header_guessed.yml");
    }

    @Test
    public void testIntSingleColumn() throws Exception {
        assertGuessByResource(embulk,
                "test_int_single_column_seed.yml", "test_int_single_column.csv",
                "test_int_single_column_guessed.yml");
    }

    @Test
    public void testDoubleSingleColumn() throws Exception {
        assertGuessByResource(embulk,
                "test_double_single_column_seed.yml", "test_double_single_column.csv",
                "test_double_single_column_guessed.yml");
    }

    @Test
    public void testStringSingleColumnWithHeader() throws Exception {
        assertGuessByResource(embulk,
                "test_string_single_column_with_header_seed.yml", "test_string_single_column_with_header.csv",
                "test_string_single_column_with_header_guessed.yml");
    }

    @Test
    public void testStringSingleColumn() throws Exception {
        assertGuessByResource(embulk,
                "test_string_single_column_seed.yml", "test_string_single_column.csv",
                "test_string_single_column_guessed.yml");
    }

    @Test
    public void testHeader() throws Exception {
        assertGuessByResource(embulk,
                "test_header_seed.yml", "test_header.csv",
                "test_header_guessed.yml");
    }

    @Test
    public void suggestTabAsDelimiter() throws Exception {
        assertGuessByResource(embulk,
                "test_tab_delimiter_seed.yml", "test_tab_delimiter.csv",
                "test_tab_delimiter_guessed.yml");
    }

    @Test
    public void suggestSemicolonAsDelimiter() throws Exception {
        assertGuessByResource(embulk,
                "test_semicolon_delimiter_seed.yml", "test_semicolon_delimiter.csv",
                "test_semicolon_delimiter_guessed.yml");
    }

    @Test
    public void suggestSingleQuoteAsQuote() throws Exception {
        assertGuessByResource(embulk,
                "test_single_quote_seed.yml", "test_single_quote.csv",
                "test_single_quote_guessed.yml");
    }

    @Test
    public void suggestBackslashAsEscape() throws Exception {
        assertGuessByResource(embulk,
                "test_backslash_escape_seed.yml", "test_backslash_escape.csv",
                "test_backslash_escape_guessed.yml");
    }

    @Test
    public void skipSuggestIfEmptySampleRecords() throws Exception {
        // This test checks that the CSV guess doesn't suggest anything by invalid formatted CSV file.
        assertGuessByResource(embulk,
                "test_skip_suggest_if_empty_sample_records_seed.yml", "test_skip_suggest_if_empty_sample_records.csv",
                "test_skip_suggest_if_empty_sample_records_guessed.yml");
    }

    static void assertGuessByResource(TestingEmbulk embulk, String seedYamlResourceName, String sourceCsvResourceName,
            String resultResourceName) throws IOException {
        ConfigSource seed = embulk.loadYamlResource(RESOURCE_NAME_PREFIX + seedYamlResourceName);

        ConfigDiff guessed =
                embulk.parserBuilder()
                        .parser(seed)
                        .exec(embulk.newConfig().set("exclude_guess_plugins", ImmutableList.of("json")))
                        .inputResource(RESOURCE_NAME_PREFIX + sourceCsvResourceName)
                        .guess();

        assertThat(guessed, is((DataSource) embulk.loadYamlResource(RESOURCE_NAME_PREFIX + resultResourceName)));
    }
}
