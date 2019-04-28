package net.rcarz.jiraclient;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import java.net.URI;

public class IssueTypeScheme {
    private RestClient restClient;

    public IssueTypeScheme(RestClient restClient){
        this.restClient = restClient;
    }

    /**
     * 将项目矛issuetype schme进行关联
     * @param schemeId
     * @param projectIdOrkey
     * @throws JiraException
     */
    public void addIssueTypeSchemeProjectAssociations(int schemeId, String...projectIdOrkey) throws JiraException{
        JSON response = null;
        try{
            URI uri = restClient.buildURI(Resource.getBaseUri() + "issuetypescheme/" + schemeId + "/associations");
            JSONObject payload = new JSONObject();
            payload.put("idsOrKeys", projectIdOrkey);
            response = restClient.post(uri, payload);
        }catch(Exception e){
            throw new JiraException(e.getMessage());
        }
        if(!(response instanceof JSONObject)){
            throw new JiraException("Associated project to scheme failed");
        }
    }
}
