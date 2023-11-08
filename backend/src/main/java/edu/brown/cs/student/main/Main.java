package edu.brown.cs.student.main;

import static spark.Spark.after;

import edu.brown.cs.student.main.Broadband.RealBBSource;
import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Handlers.BroadbandHandler;
import edu.brown.cs.student.main.Handlers.LoadHandler;
import edu.brown.cs.student.main.Handlers.MapsHandler;
import edu.brown.cs.student.main.Handlers.SearchHandler;
import edu.brown.cs.student.main.Handlers.ViewHandler;
import java.io.IOException;
import spark.Spark;

/** The Main class of our project. This is where execution begins. */
public class Main {

  //  Parser p;
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) throws IOException, FactoryFailureException {
    int port = 2025;
    Spark.port(port);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET /order and /mock endpoints
    CSVData data = new CSVData("");

    Spark.get("loadCSV", new LoadHandler(data));
    Spark.get("viewCSV", new ViewHandler(data));
    Spark.get("searchCSV", new SearchHandler(data));
    Spark.get("broadband", new BroadbandHandler(new RealBBSource()));
    Spark.get("areaSearch", new MapsHandler());
    // have to go to endpoint mock for the website to not have 404 error

    Spark.init();
    Spark.awaitInitialization();
    System.out.println("running");

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }

  Main(String[] args) {}
}
