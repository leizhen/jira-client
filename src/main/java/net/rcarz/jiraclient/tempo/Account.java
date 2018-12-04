package net.rcarz.jiraclient.tempo;

import net.rcarz.jiraclient.*;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.rcarz.jiraclient.tempo.TempoResource.getBaseTempoUri;

/**
 * Created by Sergey Nekhviadovich on 12/4/2018.
 */
public class Account {
    private interface Field {
        String NAME = "name";
        String KEY = "key";
        String ID = "id";
        String LEAD = "lead";
        String STATUS = "status";
        String CUSTOMER = "customer";
        String CUSTOMER_IS_NEW = "isNew";
        String CATEGORY = "category";
        String CATEGORY_IS_NEW = "isNew";
        String LEAD_USERNAME = "username";
    }


    private static String getRestUri(String key) {
        String url = getBaseTempoUri() + "/account/";
        if (StringUtils.isNotBlank(key)) {
            url += "key/" + key;
        }
        return url;
    }

    /**
     * Used to chain fields to a create action.
     */
    public static final class FluentCreate {

        Map<String, Object> fields = new HashMap<String, Object>();
        RestClient restClient;
        JSONObject createMeta;

        private FluentCreate(RestClient restClient, JSONObject createMeta) {
            this.restClient = restClient;
            this.createMeta = createMeta;
        }

        /**
         * Executes the create action.
         *
         * @throws JiraException when the create fails
         */
        public Account create() throws JiraException {
            if (fields.size() == 0) {
                throw new JiraException("No fields were given for create");
            }

            JSONObject req = new JSONObject();
            req.putAll(fields);

            JSON result;

            try {
                result = restClient.post(getRestUri(null), req);
            } catch (Exception ex) {
                throw new JiraException("Failed to create account", ex);
            }

            if (!(result instanceof JSONObject) || !((JSONObject) result).containsKey("key")
                    || !(((JSONObject) result).get("key") instanceof String)) {
                throw new JiraException("Unexpected result on create account");
            }

            return new Account(restClient, (JSONObject) result);
        }

        /**
         * Appends a field to the create action.
         *
         * @param name Name of the field
         * @param value New field value
         *
         * @return the current fluent create instance
         */
        public Account.FluentCreate field(String name, Object value) {
            fields.put(name, value);
            return this;
        }

        /**
         * Appends lead field with provided data. At least one of the fields is mandatory
         *
         * @param username if set other parameters are ignored
         * @param name display name of the lead
         * @param email email of the lead
         *
         * @return the current fluent create instance
         */
        public Account.FluentCreate lead(final String username, final String email, final String name) throws JiraException {
            JSONObject lead = new JSONObject();
            String leadName = username;
            if (StringUtils.isBlank(username)) {
                List<User> users = User.search(restClient, email);
                Optional<User> user = users.stream().filter(u -> u.getEmail().equals(email)).findFirst();
                if (user.isPresent()) {
                    leadName = user.get().getName();
                } else {
                    users = User.search(restClient, name);
                    user = users.stream().filter(u -> u.getDisplayName().equals(name)).findFirst();
                    if (user.isPresent()) {
                        leadName = user.get().getName();
                    }
                }
            }
            lead.put(Field.LEAD_USERNAME, leadName);
            fields.put(Field.LEAD, lead);
            return this;
        }

        public Account.FluentCreate customer(Customer customer, boolean isNew) {
            JSONObject jsonObject = customer.asJsonObject();
            jsonObject.put(Field.CUSTOMER_IS_NEW, isNew);
            fields.put(Field.CUSTOMER, jsonObject);
            return this;
        }

        public Account.FluentCreate category(Category category, boolean isNew) {
            JSONObject jsonObject = category.asJsonObject();
            jsonObject.put(Field.CATEGORY_IS_NEW, isNew);
            fields.put(Field.CATEGORY, jsonObject);
            return this;
        }
    }

    public static FluentCreate create(RestClient restClient, String key, String name) throws JiraException {
        JSONObject createMeta = new JSONObject();
        Account.FluentCreate fc = new Account.FluentCreate(restClient, createMeta);
        fc.field(Field.NAME, name);
        fc.field(Field.KEY, key);
        return fc;
    }

    private String status;
    private Category category;
    private Customer customer;
    private Integer id;
    private String key;
    private String name;
    private User lead;
    //TODO: add rate table

    public Account(RestClient restClient, JSONObject map) {
        id = net.rcarz.jiraclient.Field.getInteger(map.get(Field.ID));
        status = net.rcarz.jiraclient.Field.getString(map.get(Field.STATUS));
        name = net.rcarz.jiraclient.Field.getString(map.get(Field.NAME));
        key = net.rcarz.jiraclient.Field.getString(map.get(Field.KEY));
        lead = new User(restClient, (JSONObject) map.get(Field.LEAD));
        if (map.has(Field.CATEGORY)) {
            category = new Category((JSONObject) map.get(Field.CATEGORY));
        }
        if (map.has(Field.CUSTOMER)) {
            customer = new Customer((JSONObject) map.get(Field.CUSTOMER));
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public User getLead() {
        return lead;
    }

    public void setLead(User lead) {
        this.lead = lead;
    }
}

