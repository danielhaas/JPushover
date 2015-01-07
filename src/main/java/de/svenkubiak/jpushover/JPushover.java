package de.svenkubiak.jpushover;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.svenkubiak.jpushover.enums.Constants;
import de.svenkubiak.jpushover.enums.Priority;
import de.svenkubiak.jpushover.enums.Sound;

public class JPushover {
    private static final Logger LOG = LoggerFactory.getLogger(JPushover.class);

    private String pushoverToken;
    private String pushoverUser;
    private String pushoverMessage;
    private String pushoverDevice;
    private String pushoverTitle;
    private String pushoverUrl;
    private String pushoverUrlTitle;
    private String pushoverTimestamp;
    private Priority pushoverPriority;
    private Sound pushoverSound;

    public JPushover(){
    }

    /**
     * Your application's API token
     * (required) 
     * 
     * @param token
     * @return JPushover instance
     */
    public JPushover token(String token) {
        this.pushoverToken = token;
        return this;
    }

    /**
     * The user/group key (not e-mail address) of your user (or you),
     * viewable when logged into the @see <a href="https://pushover.net/login">pushover dashboard</a>
     * (required) 
     * 
     * @param user
     * @return JPushover instance
     */
    public JPushover user(String user) {
        this.pushoverUser = user;
        return this;
    }

    /**
     * Your message 
     * (required) 
     * 
     * @param message
     * @return JPushover instance
     */
    public JPushover message(String message) {
        this.pushoverMessage = message;
        return this;
    }

    /**
     * Your user's device name to send the message directly to that device,
     * rather than all of the user's devices
     * (optional)
     * 
     * @param device
     * @return JPushover instance
     */
    public JPushover device(String device) {
        this.pushoverDevice = device;
        return this;
    }

    /**
     * Your message's title, otherwise your app's name is used
     * (optional)
     * 
     * @param title
     * @return JPushover instance
     */
    public JPushover title(String title) {
        this.pushoverTitle = title;
        return this;
    }

    /**
     * A supplementary URL to show with your message
     * (optional)
     * 
     * @param url
     * @return JPushover instance
     */
    public JPushover url(String url) {
        this.pushoverUrl = url;
        return this;
    }

    /**
     * A title for your supplementary URL, otherwise just the URL is shown
     * 
     * @param urlTitle
     * @return JPushover instance
     */
    public JPushover urlTitle(String urlTitle) {
        this.pushoverUrlTitle = urlTitle;
        return this;
    }

    /**
     * A Unix timestamp of your message's date and time to display to the user,
     * rather than the time your message is received by our API
     * 
     * @param timestamp
     * @return JPushover instance
     */
    public JPushover timestamp(String timestamp) {
        this.pushoverTimestamp = timestamp;
        return this;
    }

    /**
     * Priority of the message based on the @see <a href="https://pushover.net/api#priority">documentation</a>
     * (optional)
     * 
     * @param priority
     * @return JPushover instance
     */
    public JPushover priority(Priority priority) {
        this.pushoverPriority = priority;
        return this;
    }

    /**
     * The name of one of the sounds supported by device clients to override
     * the user's default sound choice
     * (optional)
     * 
     * @param sound
     * @return JPushover instance
     */
    public JPushover sound(Sound sound) {
        this.pushoverSound = sound;
        return this;
    }

    /**
     * Send the message to pushover
     */
    public JPushoverResponse push() {
        Preconditions.checkNotNull(this.pushoverToken, "Token is required");
        Preconditions.checkNotNull(this.pushoverUser, "User is required");
        Preconditions.checkNotNull(this.pushoverMessage, "Message is required");
        
        List<NameValuePair> params = Form.form()
                .add(Constants.TOKEN.get(), this.pushoverToken)
                .add(Constants.USER.get(), this.pushoverUser)
                .add(Constants.MESSAGE.get(), this.pushoverMessage)
                .add(Constants.DEVICE.get(), this.pushoverDevice)
                .add(Constants.TITLE.get(), this.pushoverTitle)
                .add(Constants.URL.get(), this.pushoverUrl)
                .add(Constants.URLTITLE.get(), this.pushoverUrlTitle)
                .add(Constants.PRIORITY.get(), this.pushoverPriority.get())
                .add(Constants.TIMESTAMP.get(), this.pushoverTimestamp)
                .add(Constants.SOUND.get(), this.pushoverSound.get())
                .build();

        HttpResponse httpResponse = null;
        JPushoverResponse jPushoverResponse = null;
        try {
            httpResponse = Request.Post(Constants.PUSHOVER_URL.get()).bodyForm(params, Consts.UTF_8).execute().returnResponse();
            
            if (httpResponse != null) {
                int status = httpResponse.getStatusLine().getStatusCode();
                
                jPushoverResponse = new JPushoverResponse()
                    .httpStatus(status)
                    .response(IOUtils.toString(httpResponse.getEntity().getContent(), Consts.UTF_8))
                    .isSuccessful((status == 200) ? true : false);
            }
        } catch (IOException e) {
            LOG.error("Failed to send message to pushover", e);
        }
        
        return (jPushoverResponse == null) ? new JPushoverResponse().isSuccessful(false) : jPushoverResponse;
    }
}