package net.rcarz.jiraclient.tempo;

import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.RestClient;
import net.rcarz.jiraclient.RestException;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Volha Bibik on 01/2/2019.
 */
public class Team {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String SUMMARY = "summary";
    private static final String LEAD = "lead";

    private static String getRestUri() {
        return TempoResource.getBaseTempoTeamsUri() + "/team/";
    }

    private Integer id;
    private String name;
    private String summary;
    private String lead;

    public Team(Integer id, String name, String summary, String lead) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.lead = lead;
    }


    public Team(JSONObject map) {
        id = net.rcarz.jiraclient.Field.getInteger(map.get(ID));
        name = net.rcarz.jiraclient.Field.getString(map.get(NAME));
        summary = net.rcarz.jiraclient.Field.getString(map.get(SUMMARY));
        lead = net.rcarz.jiraclient.Field.getString(map.get(LEAD));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLead() {
        return lead;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public static List<Team> get(RestClient restclient) throws JiraException {
        JSON response = null;

        try {
            response = restclient.get(getRestUri());
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve all teams", ex);
        }

        if (!(response instanceof JSONArray)) {
            throw new JiraException("JSON payload is malformed");
        }
        List<Team> result = new ArrayList<Team>();
        Iterator it = ((JSONArray) response).iterator();
        while (it != null && it.hasNext()) {
            result.add(new Team((JSONObject) it.next()));
        }

        return result;
    }

    public static Team get(RestClient restClient, Integer id) throws JiraException {
        JSON response = null;
        if (id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }
        try {
            response = restClient.get(getRestUri() + id);
        } catch (RestException rx) {
            if (rx.getHttpStatusCode() == HttpStatus.SC_NOT_FOUND) {
                return null;
            } else {
                throw new JiraException("Failed to retrieve team by id: " + id, rx);
            }
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve team by id: " + id, ex);
        }

        if (!(response instanceof JSONObject)) {
            throw new JiraException("JSON payload is malformed");
        }
        JSONObject result = (JSONObject) response;
        if (!result.containsKey(ID) || !(result.get(ID) instanceof Number)) {
            return null;
        }

        return new Team(result);
    }

    public static Team create(RestClient restClient, String name, String summary, String lead) throws JiraException {
        JSON result = null;

        try {
            result = restClient.post(getRestUri(), new Team(null, name, summary, lead).asJsonObject());
        } catch (Exception ex) {
            throw new JiraException("Failed to create team", ex);
        }

        if (!(result instanceof JSONObject) || !((JSONObject) result).containsKey(ID)
                || !(((JSONObject) result).get(ID) instanceof Number)) {
            throw new JiraException("Unexpected result on team creation: " + result.toString());
        }

        return new Team((JSONObject) result);
    }

    /**
     * Delete tempo team by id
     *
     * @return false if team with provided id does not exist
     */
    public static boolean delete(RestClient restClient, Integer id) throws JiraException {
        JSON result = new JSONObject();

        if (id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }
        try {
            result = restClient.delete(getRestUri() + id);
        } catch (RestException rx) {
            if (rx.getHttpStatusCode() == HttpStatus.SC_NOT_FOUND) {
                return false;
            } else {
                throw new JiraException("Failed to delete team by id " + id, rx);
            }
        } catch (Exception ex) {
            throw new JiraException("Failed to delete team by id " + id, ex);
        }

        //check that response is correct (correct response is null)
        if (result != null) {
            throw new JiraException("Unexpected result on team deletion: " + result);
        }
        return true;

    }

    public JSONObject asJsonObject() {
        JSONObject result = new JSONObject();
        if (id != null) {
            result.put(ID, id);
        }
        if (StringUtils.isNotBlank(name)) {
            result.put(NAME, name);
        }
        result.put(SUMMARY, summary);
        if (StringUtils.isNotBlank(lead)) {
            result.put(LEAD, lead);
        }
        return result;
    }
}

