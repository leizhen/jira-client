package net.rcarz.jiraclient.tempo;

import net.rcarz.jiraclient.*;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Volha Bibik on 01/2/2019.
 */
public class Team {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String SUMMARY = "summary";
    private static final String LEAD = "lead";
    private static final String MEMBER = "member";
    private static final String MEMBERSHIP = "membership";

    private static String getRestUri() {
        return TempoResource.getBaseTempoTeamsUri() + "/team/";
    }

    private Integer id;
    private String name;
    private String summary;
    private String lead;
    private RestClient restClient;
    private List<User> members;
    private List<TeamMembership> membershipList;
    private Map<String, Integer> memberRoles;

    private Team(RestClient restClient, Integer id, String name, String summary, String lead) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.lead = lead;
        this.restClient = restClient;
    }

    public Team(RestClient restClient, JSONObject map) {
        this.restClient = restClient;

        if (map != null) {
            deserialise(map);
        }
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

    /**
     * Returns team members only if it was previously initialized by #findTeamMembers
     *
     * @return list of users
     */
    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    /**
     * Returns membership data only if it was previously initialized by #findMembership
     *
     * @return list of team membership
     */
    public List<TeamMembership> getMembershipList() {
        return membershipList;
    }

    public void setMembershipList(List<TeamMembership> membershipList) {
        this.membershipList = membershipList;
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
            result.add(new Team(restclient, (JSONObject) it.next()));
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

        return new Team(restClient, result);
    }

    public static Team create(RestClient restClient, String name, String summary, String lead) throws JiraException {
        JSON result = null;

        try {
            result = restClient.post(getRestUri(), new Team(null, null, name, summary, lead).asJsonObject());
        } catch (Exception ex) {
            throw new JiraException("Failed to create team", ex);
        }

        if (!(result instanceof JSONObject) || !((JSONObject) result).containsKey(ID)
                || !(((JSONObject) result).get(ID) instanceof Number)) {
            throw new JiraException("Unexpected result on team creation: " + result.toString());
        }

        return new Team(restClient, (JSONObject) result);
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

    public List<User> findTeamMembers() throws JiraException {
        if (members == null) {
            findTeamMembersship();
        }
        return members;
    }

    public List<TeamMembership> findTeamMembersship() throws JiraException {
        if (membershipList == null) {
            JSON response;

            if (id == null) {
                throw new IllegalArgumentException("ID can't be null");
            }
            try {
                response = restClient.get(getRestUri() + id + "/member");
            } catch (Exception ex) {
                throw new JiraException("Failed to retrieve team members by id: " + id, ex);
            }

            if (!(response instanceof JSONArray)) {
                throw new JiraException("JSON payload is malformed");
            }
            members = new ArrayList<>();
            membershipList = new ArrayList<>();
            Iterator it = ((JSONArray) response).iterator();
            while (it != null && it.hasNext()) {
                JSONObject jsonObject = (JSONObject) it.next();
                String teamMemberUsername = jsonObject.getJSONObject(MEMBER).getString("name");
                User user = null;
                try {
                    user = User.get(restClient, teamMemberUsername);
                } catch (JiraException je) {
                    if (je.getMessage().startsWith("No such user found")) {
                        JSONObject defaultUser = new JSONObject();
                        defaultUser.put("id", teamMemberUsername);
                        defaultUser.put("name", teamMemberUsername);
                        defaultUser.put("displayName", teamMemberUsername);
                        defaultUser.put("active", false);
                        user = new User(restClient, defaultUser);
                    }
                }
                if (user != null) {
                    members.add(user);
                    membershipList.add(new TeamMembership(user, jsonObject.getJSONObject(MEMBERSHIP)));
                }
            }
        }
        return membershipList;
    }

    /**
     * @param dateFrom String with date in format "dd/MM/yyyy"
     * @param dateTo String with date in format "dd/MM/yyyy"
     *
     * @return membership with role by default if provided roleName does not exist
     */
    public TeamMembership addTeamMember(String username, String roleName, String dateFrom, String dateTo, Integer availability) throws
            JiraException {
        JSON response;
        
        if (id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }

        findMembersRoles();
        Integer roleId = memberRoles.get(roleName);

        try {
            response = restClient.post(getRestUri() + id + "/member", new TeamMembership(User.get(restClient, username), null,
                    roleId, null, availability, id, null, dateFrom, dateTo).asJsonObject());
        } catch (Exception ex) {
            throw new JiraException("Failed to add team member " + username + " by id: " + id, ex);
        }

        if (!(response instanceof JSONObject)) {
            throw new JiraException("JSON payload is malformed");
        }

        String teamMemberUsername = ((JSONObject) response).getJSONObject("member").getString("name");

        return new TeamMembership(User.get(restClient, teamMemberUsername), (JSONObject) response);
    }

    public Map<String, Integer> findMembersRoles() throws JiraException {

        if (memberRoles == null) {
            JSON response;

            try {
                response = restClient.get( TempoResource.getBaseTempoTeamsUri() + "/role");
            } catch (Exception ex) {
                throw new JiraException("Failed to retrieve team roles", ex);
            }

            if (!(response instanceof JSONArray)) {
                throw new JiraException("JSON payload is malformed");
            }
            memberRoles = new HashMap<>();
            Iterator it = ((JSONArray) response).iterator();
            while (it != null && it.hasNext()) {
                JSONObject jsonObject = (JSONObject) it.next();
                String roleName = jsonObject.getString("name");
                Integer roleId = jsonObject.getInt("id");
                memberRoles.put(roleName, roleId);
            }
        }
        return memberRoles;
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

    private void deserialise(JSONObject map) {
        id = net.rcarz.jiraclient.Field.getInteger(map.get(ID));
        name = net.rcarz.jiraclient.Field.getString(map.get(NAME));
        summary = net.rcarz.jiraclient.Field.getString(map.get(SUMMARY));
        lead = net.rcarz.jiraclient.Field.getString(map.get(LEAD));
    }
}