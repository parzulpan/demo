package cn.parzulpan.domain;

import java.io.Serializable;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

public class QueryV implements Serializable {
    private static final long serialVersionUID = 93481234250463743L;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
