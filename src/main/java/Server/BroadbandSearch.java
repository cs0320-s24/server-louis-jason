package Server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BroadbandSearch implements BroadbandInterface<String,String>{

    @Override
    public String search(String countyAndStateCode) {
        // WE WILL HAVE TO DESERIALIZE AND SERIALIZE THE BROADBANDDATA BELOW
        String[] codeArr = countyAndStateCode.split(",");
        String countyCode = codeArr[0];
        String stateCode = codeArr[1];
        String broadbandData;
        try {
            broadbandData = this.sendRequest(countyCode, stateCode);
        }
        catch (Exception e){
            e.printStackTrace();
            broadbandData = "";
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
}
