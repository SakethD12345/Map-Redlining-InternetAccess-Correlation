import csv.CSVParser;
import csv.CSVSearch;
import csv.CreatorFromRow;
import csv.RowCreator;
import exception.ParserException;
import exception.SearchException;
import org.junit.jupiter.api.Test;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A class to test the exceptions thrown by CSVParser and CSVSearch.
 */
public class TestCSVProcessing {

    /**
     * Testing to make sure CSVParser correctly throws exceptions for error handling.
     */
    @Test
    public void testParserExceptions() {
        CreatorFromRow<List<String>> creatorFromRow = new RowCreator();
        assertThrows(FileNotFoundException.class, () -> new CSVParser<>(new FileReader("FakeFile"), Boolean.TRUE, creatorFromRow));
        assertThrows(FileNotFoundException.class, () -> new CSVParser<>(new BufferedReader(Reader.nullReader()), Boolean.TRUE, creatorFromRow));
    }

    /**
     * Testing to make sure CSVSearch correctly throws exceptions for error handling.
     * @throws ParserException
     * @throws FileNotFoundException
     */

    @Test
    public void testSearchException() throws ParserException, FileNotFoundException {
        CSVParser<List<String>> parser = new CSVParser<>(new FileReader("data/data2.csv"), Boolean.TRUE, new RowCreator());
        CSVSearch searcher = new CSVSearch("400", parser.parseCSVFile(), Boolean.TRUE);
        assertThrows(SearchException.class, () -> searcher.search("Location")); // testing nonexistent column name

        CSVParser<List<String>> parser2 = new CSVParser<>(new FileReader("data/data2.csv"), Boolean.FALSE, new RowCreator());
        CSVSearch searcher2 = new CSVSearch("400", parser2.parseCSVFile(), Boolean.FALSE);
        assertThrows(SearchException.class, () -> searcher.search("Location")); // testing exception thrown when column name given without header

        CSVParser<List<String>> parser3 = new CSVParser<>(new FileReader("data/data2.csv"), Boolean.FALSE, new RowCreator());
        CSVSearch searcher3 = new CSVSearch("400", parser3.parseCSVFile(), Boolean.FALSE, List.of("a", "b", "c"));
        assertThrows(SearchException.class, () -> searcher.search("Location")); // testing exception thrown when header is given and shouldn't be
    }
}
