package cn.parzulpan.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 角色实体类
 */

public class Role implements Serializable {
    private Integer roleId;
    private String roleName;
    private String roleDesc;

    private List<User> users;   //多对多的关系映射：一个角色可以赋予多个用户

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", roleDesc='" + roleDesc + '\'' +
                ", users=" + users +
                '}';
    }
}
