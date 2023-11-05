
import datasource.APICensusDatasource;
import datasource.CensusDatasource;
import exception.DatasourceException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit testing for methods in the class that implements CensusDatasource (APICensusDatasource)
 */
public class TestCensusDatasource {

    /**
     * Testing to make sure the state codes produced are correct, and to make sure proper errors are thrown with
     * invalid states.
     * @throws DatasourceException
     * @throws IOException
     */
    @Test
    public void testStateCodes() throws DatasourceException, IOException {
        CensusDatasource apiDatasource = new APICensusDatasource();
        assertEquals("44", apiDatasource.getStateCode("Rhode Island"));
        assertThrows(DatasourceException.class, () -> apiDatasource.getStateCode("United States"));
    }

    /**
     * Testing to make sure county codes generated are correct, and that errors are thrown properly with invalid
     * states or counties.
     * @throws DatasourceException
     * @throws IOException
     */

    @Test
    public void testCountyCodes() throws DatasourceException, IOException {
        CensusDatasource apiDatasource = new APICensusDatasource();
        assertEquals("007", apiDatasource.getCountyCode("Providence", "Rhode Island")); // testing valid county, valid state
        assertThrows(DatasourceException.class, () -> apiDatasource.getCountyCode("Rhode Island", "Rhode Island")); // testing invalid county, valid state
        assertThrows(DatasourceException.class, () -> apiDatasource.getCountyCode("Providence", "Canada")); // testing valid county, invalid state
        assertThrows(DatasourceException.class, () -> apiDatasource.getCountyCode("Providence", "Pennsylvania")); // testing valid county and state but state county is not in
    }

}
