package com.kiran.madanwad.springsecurityclient.event;

import com.kiran.madanwad.springsecurityclient.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistartionCompleteEvent extends ApplicationEvent {

    private User user;
    private String applicationUrl;
    public RegistartionCompleteEvent(User user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
