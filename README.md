> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up
> with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this
> base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for
> Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your
> Sprint
> 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set
> to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer
> resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details

Project Name: Server
Team Members: jestra10 and ljgeer
Total Time: 15 hours
Link: https://github.com/cs0320-s24/server-louis-jason

# Design Choices

The main class of this is our Server class in our server package. In this class we set up our datawrapper class
and our cached searcher. This is done so that the datawrapper (which wraps an instance of our CSV parser) can
be passed into the different handlers. This allows for one handler to make a change to the CSV parser and have
it be updated across all the handlers. Our cached searcher is also set up so it can be passed into its respective
handler and a new one is therefore not created everytime the handler is invoked.

From there, we have three handlers
that have to do with CSV files. Each is self-explanatory from its name. They're invoked whenever someone types
in their path into the url. All handlers provide helpful messages to the user calling them to help debug on their
end if it is not working. One thing to note about the searhcsv handler is that it takes in 4 arguments from the
URL. It takes in value(the one you are searching for), a booleanHeader(true or false if there are headers for the csv),
a booleanHeaderAnInt(true or false if the headers are an integer), and an identifier(an integer for a specific column or
name of a header for a specific column.) Once the handlers have done their action, it creates a response and serializes
the response into a JSON to be returned to the end user.

We have another handler for broadband. In this handler, an instance of CachedSearcher and BroadbandSearch are
implemented. They both implement the SearchInterface. This requires the classes to have a search method be implemented.
This is done to implement caching when searching for broadband information. CachedSearcher is actually a wrapper class
of BroadbandSearch. This is why it is important that they both implement the interface so that it can be easily passed
in, or if needed by another developer, they can pass in their own class that implements the interface. Since 
CachedSearcher wraps BroadbandSearch in our case, whenever we want to search for broadband data, we call the
CachedSearcher to find the data. If the data is not in the cache previously, then it will call the search method
in BroadbandSearch to actually retrieve the data from the census API. This data is then stored in the cache. 
We have designed the class so that a developer or another using this class can control how much info is stored
in the cache and for how long. They can do this by passing in arguments whenever they initialize an instance of the
class. So, essentially our handler and these two other classes, act as a proxy API for the census API. We get the
data and only return the data essential for the end user. We also return the arguments they passed in and the date
they tried to access the information for easier debugging on their end if needed.

We also have two classes used for converting JSON files into specific types (BroadbandInfo and CSVFile.) This 
makes our code more understandable and makes it easier to convert a JSON file into an object that we can use
easily throughout the code.

The Parser, Searcher, and Creator classes are all the same from the CSV project and operate the same.

# Errors/Bugs
No bugs in our code. We have the error that everyone has when starting up a server, but this does not impede it
from operating normally. 

# Tests
We have three testing classes. One that tests all the functionality of our Searcher and Parser classes. A testing
file that tests all of our handlers and that they can be called on and actually operate and return data. And a
testing file that tests are caching and the source. We tested for all sorts of cases (calling load multiple
times, malformed data, etc.)

# How to
To run our server, type "mvn package" and then to run it use "./run". To run our tests, either click the green
button in intellij or run hwo you normally would run them with mvn package.
