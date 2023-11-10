# maps-cpbryant-ssdhulip

Collaboration
We collaborated with and took inspiration from dsedarous and felia


Design Choices:
To integrate the backend we imported our project from REPL and to integrate the frontend we took the code from the gearup. We used our backend server to actually run the new functions that we added to search a city with longitude and latitude bounds. These functions are modeled by the MapsHandler and JSONHandler classes. We also created a class in the back end that contains several wrapper classes so that we could map the types in the JSON file to certain properties. Along with that, we have a class that handles parsing data to and from a JSON object. The last class that we added in the backend handles the caching of the search results. In the front end, we have what was covered in the gearup as well as several classes: One that handles the overlay of the redlining data from our json as well as the highlighted values when a user searches for a certain location. We also added a class that handles displaying broadband data when it is searched and an input box to allow for search queries.

How to run the program:
In order to run the code, we must start the backend first, then the frontend code:
1. Open the pom.xml file in IntelliJ and start the server through the main.java_ class in the backend directory. The backend runs on port 2025. To test if the backend is running properly we can type http://localhost:2025/geoJSON?minLat=maxLat=&minLong=&maxLong= in our server and make sure an error isn't returned.
2. In order to run the code through the front end, we must open the project in VSCode and cd into the frontend directory by simply typing cd frontend. After doing this, we call npm run dev in the terminal. With our front end and back end running, we now open the local host provided after running npm run dev in the terminal.
3. To search type in any keyword which will highlight regions of the map related to the keyword
4. To identify areas with low internet access you must search "broadband [state] [county]"


Errors/Bugs:
None so far

Tests:
There are two types of tests that we have in our program. Integration tests and fuzz tests. Our integration tests mostly check our program functionality. We test to make sure that our search and json loading based on longitude and latitude work as well as checking for errors in both of these test classes. We also have a fuzz testing method that generates 100 random combinations of latitude and longitude and spam our server to make sure it can successfully handle all of the different combinations.


How to Run the tests you wrote/were provided:
For backend tests you should type run mvn test in the terminal and For frontend tests, you should type run npm test.

