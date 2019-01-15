package net.rcarz.jiraclient.tempo;

import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.RestClient;
import net.rcarz.jiraclient.RestException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

public class TeamTest {

    @Test
    public void testInitTeam() {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        Team team = new Team(restClient, getTestJSON());
        assertEquals(team.getId(), (Integer) 3);
        assertEquals(team.getName(), "Cloud Development");
        assertEquals(team.getSummary(), "Cloud Development Team");
        assertEquals(team.getLead(), "erica");
    }

    @Test
    public void testGetTeamByID() throws Exception {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        when(restClient.get(anyString())).thenReturn(getTestJSON());
        Team team = Team.get(restClient, 3);
        assertEquals(team.getId(), (Integer) 3);
        assertEquals(team.getName(), "Cloud Development");
        assertEquals(team.getSummary(), "Cloud Development Team");
        assertEquals(team.getLead(), "erica");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJiraExceptionGetTeamByNullID() throws JiraException {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        Team.get(restClient, null);
    }

    @Test(expected = JiraException.class)
    public void testRestExceptionGetTeamByID() throws Exception {
        final RestClient mockRestClient = PowerMockito.mock(RestClient.class);
        when(mockRestClient.get(anyString())).thenThrow(RestException.class);
        Team.get(mockRestClient, 3);
    }

    @Test(expected = JiraException.class)
    public void testJiraExceptionGetNonJSON() throws Exception {
        final RestClient mockRestClient = PowerMockito.mock(RestClient.class);
        when(mockRestClient.get(anyString())).thenReturn(new JSONArray());
        Team.get(mockRestClient, 3);
    }

    @Test
    public void testNullResultGetTeamByID() throws Exception {
        final RestClient mockRestClient = PowerMockito.mock(RestClient.class);
        when(mockRestClient.get(anyString())).thenReturn(getTestErrorJSON());
        Team team = Team.get(mockRestClient, 3);
        assertNull(team);
    }

    @Test
    public void testGetTeamsByID() throws Exception {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        when(restClient.get(anyString())).thenReturn(getTestJSONArray());
        List<Team> team = Team.get(restClient);
        assertEquals(team.get(0).getId(), (Integer) 3);
        assertEquals(team.get(0).getName(), "Cloud Development");
        assertEquals(team.get(0).getSummary(), "Cloud Development Team");
        assertEquals(team.get(0).getLead(), "erica");
        assertEquals(team.get(1).getId(), (Integer) 3);
        assertEquals(team.get(1).getName(), "Cloud Development");
        assertEquals(team.get(1).getSummary(), "Cloud Development Team");
        assertEquals(team.get(1).getLead(), "erica");
    }

    @Test
    public void testCreateTeam() throws Exception {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        when(restClient.post(anyString(), any(JSONObject.class))).thenReturn(getTestJSON());
        Team team = Team.create(restClient, "Cloud Development", "Cloud Development Team", "erica");
        assertEquals(team.getId(), (Integer) 3);
        assertEquals(team.getName(), "Cloud Development");
        assertEquals(team.getSummary(), "Cloud Development Team");
        assertEquals(team.getLead(), "erica");
    }

    @Test(expected = JiraException.class)
    public void testJiraExceptionCreateTeam() throws Exception {
        final RestClient mockRestClient = PowerMockito.mock(RestClient.class);
        when(mockRestClient.post(anyString(), any(JSONObject.class))).thenThrow(RestException.class);
        Team.create(mockRestClient, "Cloud Development", "Cloud Development Team", "erica");
    }

    @Test(expected = JiraException.class)
    public void testJiraExceptionCreateNonJSON() throws Exception {
        final RestClient mockRestClient = PowerMockito.mock(RestClient.class);
        when(mockRestClient.post(anyString(), any(JSONObject.class))).thenReturn(getTestErrorJSON());
        Team.get(mockRestClient, 3);
    }

    @Test
    public void testDeleteExistTeam() throws Exception {
        final RestClient mockRestClient = PowerMockito.mock(RestClient.class);
        when(mockRestClient.delete(anyString())).thenReturn(null);
        boolean team = Team.delete(mockRestClient, 3);
        assertTrue(team);
    }

    @Test
    public void testDeleteNonExistTeam() throws Exception {
        final RestClient mockRestClient = PowerMockito.mock(RestClient.class);
        when(mockRestClient.delete(anyString())).thenThrow(new RestException("", 404, "", new Header[] {new BasicHeader("", "")}));
        boolean team = Team.delete(mockRestClient, 3);
        assertFalse(team);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJiraExceptionDeleteTeamByNullID() throws JiraException {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        Team.delete(restClient, null);
    }

    @Test(expected = JiraException.class)
    public void testRestExceptionDeleteTeam() throws Exception {
        final RestClient mockRestClient = PowerMockito.mock(RestClient.class);
        when(mockRestClient.delete(anyString())).thenThrow(RestException.class);
        Team.delete(mockRestClient, 3);
    }

    @Test(expected = JiraException.class)
    public void testJiraExceptionDeleteTeam() throws Exception {
        final RestClient mockRestClient = PowerMockito.mock(RestClient.class);
        when(mockRestClient.delete(anyString())).thenReturn(getTestJSON());
        Team.delete(mockRestClient, 3);
    }

    private JSONObject getTestJSON() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", 3);
        jsonObject.put("name", "Cloud Development");
        jsonObject.put("summary", "Cloud Development Team");
        jsonObject.put("lead", "erica");

        return jsonObject;
    }

    private JSONObject getTestErrorJSON() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("name", "Cloud Development");
        jsonObject.put("summary", "Cloud Development Team");
        jsonObject.put("lead", "erica");

        return jsonObject;
    }

    private JSONArray getTestJSONArray() {
        JSONArray jsonArray = new JSONArray();

        jsonArray.add(getTestJSON());
        jsonArray.add(getTestJSON());

        return jsonArray;
    }
}
