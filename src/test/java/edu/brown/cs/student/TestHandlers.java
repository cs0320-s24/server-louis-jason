package edu.brown.cs.student;

import JsonTypes.BroadbandInfo;
import JsonTypes.CSVFile;
import Server.Broadband.BroadbandHandler;
import Server.Broadband.BroadbandSearch;
import Server.Cache.CachedSearcher;
import Server.Csv.CSVDataWrapper;
import Server.Csv.LoadCSVFileHandler;
import Server.Csv.SearchCSVFileHandler;
import Server.Csv.ViewCSVFileHandler;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.Mocks.MockCensusAPI;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static spark.Spark.after;

public class TestHandlers {

    @BeforeAll
    public static void setupOnce() {
        // Pick an arbitrary free port
        Spark.port(0);
        // Eliminate logger spam in console for test suite
        Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
    }

    private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    private JsonAdapter<Map<String, Object>> adapter;
    private JsonAdapter<CSVFile> csvFileAdapter;
    private JsonAdapter<BroadbandInfo> broadbandInfoAdapter;

    @BeforeEach
    public void setup() throws URISyntaxException, IOException, InterruptedException {
        // Re-initialize parser, state, etc. for every test method

        // Create a CSV Data Wrapper
        CSVDataWrapper<List<String>> dataWrapper = new CSVDataWrapper<List<String>>(null);
        // Create a cached searcher for a MOCKED searcher. Set to 10 max cache size and 1 minute
        // expirations.
        CachedSearcher<String, BroadbandInfo> mockedBroadbandSearch =
                new CachedSearcher<>(new MockCensusAPI(), 10, 60);

        Spark.get("loadcsv", new LoadCSVFileHandler(dataWrapper));
        Spark.get("viewcsv", new ViewCSVFileHandler(dataWrapper));
        Spark.get("searchcsv", new SearchCSVFileHandler(dataWrapper));
        Spark.get("broadband", new BroadbandHandler(mockedBroadbandSearch));
        Spark.awaitInitialization(); // don't continue until the server is listening

        // New Moshi adapter for responses (and requests, too; see a few lines below)
        //   For more on this, see the Server gearup.
        Moshi moshi = new Moshi.Builder().build();
        adapter = moshi.adapter(mapStringObject);
        csvFileAdapter = moshi.adapter(CSVFile.class);
        broadbandInfoAdapter = moshi.adapter(BroadbandInfo.class);
    }

    @AfterEach
    public void tearDown() {
        // Gracefully stop Spark listening on both endpoints
        Spark.unmap("loadcsv");
        Spark.unmap("viewcsv");
        Spark.unmap("searchcsv");
        Spark.unmap("broadband");
        Spark.awaitStop(); // don't proceed until the server is stopped
    }

    /**
     * Helper to start a connection to a specific API endpoint/params
     *
     * The "throws" clause doesn't matter below -- JUnit will fail if an
     *     exception is thrown that hasn't been declared as a parameter to @Test.
     *
     * @param apiCall the call string, including endpoint
     *                (Note: this would be better if it had more structure!)
     * @return the connection for the given URL, just after connecting
     * @throws IOException if the connection fails for some reason
     */
    private HttpURLConnection tryRequest(String apiCall) throws IOException {
        // Configure the connection (but don't actually send a request yet)
        URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
        // The request body contains a Json object
        clientConnection.setRequestProperty("Content-Type", "application/json");
        // We're expecting a Json object in the response body
        clientConnection.setRequestProperty("Accept", "application/json");

        clientConnection.connect();
        return clientConnection;
    }

    /**
     * Helper to make working with a large test suite easier: if an error, print more info.
     * @param body
     */
    private void showDetailsIfError(Map<String, Object> body) {
        if(body.containsKey("type") && "error".equals(body.get("type"))) {
            System.out.println(body.toString());
        }
    }


    // TESTS START HERE
    // TESTS START HERE
    // TESTS START HERE

    @Test
    public void testCsvLoads() throws IOException {
        /////////// LOAD DATASOURCE ///////////
        // Set up the request, make the request
        HttpURLConnection loadCsv = tryRequest("loadcsv?path=RI_Town_and_Income.csv");
        // Get an OK response (the *connection* worked, the *API* provides an error response)
        assertEquals(200, loadCsv.getResponseCode());
        // Get the expected response: a success
        Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadCsv.getInputStream()));
        showDetailsIfError(responseBody);
        assertEquals("success", responseBody.get("result"));
        loadCsv.disconnect();
    }

    @Test
    public void testBadPathLoad() throws IOException {
        /////////// LOAD DATASOURCE ///////////
        // Set up the request, make the request
        HttpURLConnection loadCsv = tryRequest("loadcsv?src/main/java/JsonTypes/BroadbandInfo.java");
        // Get an OK response (the *connection* worked, the *API* provides an error response)
        assertEquals(200, loadCsv.getResponseCode());
        // Get the expected response: a success
        Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadCsv.getInputStream()));
        showDetailsIfError(responseBody);
        assertEquals("error_datasource", responseBody.get("result"));
        loadCsv.disconnect();
    }

    @Test
    public void testCsvLoadView() throws IOException {
        /////////// LOAD DATASOURCE ///////////
        // Set up the request, make the request
        HttpURLConnection loadCsv = tryRequest("loadcsv?path=RI_Town_and_Income.csv");
        assertEquals(200, loadCsv.getResponseCode());
        HttpURLConnection viewCsv = tryRequest("viewcsv");
        assertEquals(200, viewCsv.getResponseCode());
        // Get the expected response: a success
        Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(viewCsv.getInputStream()));
        showDetailsIfError(responseBody);
        Map<String, Object> responseMap = (Map<String, Object>) responseBody.get("responseMap");
        List<List<String>> data = (List<List<String>>) responseMap.get("data");
        assertEquals("City/Town", data.get(0).get(0));
        loadCsv.disconnect();
    }

    @Test
    public void testCsvLoadSearch() throws IOException {
        /////////// LOAD DATASOURCE ///////////
        // Set up the request, make the request
        HttpURLConnection loadCsv = tryRequest("loadcsv?path=RI_Town_and_Income.csv");
        assertEquals(200, loadCsv.getResponseCode());
        HttpURLConnection viewCsv = tryRequest("searchcsv?value=Cranston");
        assertEquals(200, viewCsv.getResponseCode());
        // Get the expected response: a success
        Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(viewCsv.getInputStream()));
        showDetailsIfError(responseBody);
        Map<String, Object> responseMap = (Map<String, Object>) responseBody.get("responseMap");
        List<List<String>> data = (List<List<String>>) responseMap.get("data");
        assertEquals("Cranston", data.get(0).get(0));
        loadCsv.disconnect();
    }

    @Test
    public void testCsvLoadSearch2() throws IOException {
        /////////// LOAD DATASOURCE ///////////
        // Set up the request, make the request
        HttpURLConnection loadCsv = tryRequest("loadcsv?path=RI_Town_and_Income.csv");
        assertEquals(200, loadCsv.getResponseCode());
        HttpURLConnection viewCsv =
                tryRequest("searchcsv?value=Cranston&booleanHeader=true&identifier=City/Town");
        HttpURLConnection viewCsv2 =
                tryRequest("searchcsv?value=Cranston&booleanHeader=true&identifier=Median%20Family%20Income");
        assertEquals(200, viewCsv.getResponseCode());
        // Get the expected response: a success
        Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(viewCsv.getInputStream()));
        showDetailsIfError(responseBody);
        Map<String, Object> responseMap = (Map<String, Object>) responseBody.get("responseMap");
        List<List<String>> data = (List<List<String>>) responseMap.get("data");
        assertEquals("Cranston", data.get(0).get(0));

//        Map<String, Object> noResult = adapter.fromJson(new Buffer().readFrom(viewCsv2.getInputStream()));
//        showDetailsIfError(noResult);
//        assertEquals("error_datasource", noResult.get("result"));

        loadCsv.disconnect();
    }
}
