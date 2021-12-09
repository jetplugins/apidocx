package io.yapix.base.util;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import io.yapix.config.DefaultConstants;

public class PasswordSafeUtils {

    private PasswordSafeUtils() {
    }

    /**
     * 获取密码
     */
    public static String getPassword(String key, String username) {
        return PasswordSafe.getInstance().getPassword(createCredentialAttributes(key, username));
    }

    /**
     * 存储密码
     */
    public static void storePassword(String key, String username, String password) {
        Credentials credentials = new Credentials("", password);
        PasswordSafe.getInstance().set(createCredentialAttributes(key, username), credentials);
    }

    private static CredentialAttributes createCredentialAttributes(String key, String username) {
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName(DefaultConstants.ID, key), username);
    }

}
