package io.apidocx.base.util;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import io.apidocx.config.DefaultConstants;
import lombok.experimental.UtilityClass;

/**
 * 密码安全读写工具类
 */
@UtilityClass
public class PasswordSafeUtils {

    /**
     * 获取密码
     */
    public static String getPassword(String key, String username) {
        CredentialAttributes attributes = new CredentialAttributes(
                CredentialAttributesKt.generateServiceName(DefaultConstants.ID, key), username);
        return PasswordSafe.getInstance().getPassword(attributes);
    }

    /**
     * 存储密码
     */
    public static void storePassword(String key, String username, String password) {
        Credentials credentials = new Credentials(username, password);
        CredentialAttributes attributes = new CredentialAttributes(
                CredentialAttributesKt.generateServiceName(DefaultConstants.ID, key));
        PasswordSafe.getInstance().set(attributes, credentials);
    }

}
