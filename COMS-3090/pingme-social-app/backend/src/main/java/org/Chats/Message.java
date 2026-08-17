package org.Chats;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Data;
import net.bytebuddy.asm.Advice;

@Entity
@Table(name = "messages")
@Data
public class Message {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userName;

    @Column
    private Integer groupID;

    @Lob
    private String content;

    //@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent")
    private String sent;
	
	
	public Message() {};
	
	public Message(String userName, String content, int ID) {
		this.userName = userName;
		this.content = content;
        this.groupID = ID;
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

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }
}
