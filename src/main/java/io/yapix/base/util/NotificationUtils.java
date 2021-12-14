package io.yapix.base.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationListener.UrlOpeningListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import io.yapix.config.DefaultConstants;

/**
 * 消息通知工具类.
 */
public final class NotificationUtils {

    public static final NotificationGroup DEFAULT_GROUP = NotificationGroup.balloonGroup(DefaultConstants.NAME);

    /**
     * 提示普通消息
     */
    public static void notifyInfo(String content) {
        Notification notification = DEFAULT_GROUP.createNotification(content, NotificationType.INFORMATION);
        Notifications.Bus.notify(notification);
    }

    /**
     * 提示普通消息
     */
    public static void notifyInfo(String title, String content) {
        Notification notification = DEFAULT_GROUP
                .createNotification(title, content, NotificationType.INFORMATION, new UrlOpeningListener(false));
        Notifications.Bus.notify(notification);
    }

    /**
     * 提示警告消息
     */
    public static void notifyWarning(String content) {
        Notification notification = DEFAULT_GROUP.createNotification(content, NotificationType.WARNING);
        Notifications.Bus.notify(notification);
    }
    
    /**
     * 提示警告消息
     */
    public static void notifyWarning(String title, String content) {
        Notification notification = DEFAULT_GROUP.createNotification(title, content, NotificationType.WARNING, null);
        Notifications.Bus.notify(notification);
    }
    

    /**
     * 提示错误消息
     */
    public static void notifyError(String content) {
        Notification notification = DEFAULT_GROUP.createNotification(content, NotificationType.ERROR);
        Notifications.Bus.notify(notification);
    }

    /**
     * 提示错误消息
     */
    public static void notifyError(String title, String content) {
        Notification notification = DEFAULT_GROUP.createNotification(title, content, NotificationType.ERROR, null);
        Notifications.Bus.notify(notification);
    }
}
