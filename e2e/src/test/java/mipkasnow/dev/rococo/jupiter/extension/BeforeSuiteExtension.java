package mipkasnow.dev.rococo.jupiter.extension;

import mipkasnow.dev.rococo.utils.ClearDataUtils;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BeforeSuiteExtension implements SuiteExtension {

    @Override
    public void beforeSuite(ExtensionContext context) {
        ClearDataUtils.clear();
    }
}
