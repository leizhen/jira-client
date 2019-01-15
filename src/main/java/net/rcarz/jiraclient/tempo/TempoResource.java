package net.rcarz.jiraclient.tempo;

/**
 * Created by Sergey Nekhviadovich on 12/4/2018.
 */
public class TempoResource {

    private static String getAccountApiRev() {
        return "1";
    }

    private static String getTeamApiRev() {
        return "2";
    }

    public static String getBaseTempoAccountsUri() {
        return "rest/tempo-accounts/" + getAccountApiRev();
    }

    public static String getBaseTempoTeamsUri() {
        return "rest/tempo-teams/" + getTeamApiRev();
    }
}
