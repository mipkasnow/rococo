package mipkasnow.dev.rococo.jupiter.extension;

import mipkasnow.dev.rococo.api.timing.ApiTimingCollector;
import mipkasnow.dev.rococo.api.timing.ApiTimingReport;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Instant;

@ParametersAreNonnullByDefault
public class SuiteApiTimingExtension implements SuiteExtension {

    private Instant startedAt;

    @Override
    public void beforeSuite(ExtensionContext context) {
        ApiTimingCollector.reset();
        startedAt = Instant.now();
    }

    @Override
    public void afterSuite() {
        Instant finishedAt = Instant.now();
        Instant started = startedAt == null ? finishedAt : startedAt;
        ApiTimingReport.write(started, finishedAt, ApiTimingCollector.snapshot());
    }
}
