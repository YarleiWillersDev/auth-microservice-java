package br.com.confidence.updater.user;

import org.springframework.stereotype.Component;

import br.com.confidence.dto.user.UserUpdateRequest;
import br.com.confidence.model.user.User;

@Component
public class UserUpdater {

    public void updateUsername(User user, UserUpdateRequest updateRequest) {
        updateRequest.name().ifPresent(user::setName);
    }
}
