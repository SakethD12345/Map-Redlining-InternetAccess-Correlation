# CSV

## Project Details:
In this project, we created a server application that provides a web API for data retrieval and
search. Our server will itself uses two data sources: CSV files (using our parser from the CSV
sprint) and the United State Census API. Our server has an adapter that resolves incoming requests
by making the appropriate outgoing requests to the Census.

Team Members: Annie Ye (aye15) & Shadi Soufan (ssoufan)<br />
Contributors: Daniel Sedarous (dsedarou) <br />
Estimated time to complete: 30 hours<br />
https://github.com/cs0320-f23/server-annie-ye4-ShadiSoufan44

## Design Choices:
This project contains 10 classes, 2 interfaces, 2 exception classes, and 2 records.
- The Main class serves as the entry point for a Spark web application. It initializes the
  application by setting up a Spark server to listen on port 2023. The class defines route handlers
  for several endpoints by creating an object of LoadHandler, ViewHandler, SearchHandler, and
  BroadbandHandler.
- The LoadHandler class manages CSV data loading requests in a Java web application using Spark.
  It extracts and sets the file path, responding with JSON success or failure messages. Inner records,
  SuccessResponse and FailureResponse, structure and serialize responses. It specializes in handling
  CSV data loading and JSON response generation. It creates a HashMap to store the responses.
- The ViewHandler class is responsible for handling requests to view the contents of a CSV file.
  It takes a CSVData object as a dependency, which presumably contains information about the loaded
  CSV file. The handler checks if a valid file path is available, then loads and parses the CSV data
  using the parseCSV method from the CSVData class. It then constructs a JSON response containing the
  CSV data or an error message in case of failure. It creates a HashMap to store and display the
  responses.
- The SearchHandler class handles HTTP requests for CSV data searches in a web app. It extracts
  parameters, conducts searches by creating a Searcher object, and produces JSON responses. Success
  or failure responses are structured using inner records. This class plays a vital role in managing
  CSV data search operations.
- The BroadbandHandler class is responsible for managing HTTP requests related to broadband data
  in a web application. It implements the Spark Route interface, enabling it to process incoming HTTP
  GET requests. This class interacts with a BroadbandDataSource to retrieve information about
  broadband availability in a specified state and county. It then constructs JSON responses containing
  details such as the state, county, household percentage, and the date and time of access. It
  contains an instance of BroadbandDataSource to separate concerns and improve the organization and
  maintainability of the code.
- The `BroadbandDataSource` interface defines a contract for data sources that provide information
  related to broadband availability. Implementing classes are required to offer methods for retrieving
  broadband data for a specific location, obtaining state and county codes, and fetching household
  percentage information. This interface serves as a blueprint for various data sources that can
  supply broadband-related data and promotes flexibility and consistency in handling such data in a
  broader application context.
- The BroadbandData record class represents broadband-related data, specifically focusing on the
  percentage of households with Wi-Fi access. This record is designed primarily for testing purposes,
  offering a straightforward structure to encapsulate and manipulate broadband data within test
  scenarios.
- The CSVData class serves as a utility for managing and parsing CSV data within a web application.
  It allows users to set and retrieve the file path to a CSV file and provides a method for parsing
  the CSV file's contents into a list of lists of strings, representing the parsed CSV data. To
  accomplish this, it creates an object of the Parser class, which is responsible for reading and
  interpreting the CSV data.
- The DatasourceException class is a custom exception in Java designed to handle errors and
  unexpected conditions specific to data source operations within a software application.
- The FactoryFailureException class is a custom exception designed to capture errors that may occur
  when creating an object from a row of data.
- The Parser class is responsible for parsing CSV data from a given Reader and converting it into a
  list of objects of type T. It utilizes a CreatorFromRow implementation to create objects from each
  CSV row. The class splits CSV rows into individual elements using a regular expression pattern and
  handles any exceptions that may occur during the parsing process.
- The Searcher class is used to search CSV data based on various criteria such as column name,
  column index, or with no headers. It can search for a specific item within the CSV data and return
  the rows that match the search criteria. This class provides constructors and methods for performing
  searches based on the specified criteria and utilizes a Parser to parse the CSV data.
- The StateAndCounty record represents a geographical location with both a state and a county. It
  includes a method, toOurServerParams(), which converts the object into a string format suitable for
  sending as parameters to a server.
- The Creator and CreatorString classes implement CreatorFromRow, allowing developers to dictate
  which type of Object the CSV parser will convert each row into.

## Errors/Bugs:
N/A

## Tests:
Our Testing class handles integration testing and mock testing. It tests the logic of the program
such as:
- ensuring that we get the SuccessResponse and FailureResponse when necessary after calling each 
Handler
- ensuring that the correct data is being returned as a response (state code, county code, household percentage)
- the correct response code is being returned depending on if the query will produce an error
- the SearchHandler is able to search for values with or without headers, with index headers, and
with name headers

## How to:
To run the tests, go into the directory test -> java -> edu.brown.cs.student -> and hit the
run button for the respective tests.<br />
To run the program, go into Main and hit run. Once the link is provided, click on the link. <br />
To load the CSV: http://localhost:2023/loadCSV?filepath=[enter your filepath here] <br />
To view the CSV: http://localhost:2023/viewCSV <br />
To search within the CSV without headers: http://localhost:2023/searchCSV?Value=[insert value]&Headers=No <br />
To search within the CSV with headers being index:http://localhost:2023/searchCSV?Value=[insert value]&Headers=Yes&
NameOrIndex=index&Index=[insert index]<br />
To search within the CSV with headers being name:http://localhost:2023/searchCSV?Value=[insert value]&Headers=Yes&
NameOrIndex=name&Name=[insert name]<br />