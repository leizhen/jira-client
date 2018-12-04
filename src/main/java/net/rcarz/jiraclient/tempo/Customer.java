package net.rcarz.jiraclient.tempo;

import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.RestClient;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static net.rcarz.jiraclient.tempo.TempoResource.getBaseTempoUri;

/**
 * Created by Sergey Nekhviadovich on 12/4/2018.
 */
public class Customer {

    private static final String ID = "id";
    private static final String KEY = "key";
    private static final String NAME = "name";

    private static String getRestUri() {
        return getBaseTempoUri() + "/customer/";
    }

    private Integer id;
    private String key;
    private String name;

    public Customer(Integer id, String key, String name) {
        this.id = id;
        this.key = key;
        this.name = name;
    }

    public Customer(String key) {
        this.key = key;
    }

    public Customer(JSONObject map) {
        id = net.rcarz.jiraclient.Field.getInteger(map.get(ID));
        name = net.rcarz.jiraclient.Field.getString(map.get(NAME));
        key = net.rcarz.jiraclient.Field.getString(map.get(KEY));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<Customer> get(RestClient restclient) throws JiraException {
        JSON response;

        try {
            response = restclient.get(getRestUri());
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve customers", ex);
        }

        if (!(response instanceof JSONArray)) {
            throw new JiraException("JSON payload is malformed");
        }
        List<Customer> result = new ArrayList<Customer>();
        Iterator it = ((JSONArray) response).iterator();
        while (it != null && it.hasNext()) {
            result.add(new Customer((JSONObject) it.next()));
        }
        return result;
    }

    public static Customer create(RestClient restClient, String key, String name) throws JiraException {
        JSON result;
        try {
            result = restClient.post(getRestUri(), new Customer(null, key, name).asJsonObject());
        } catch (Exception ex) {
            throw new JiraException("Failed to create customer", ex);
        }
        if (!(result instanceof JSONObject) || !((JSONObject) result).containsKey("key")
                || !(((JSONObject) result).get("key") instanceof String)) {
            throw new JiraException("Unexpected result on customer creation");
        }

        return new Customer((JSONObject) result);
    }

    public JSONObject asJsonObject() {
        JSONObject result = new JSONObject();
        if (id != null) {
            result.put(ID, id);
        }
        if (StringUtils.isNotBlank(key)) {
            result.put(KEY, key);
        }
        if (StringUtils.isNotBlank(name)) {
            result.put(NAME, name);
        }
        return result;
    }
}
