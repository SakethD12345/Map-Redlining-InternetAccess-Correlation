package csv;

import java.util.ArrayList;
import java.util.List;

public class RowCreator implements CreatorFromRow<List<String>> {
    /**
     * The create method creates an array of strings from a list of strings
     * @param row is the given row that is a list of strings
     * @return the row represented as an array of strings
     */

    @Override
    public List<String> create(List<String> row) {
        List<String> finalRow = new ArrayList<>();
        finalRow.addAll(row);
        return finalRow;
    }
}
