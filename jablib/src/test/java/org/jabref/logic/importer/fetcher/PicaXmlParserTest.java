package org.jabref.logic.importer.fetcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jabref.logic.importer.ParseException;
import org.jabref.logic.importer.fileformat.PicaXmlParser;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.support.BibEntryAssert;
import org.jabref.testutils.category.FetcherTest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@FetcherTest
class PicaXmlParserTest {

    private void doTest(String xmlName, int expectedSize, List<String> resourceNames) throws ParseException, IOException {
        try (InputStream is = PicaXmlParserTest.class.getResourceAsStream(xmlName)) {
            PicaXmlParser parser = new PicaXmlParser();
            List<BibEntry> entries = parser.parseEntries(is);
            assertNotNull(entries);
            assertEquals(expectedSize, entries.size());
            int i = 0;
            for (String resourceName : resourceNames) {
                BibEntryAssert.assertEquals(PicaXmlParserTest.class, resourceName, entries.get(i));
                i++;
            }
        }
    }

    @Test
    void emptyResult() throws ParseException, IOException {
        doTest("gvk_empty_result_because_of_bad_query.xml", 0, List.of());
    }

    @Test
    void resultFor797485368() throws ParseException, IOException {
        doTest("gvk_result_for_797485368.xml", 1, List.of("gvk_result_for_797485368.bib"));
    }

    @Test
    void gMP() throws ParseException, IOException {
        doTest("gvk_gmp.xml", 2, Arrays.asList("gvk_gmp.1.bib", "gvk_gmp.2.bib"));
    }

    @Test
    void subTitleTest() throws IOException, ParseException {
        try (InputStream is = PicaXmlParserTest.class.getResourceAsStream("gvk_artificial_subtitle_test.xml")) {
            PicaXmlParser parser = new PicaXmlParser();
            List<BibEntry> entries = parser.parseEntries(is);
            assertNotNull(entries);
            assertEquals(5, entries.size());

            assertEquals(Optional.empty(), entries.getFirst().getField(StandardField.SUBTITLE));
            assertEquals(Optional.of("C"), entries.get(1).getField(StandardField.SUBTITLE));
            assertEquals(Optional.of("Word"), entries.get(2).getField(StandardField.SUBTITLE));
            assertEquals(Optional.of("Word1 word2"), entries.get(3).getField(StandardField.SUBTITLE));
            assertEquals(Optional.of("Word1 word2"), entries.get(4).getField(StandardField.SUBTITLE));
        }
    }
}
