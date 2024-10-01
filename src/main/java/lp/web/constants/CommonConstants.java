package lp.web.constants;

/**
 * This utility class exposes common constants
 */
public class CommonConstants {

    /**
     * The standard message for the private constructor of utility classes
     */
    public static final String STANDARD_MESSAGE_UTILITY_CLASS = "Utility class";

    /**
     * Private constructor for the utility class
     */
    private CommonConstants() {
        throw new IllegalAccessError(STANDARD_MESSAGE_UTILITY_CLASS);
    }

}
