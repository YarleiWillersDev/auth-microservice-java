package br.com.confidence.updater.user;

import org.springframework.stereotype.Component;

import br.com.confidence.model.user.User;

@Component
public class UserUpdater {

    public void updateUsername(User user, String newName) {
        user.setName(newName);
    }

    public void updateEmail(User user, String newEmail) {
        user.setEmail(newEmail);
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
    }

}
