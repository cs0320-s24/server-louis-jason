package Server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

public interface BroadbandInterface<RESULT, TARGET> {
    RESULT search(TARGET target) throws URISyntaxException, IOException, InterruptedException;
}
