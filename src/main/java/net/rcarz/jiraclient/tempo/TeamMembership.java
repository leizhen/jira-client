package net.rcarz.jiraclient.tempo;

import net.rcarz.jiraclient.User;
import net.sf.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sergey Nekhviadovich on 2/14/2019.
 */
public class TeamMembership {
    private static final String ID = "id";
    private static final String ROLE = "role";
    private static final String ROLE_NAME = "name";
    private static final String START_DATE = "dateFromANSI";
    private static final String END_DATE = "dateToANSI";
    private static final String AVAILABILITY = "availability";
    private static final String STATUS = "status";
    private static final String TEAM_ID = "team_id";

    private Integer id;
    private String role;
    //Workload percent
    private Integer availability;
    private User user;
    private Integer teamId;
    private String status;
    private Date startDate;
    private Date endDate;

    public TeamMembership(User user, JSONObject map) {
        this.user = user;
        id = net.rcarz.jiraclient.Field.getInteger(map.get(ID));
        teamId = net.rcarz.jiraclient.Field.getInteger(map.get(TEAM_ID));
        status = net.rcarz.jiraclient.Field.getString(map.get(STATUS));
        startDate = parseDate(map, START_DATE);
        endDate = parseDate(map, END_DATE);
        if (map.get(ROLE) != null) {
            role = ((JSONObject) map.get(ROLE)).getString(ROLE_NAME);
        }
    }

    private Date parseDate(JSONObject map, String field) {
        Date result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String dateStr = net.rcarz.jiraclient.Field.getString(map.get(field));
            if (dateStr != null) {
                result = sdf.parse(dateStr);
            }
        } catch (ParseException pe) {
            //ignore it for now
        }
        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
