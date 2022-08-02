package com.ryl.customControls.button.submitButton;


import javafx.scene.text.Font;


/**
 * Created by hansolo on 22.08.16.
 */
public class Fonts {
    private static final String ROBOTO_THIN_NAME;
    private static final String ROBOTO_LIGHT_NAME;
    private static final String ROBOTO_REGULAR_NAME;
    private static final String ROBOTO_MEDIUM_NAME;
    private static final String ROBOTO_BOLD_NAME;

    private static String robotoThinName;
    private static String robotoLightName;
    private static String robotoRegularName;
    private static String robotoMediumName;
    private static String robotoBoldName;


    static {
        try {
            robotoThinName             = Font.loadFont(Fonts.class.getResourceAsStream("/fonts/Roboto-Thin.ttf"), 10).getName();
            robotoLightName            = Font.loadFont(Fonts.class.getResourceAsStream("/fonts/Roboto-Light.ttf"), 10).getName();
            robotoRegularName          = Font.loadFont(Fonts.class.getResourceAsStream("/fonts/Roboto-Regular.ttf"), 10).getName();
            robotoMediumName           = Font.loadFont(Fonts.class.getResourceAsStream("/fonts/Roboto-Medium.ttf"), 10).getName();
            robotoBoldName             = Font.loadFont(Fonts.class.getResourceAsStream("/fonts/Roboto-Bold.ttf"), 10).getName();
        } catch (Exception exception) { }
        ROBOTO_THIN_NAME              = robotoThinName;
        ROBOTO_LIGHT_NAME             = robotoLightName;
        ROBOTO_REGULAR_NAME           = robotoRegularName;
        ROBOTO_MEDIUM_NAME            = robotoMediumName;
        ROBOTO_BOLD_NAME              = robotoBoldName;
    }


    // ******************** Methods *******************************************
    public static Font robotoThin(final double SIZE) { return new Font(ROBOTO_THIN_NAME, SIZE); }
    public static Font robotoLight(final double SIZE) { return new Font(ROBOTO_LIGHT_NAME, SIZE); }
    public static Font robotoRegular(final double SIZE) { return new Font(ROBOTO_REGULAR_NAME, SIZE); }
    public static Font robotoMedium(final double SIZE) { return new Font(ROBOTO_MEDIUM_NAME, SIZE); }
    public static Font robotoBold(final double SIZE) { return new Font(ROBOTO_BOLD_NAME, SIZE); }
}
