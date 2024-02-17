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
import java.nio.CharBuffer;
import java.util.ArrayList;
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
    public void testCsvTwoLoads() throws IOException, InterruptedException {
        /////////// LOAD DATASOURCE ///////////
        // Set up the request, make the request
        HttpURLConnection loadCsv = tryRequest("loadcsv?path=census/income_by_race.csv");
        // Get an OK response (the *connection* worked, the *API* provides an error response)
        assertEquals(200, loadCsv.getResponseCode());
        HttpURLConnection viewCsv = tryRequest("viewcsv");
        assertEquals(200, viewCsv.getResponseCode());
        // Get the expected response: a success
        Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(viewCsv.getInputStream()));
        showDetailsIfError(responseBody);
        assertEquals("success", responseBody.get("result"));
        List<List<String>> data = (List<List<String>>) responseBody.get("data");
        assertEquals("ID Race", data.get(0).get(0));

        HttpURLConnection loadCsv2 = tryRequest("loadcsv?path=RI_Town_and_Income.csv");
        // Get an OK response (the *connection* worked, the *API* provides an error response)
        assertEquals(200, loadCsv2.getResponseCode());
        HttpURLConnection viewCsv2 = tryRequest("viewcsv");
        assertEquals(200, viewCsv2.getResponseCode());
        // Get the expected response: a success
        Map<String, Object> responseBody2 = adapter.fromJson(new Buffer().readFrom(viewCsv2.getInputStream()));
        showDetailsIfError(responseBody2);
        assertEquals("success", responseBody2.get("result"));
        List<List<String>> data2 = (List<List<String>>) responseBody2.get("data");
        assertEquals("City/Town", data2.get(0).get(0));
        loadCsv.disconnect();
        loadCsv2.disconnect();
        viewCsv.disconnect();
        viewCsv2.disconnect();
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
        assertEquals("error_bad_json", responseBody.get("result"));
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
        List<List<String>> data = (List<List<String>>) responseBody.get("data");
        assertEquals("City/Town", data.get(0).get(0));
        loadCsv.disconnect();
        viewCsv.disconnect();
    }

    @Test
    public void testCsvLoadSearch() throws IOException {
        /////////// LOAD DATASOURCE ///////////
        // Set up the request, make the request
        HttpURLConnection loadCsv = tryRequest("loadcsv?path=RI_Town_and_Income.csv");
        assertEquals(200, loadCsv.getResponseCode());
        HttpURLConnection searchCsv = tryRequest("searchcsv?value=Cranston");
        assertEquals(200, searchCsv.getResponseCode());
        // Get the expected response: a success
        Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(searchCsv.getInputStream()));
        showDetailsIfError(responseBody);
        List<List<String>> data = (List<List<String>>) responseBody.get("data");
        assertEquals("Cranston", data.get(0).get(0));
        loadCsv.disconnect();
        searchCsv.disconnect();
    }

    @Test
    public void testCsvLoadSearch2() throws IOException {
        /////////// LOAD DATASOURCE ///////////
        // Set up the request, make the request
        HttpURLConnection loadCsv = tryRequest("loadcsv?path=RI_Town_and_Income.csv");
        assertEquals(200, loadCsv.getResponseCode());
        HttpURLConnection searchCsv =
                tryRequest("searchcsv?value=Cranston&booleanHeader=true&identifier=City/Town");
        HttpURLConnection searchCsv2 =
                tryRequest("searchcsv?value=Cranston&booleanHeader=true&identifier=Median%20Family%20Income");
        assertEquals(200, searchCsv.getResponseCode());
        // Get the expected response: a success
        Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(searchCsv.getInputStream()));
        showDetailsIfError(responseBody);
        List<List<String>> data = (List<List<String>>) responseBody.get("data");
        assertEquals("Cranston", data.get(0).get(0));

        Map<String, Object> noResult = adapter.fromJson(new Buffer().readFrom(searchCsv2.getInputStream()));
        showDetailsIfError(noResult);
        List<List<String>> data2 = (List<List<String>>) noResult.get("data");
        List<List<String>> params2 = (List<List<String>>) noResult.get("params");
        List<String> params2check = new ArrayList<>();
        params2check.add("Cranston");
        params2check.add("true");
        params2check.add(null);
        params2check.add("Median Family Income");
        assertEquals(new ArrayList<>(), data2);
        assertEquals(params2check, params2);

        loadCsv.disconnect();
        searchCsv.disconnect();
        searchCsv2.disconnect();
    }

    @Test
    public void testBroadband() throws IOException {
        /////////// LOAD DATASOURCE ///////////
        // Set up the request, make the request
        HttpURLConnection broadband = tryRequest("broadband?state=California&county=Marin%20County");
        assertEquals(200, broadband.getResponseCode());
        Map<String, Object> result = adapter.fromJson(new Buffer().readFrom(broadband.getInputStream()));
        showDetailsIfError(result);
        String data = (String) result.get("result");
        assertEquals("success", data);
        String data2 = (String) result.get("broadband_data");
        assertEquals("94.0", data2);
        String data3 = (String) result.get("county_entered");
        assertEquals("Marin County", data3);
        String data4 = (String) result.get("state_entered");
        assertEquals("California", data4);


        HttpURLConnection broadband2 = tryRequest("broadband?state=California&county=Los%20Angeles%20County");
        assertEquals(200, broadband2.getResponseCode());
        Map<String, Object> result2 = adapter.fromJson(new Buffer().readFrom(broadband2.getInputStream()));
        showDetailsIfError(result2);
        String data5 = (String) result2.get("result");
        assertEquals("error_datasource", data5);

        HttpURLConnection broadband3 = tryRequest("broadband?state=somewhere&county=nowhere");
        assertEquals(200, broadband3.getResponseCode());
        Map<String, Object> result3 = adapter.fromJson(new Buffer().readFrom(broadband3.getInputStream()));
        showDetailsIfError(result3);
        String data6 = (String) result3.get("result");
        assertEquals("error_bad_request", data6);

        broadband.disconnect();
        broadband2.disconnect();
        broadband3.disconnect();
    }

    @Test
    public void testAll() throws IOException {
        /////////// LOAD DATASOURCE ///////////
        // Set up the request, make the request
        HttpURLConnection loadCsv = tryRequest("loadcsv?path=RI_Town_and_Income.csv");
        assertEquals(200, loadCsv.getResponseCode());
        HttpURLConnection viewCsv = tryRequest("viewcsv");
        assertEquals(200, viewCsv.getResponseCode());
        HttpURLConnection searchCsv = tryRequest("searchcsv?value=Cranston");
        assertEquals(200, searchCsv.getResponseCode());

        Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(viewCsv.getInputStream()));
        showDetailsIfError(responseBody);
        List<List<String>> dataV = (List<List<String>>) responseBody.get("data");
        assertEquals("City/Town", dataV.get(0).get(0));

        Map<String, Object> responseBody2 = adapter.fromJson(new Buffer().readFrom(searchCsv.getInputStream()));
        showDetailsIfError(responseBody2);
        List<List<String>> dataS = (List<List<String>>) responseBody2.get("data");
        assertEquals("Cranston", dataS.get(0).get(0));


        HttpURLConnection broadband = tryRequest("broadband?state=California&county=Marin%20County");
        assertEquals(200, broadband.getResponseCode());
        Map<String, Object> result = adapter.fromJson(new Buffer().readFrom(broadband.getInputStream()));
        showDetailsIfError(result);
        String data = (String) result.get("result");
        assertEquals("success", data);
        String data2 = (String) result.get("broadband_data");
        assertEquals("94.0", data2);
        String data3 = (String) result.get("county_entered");
        assertEquals("Marin County", data3);
        String data4 = (String) result.get("state_entered");
        assertEquals("California", data4);

        broadband.disconnect();
        viewCsv.disconnect();
        loadCsv.disconnect();
        searchCsv.disconnect();
    }
}
