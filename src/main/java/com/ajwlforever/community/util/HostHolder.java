package com.ajwlforever.community.util;

import com.ajwlforever.community.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    private  ThreadLocal<User> Users = new ThreadLocal<>();

    public void setUser(User user)
    {
        Users.set(user);
    }

    public User getUser()
    {
        return Users.get();
    }
    public  void clear()
    {
        Users.remove();
    }
}
