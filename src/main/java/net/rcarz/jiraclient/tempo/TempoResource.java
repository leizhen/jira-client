package net.rcarz.jiraclient.tempo;

/**
 * Created by Sergey Nekhviadovich on 12/4/2018.
 */
public class TempoResource {

    private static String getApiRev() {
        return "1";
    }

    public static String getBaseTempoAccountsUri() {
        return "rest/tempo-accounts/" + getApiRev();
    }

    public static String getBaseTempoTeamsUri() {
        return "rest/tempo-teams/" + getApiRev();
    }

}
