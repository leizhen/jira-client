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

import static net.rcarz.jiraclient.tempo.TempoResource.getBaseTempoAccountsUri;

/**
 * Created by Sergey Nekhviadovich on 12/4/2018.
 */
public class Category {

    private static final String ID = "id";
    private static final String KEY = "key";
    private static final String NAME = "name";

    private static String getRestUri() {
        return getBaseTempoAccountsUri() + "/category/";
    }

    private Integer id;
    private String key;
    private String name;

    public Category(Integer id, String key, String name) {
        this.id = id;
        this.key = key;
        this.name = name;
    }

    public Category(String key) {
        this.key = key;
    }

    public Category(JSONObject map) {
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

    public static List<Category> get(RestClient restclient) throws JiraException {
        JSON response;

        try {
            response = restclient.get(getRestUri());
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve categories", ex);
        }

        if (!(response instanceof JSONArray)) {
            throw new JiraException("JSON payload is malformed");
        }
        List<Category> result = new ArrayList<Category>();
        Iterator it = ((JSONArray) response).iterator();
        while (it != null && it.hasNext()) {
            Map categoryMap = (Map) it.next();
            Integer id = (Integer) categoryMap.get(ID);
            String key = (String) categoryMap.get(KEY);
            String name = (String) categoryMap.get(NAME);
            result.add(new Category(id, key, name));
        }
        return result;
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
