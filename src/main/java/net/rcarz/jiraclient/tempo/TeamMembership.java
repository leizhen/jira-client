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
    private static final String START_DATE_ANSI = "dateFromANSI";
    private static final String END_DATE_ANSI = "dateToANSI";
    private static final String START_DATE = "dateFrom";
    private static final String END_DATE = "dateTo";
    private static final String AVAILABILITY = "availability";
    private static final String STATUS = "status";
    private static final String TEAM_ID = "teamId";
    private static final String MEMBERSHIP = "membership";


    private Integer id;
    private String role;
    private Integer roleId;
    //Workload percent
    private Integer availability;
    private User user;
    private Integer teamId;
    private String status;
    private Date startDateANSI;
    private Date endDateANSI;
    private String startDate;
    private String endDate;

    public TeamMembership(User user, Integer id, Integer roleId, String role, Integer availability, Integer teamId, String status, String
            startDate, String endDate) {
        this.user = user;
        this.id = id;
        this.roleId = roleId;
        this.role = role;
        this.availability = availability;
        this.teamId = teamId;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public TeamMembership(User user, JSONObject map) {
        this.user = user;
        id = net.rcarz.jiraclient.Field.getInteger(map.get(ID));
        teamId = (map.getJSONObject(MEMBERSHIP)).getInt(TEAM_ID);
        availability = (map.getJSONObject(MEMBERSHIP)).getInt(AVAILABILITY);
        status = (map.getJSONObject(MEMBERSHIP)).getString(STATUS);
        startDateANSI = parseDate((map.getJSONObject(MEMBERSHIP)), START_DATE_ANSI);
        endDateANSI = parseDate((map.getJSONObject(MEMBERSHIP)), END_DATE_ANSI);
        startDate = (map.getJSONObject(MEMBERSHIP)).getString(START_DATE);
        endDate = (map.getJSONObject(MEMBERSHIP)).getString(END_DATE);
        if (map.getJSONObject(MEMBERSHIP).get(ROLE) != null) {
            role = (map.getJSONObject(MEMBERSHIP)).getJSONObject(ROLE).getString(ROLE_NAME);
            roleId = (map.getJSONObject(MEMBERSHIP)).getJSONObject(ROLE).getInt(ID);
        }
    }

    public JSONObject asJsonObject() {
        JSONObject result = new JSONObject();
        JSONObject member = new JSONObject();
        JSONObject membership = new JSONObject();
        JSONObject role = new JSONObject();

        if (id != null) {
            result.put(ID, id);
        }
        member.put("name", user.getName());
        member.put("type", "USER");
        if (roleId != null) {
            role.put(ID, roleId);
        }
        if (this.role != null) {
            role.put(ROLE_NAME, this.role);
        }
        membership.put(ROLE, role);
        if (startDateANSI != null) {
            membership.put(START_DATE_ANSI, startDateANSI.toString());
        }
        if (endDateANSI != null) {
            membership.put(END_DATE_ANSI, endDateANSI.toString());
        }
        if (startDate != null) {
            membership.put(START_DATE, startDate);
        }
        if (endDate != null) {
            membership.put(END_DATE, endDate);
        }
        if (availability != null) {
            membership.put(AVAILABILITY, availability);
        }
        membership.put(TEAM_ID, teamId);
        if (status != null) {
            membership.put(STATUS, status);
        }

        result.put("member", member);
        result.put("membership", membership);
        return result;
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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Date getStartDateANSI() {
        return startDateANSI;
    }

    public void setStartDateANSI(Date startDateANSI) {
        this.startDateANSI = startDateANSI;
    }

    public Date getEndDateANSI() {
        return endDateANSI;
    }

    public void setEndDateANSI(Date endDateANSI) {
        this.endDateANSI = endDateANSI;
    }
}
