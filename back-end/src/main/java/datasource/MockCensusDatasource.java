package datasource;

import com.google.common.cache.Cache;

/**
 * Mocking class used for testing
 */
public class MockCensusDatasource implements CensusDatasource {

    public MockCensusDatasource() {

    }

    public String getStateCode(String state) {
        return "44";
    }

    public String getCountyCode(String county, String state) {
        return "007";
    }
    public String[] getBroadband(Cache<String, String[]> cache, String county, String state) {
        return new String[] {"-4", "December"};
    }

    public String[] getBroadband(String county, String state) {
        return new String[] {"900", "Monday"};
    }

}
