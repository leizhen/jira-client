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

    public Account getAccount(String key) throws JiraException {
        return Account.get(restClient, key);
    }

    public List<Team> getTeams() throws JiraException {
        return Team.get(restClient);
    }

    public Team getTeam(Integer id) throws JiraException {
        return Team.get(restClient, id);
    }

    public Team createTeam(String name, String summary, String lead) throws JiraException {
        return Team.create(restClient, name, summary, lead);
    }

    public boolean deleteTeam(Integer id) throws JiraException {
        return Team.delete(restClient, id);
    }
}
