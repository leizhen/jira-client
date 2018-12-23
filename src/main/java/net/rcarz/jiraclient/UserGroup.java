package net.rcarz.jiraclient;

import net.sf.json.JSONObject;

/**
 * Created by Sergey Nekhviadovich on 12/20/2018.
 */
public class UserGroup extends Resource {

    private String name = null;

    /**
     * Creates a new JIRA resource.
     *
     * @param restclient REST client instance
     */
    public UserGroup(RestClient restclient) {
        this(restclient, null);
    }

    public UserGroup(RestClient restclient, JSONObject json) {
        super(restclient);
        if (json != null) {
            deserialize(json);
        }
    }

    private void deserialize(JSONObject json) {
        this.self = Field.getString(json.get("self"));
        this.name = Field.getString(json.get("name"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
