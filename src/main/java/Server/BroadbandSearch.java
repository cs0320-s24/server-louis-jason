package Server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class BroadbandSearch implements BroadbandInterface<String, BroadbandInfo>{

    HashMap<String, String> stateCodesMap;

    public BroadbandSearch() throws URISyntaxException, IOException, InterruptedException {
        this.stateCodesMap = BroadbandSearch.getStateCodes();
    }
    @Override
    public String search(BroadbandInfo broadbandInfo) throws URISyntaxException, IOException, InterruptedException {
        // WE WILL HAVE TO DESERIALIZE AND SERIALIZE THE BROADBANDDATA BELOW
        String countyName = broadbandInfo.getCountyName();
        String stateName = broadbandInfo.getStateName();

        String stateCode = this.stateCodesMap.get(stateName);
        String countyCode = BroadbandAPIUtilities.deserializeBroadbandCounty(
                this.getCountyCodes(stateCode)).get(countyName);

        String broadbandData;
        try {
            broadbandData = this.sendRequest(countyCode, stateCode);
        } catch (Exception e) {
            e.printStackTrace();
            broadbandData = "Error retrieving data";
        }
        // Adds results to the responseMap
        return broadbandData;
    }

    private String sendRequest(String county, String state)
            throws URISyntaxException, IOException, InterruptedException {
        HttpRequest buildCensusRequest =
            HttpRequest.newBuilder()
                .uri(
                    new URI(
                    "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                        + county
                        + "&in=state:"
                        + state))
                    .GET()
                        .build();

        // Send that API request then store the response in this variable. Note the generic type.
        HttpResponse<String> sentCensusResponse =
                HttpClient.newBuilder()
                        .build()
                        .send(buildCensusRequest, HttpResponse.BodyHandlers.ofString());

        return sentCensusResponse.body();
    }

    private static HashMap<String, String> getStateCodes()
            throws URISyntaxException, IOException, InterruptedException {

        HttpRequest retrieveStateNums =
                HttpRequest.newBuilder()
                        .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
                        .GET()
                        .build();

        // Send that API request then store the response in this variable. Note the generic type.
        HttpResponse<String> stateNums =
                HttpClient.newBuilder()
                        .build()
                        .send(retrieveStateNums, HttpResponse.BodyHandlers.ofString());

        HashMap<String, String> stateCodesMap =
                BroadbandAPIUtilities.deserializeBroadbandIntoStateMap(stateNums.body());
        return stateCodesMap;
    }

    private String getCountyCodes(String stateCode)
            throws URISyntaxException, IOException, InterruptedException {

        HttpRequest retrieveCountyNums =
                HttpRequest.newBuilder()
                        .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
                                + stateCode))
                        .GET()
                        .build();

        // Send that API request then store the response in this variable. Note the generic type.
        HttpResponse<String> countyNums =
                HttpClient.newBuilder()
                        .build()
                        .send(retrieveCountyNums, HttpResponse.BodyHandlers.ofString());

        return countyNums.body();
    }
}
