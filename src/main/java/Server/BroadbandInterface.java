package Server;

import java.util.Collection;

public interface BroadbandInterface<RESULT, TARGET> {
    RESULT search(TARGET target);
}
