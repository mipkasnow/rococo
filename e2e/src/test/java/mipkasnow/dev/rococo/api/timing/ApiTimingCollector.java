package mipkasnow.dev.rococo.api.timing;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@ParametersAreNonnullByDefault
@UtilityClass
public final class ApiTimingCollector {

    private static final ConcurrentLinkedQueue<ApiCallSample> CALLS = new ConcurrentLinkedQueue<>();

    public static void record(ApiCallSample sample) {
        CALLS.add(sample);
    }

    @Nonnull
    public static List<ApiCallSample> snapshot() {
        return List.copyOf(CALLS);
    }

    public static void reset() {
        CALLS.clear();
    }
}
