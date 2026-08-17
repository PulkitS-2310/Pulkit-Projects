package org.Chats;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "messages")
@Data
public class DM {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userName;

    @Column
    private String friend;

    @Lob
    private String content;

    //@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent")
    private String sent;
	
	
	public DM() {};
	
	public DM(String userName, String friend, String content) {
		this.userName = userName;
		this.content = content;
        this.friend = friend;
        sent = LocalDateTime.now().toString();
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSent() {

        LocalDateTime time = null;

        try
        {
            time = LocalDateTime.parse(sent);
        } catch (Exception e)
        {
            String[] s = sent.split(" ");
            String end = s[0] + "T" + s[1];
            time = LocalDateTime.parse(end);
        }

        return time;
    }

    public void setSent(LocalDateTime sent) {
        this.sent = sent.toString();
    }

    public String getFriend() { return friend; }

    public void setFriend(String friend) { this.friend = friend; }

    
}
