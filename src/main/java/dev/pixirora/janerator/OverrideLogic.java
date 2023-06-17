package dev.pixirora.janerator;

import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.License;

public class OverrideLogic {
    private Function overrideFunction;

    public OverrideLogic() {
        this(JaneratorConfig.getOverrideFunction());
    }

    private OverrideLogic(String functionText) {
        OverrideLogic.activateLicense();
        this.overrideFunction = new Function(functionText);
    }

    private static synchronized void activateLicense() {
        if (!License.checkIfUseTypeConfirmed()) {
            boolean licenseStatus = License.iConfirmNonCommercialUse("RinaS");
            Janerator.LOGGER.info("MXParser license status: " + licenseStatus);
        }
    }

    public boolean shouldOverride(double x, double z) {
        return overrideFunction.calculate(x, z) == 1.0;
    }
}
