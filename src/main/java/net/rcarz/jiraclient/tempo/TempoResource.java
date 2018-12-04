package net.rcarz.jiraclient.tempo;

/**
 * Created by Sergey Nekhviadovich on 12/4/2018.
 */
public class TempoResource {

    private static String getApiRev() {
        return "1";
    }

    public static String getBaseTempoUri() {
        return "rest/tempo-accounts/" + getApiRev();
    }

}
