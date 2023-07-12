package ai.chat2db.spi.ssh;

import com.jcraft.jsch.UserInfo;

public class MyUserInfo implements UserInfo {

    private String passphrase;

    public MyUserInfo(String passphrase) {
        this.passphrase = passphrase;
    }
    @Override
    public String getPassphrase() {
        return passphrase;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean promptPassword(String s) {
        return true;
    }

    @Override
    public boolean promptPassphrase(String s) {
        return true;
    }

    @Override
    public boolean promptYesNo(String s) {
        return true;
    }

    @Override
    public void showMessage(String s) {
        System.out.println(s);
    }
}
