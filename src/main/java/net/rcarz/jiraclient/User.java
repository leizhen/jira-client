/**
 * jira-client - a simple JIRA REST client
 * Copyright (c) 2013 Bob Carroll (bob.carroll@alum.rit.edu)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.rcarz.jiraclient;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;

import java.util.*;

/**
 * Represents a JIRA user.
 */
public class User extends Resource {

    private boolean active = false;
    private Map<String, String> avatarUrls = null;
    private String displayName = null;
    private String email = null;
    private String name = null;
    private List<UserGroup> groups = null;

    /**
     * Creates a user from a JSON payload.
     *
     * @param restclient REST client instance
     * @param json       JSON payload
     */
    public User(RestClient restclient, JSONObject json) {
        super(restclient);

        if (json != null) {
            deserialise(json);
        }
    }

    /**
     * Retrieves the given user record.
     *
     * @param restclient REST client instance
     * @param username   User logon name
     * @return a user instance
     * @throws JiraException when the retrieval fails
     */
    public static User get(RestClient restclient, String username)
            throws JiraException {

        JSON result = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);

        try {
            result = restclient.get(getBaseUri() + "user", params);
        } catch (Exception ex) {
            if (ex instanceof RestException) {
                RestException re = (RestException) ex;
                if (re.getHttpStatusCode() == HttpStatus.SC_NOT_FOUND) {
                    throw new JiraException("No such user found " + username, re);
                }
            }
            throw new JiraException("Failed to retrieve user " + username, ex);
        }

        if (!(result instanceof JSONObject)) {
            throw new JiraException("JSON payload is malformed");
        }

        return new User(restclient, (JSONObject) result);
    }

    public static List<User> search(RestClient restclient, String query) throws JiraException {
        return search(restclient, query, 0, 50, true, false);
    }

    public static List<User> search(RestClient restclient, String query, int startAt, int maxResults, boolean includeActive, boolean includeInactive) throws
            JiraException {
        JSON response;

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", query);
        params.put("startAt", "" + startAt);
        params.put("maxResults", "" + maxResults);
        params.put("includeActive", "" + includeActive);
        params.put("includeInactive", "" + includeInactive);

        try {
            response = restclient.get(getBaseUri() + "user/search", params);
        } catch (Exception ex) {
            throw new JiraException("Failed to find users " + query, ex);
        }

        if (!(response instanceof JSONArray)) {
            throw new JiraException("JSON payload is malformed");
        }

        List<User> result = new ArrayList<User>();
        Iterator it = ((JSONArray) response).iterator();
        while (it != null && it.hasNext()) {
            result.add(new User(restclient, (JSONObject) it.next()));
        }

        return result;
    }

    /**
     * Retrieves user groups from server in case it's not initialized.
     * User groups cached within single User object.
     *
     * @return list of UserGroups
     * @throws JiraException in case of any issue
     */
    public List<UserGroup> findGroups() throws JiraException {
        if (groups == null) {
            JSON response;

            Map<String, String> params = new HashMap<String, String>();
            params.put("username", this.name);
            params.put("expand", "groups");
            try {
                response = restclient.get(getBaseUri() + "user", params);
            } catch (Exception ex) {
                throw new JiraException("Failed to get user " + this.name, ex);
            }

            if (!(response instanceof JSONObject)) {
                throw new JiraException("JSON payload is malformed");
            }
            deserialise((JSONObject) response);
        }
        return groups;
    }

    private void deserialise(JSONObject json) {
        Map map = json;

        self = Field.getString(map.get("self"));
        id = Field.getString(map.get("id"));
        active = Field.getBoolean(map.get("active"));
        avatarUrls = Field.getMap(String.class, String.class, map.get("avatarUrls"));
        displayName = Field.getString(map.get("displayName"));
        email = getEmailFromMap(map);
        name = Field.getString(map.get("name"));
        if (map.get("groups") != null && (map.get("groups") instanceof JSONObject)) {
            JSONObject jsonGroupObj = (JSONObject) map.get("groups");
            if (jsonGroupObj.get("items") != null && jsonGroupObj.get("items") instanceof JSONArray) {
                JSONArray jsonGroups = (JSONArray) jsonGroupObj.get("items");
                groups = new LinkedList<>();
                Iterator it = jsonGroups.iterator();
                while (it.hasNext()) {
                    groups.add(new UserGroup(restclient, (JSONObject) it.next()));
                }
            }
        }
    }



    /**
     * API changes email address might be represented as either "email" or "emailAddress"
     *
     * @param map JSON object for the User
     * @return String email address of the JIRA user.
     */
    private String getEmailFromMap(Map map) {
        if (map.containsKey("email")) {
            return Field.getString(map.get("email"));
        } else {
            return Field.getString(map.get("emailAddress"));
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean isActive() {
        return active;
    }

    public Map<String, String> getAvatarUrls() {
        return avatarUrls;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns user groups only if it was previously initialized by #findGroups
     *
     * @return list of user groups
     */
    public List<UserGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<UserGroup> groups) {
        this.groups = groups;
    }
}

