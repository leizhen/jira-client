package net.rcarz.jiraclient.tempo;

import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.RestClient;

import java.util.List;

/**
 * Created by Sergey Nekhviadovich on 12/4/2018.
 */
public class Tempo {
    private RestClient restClient;

    public Tempo(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Category> getCategories() throws JiraException {
        return Category.get(restClient);
    }

    public List<Customer> getCustomers() throws JiraException {
        return Customer.get(restClient);
    }

    public Account.FluentCreate createAccount(String key, String name) throws JiraException {
        return Account.create(restClient, key, name);
    }

    public Customer createCustomer(String key, String name) throws JiraException {
        return Customer.create(restClient, key, name);
    }
}
