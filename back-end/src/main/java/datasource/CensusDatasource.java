package datasource;

import com.google.common.cache.Cache;
import exception.DatasourceException;

import java.io.IOException;

/**
 * This is the interface that defines a CensusDatasource and allows for mocking
 */
public interface CensusDatasource {

    /**
     * This method takes in a state name as a string and returns the state code
     *
     * @param state name
     * @return the state code
     * @throws DatasourceException
     * @throws IOException
     */
    public String getStateCode(String state) throws DatasourceException, IOException;

    /**
     * This method takes in the county name and state name and returns the county code
     * @param county name
     * @param state name
     * @return the county code
     * @throws DatasourceException
     * @throws IOException
     */
    public String getCountyCode(String county, String state) throws DatasourceException, IOException;

    /**
     * This method gets the broadband percentage for a given county given a cache
     * @param cache is the given cache
     * @param county is the specified county name
     * @param state is the state name
     * @return a string array with the broadband percentage and date+time
     * @throws DatasourceException
     * @throws IOException
     */
    public String[] getBroadband(Cache<String, String[]> cache, String county, String state) throws DatasourceException, IOException;

    /**
     * This method gets the broadband percentage without a given cache
     * @param county is the county name
     * @param state is the state name
     * @return the broadband percentage with the date+time in a string array
     * @throws DatasourceException
     * @throws IOException
     */
    public String[] getBroadband(String county, String state) throws DatasourceException, IOException;
}
