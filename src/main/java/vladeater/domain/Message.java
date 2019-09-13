package vladeater.domain;

import org.hibernate.validator.constraints.Length;
import vladeater.domain.util.MessageHelper;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Vlados Guskov
 */

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Пожалуйста введите сообщение")
    @Length(max = 2048, message = "Сообщение слишком длинное (> 2 kB)")
    private String text;
    @Length(max = 255, message = "Сообщение слишком длинное (> 255)")
    private String tag;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;
    private String filename;
    @ManyToMany
    @JoinTable(
            name = "message_likes",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likes = new HashSet<>();

    public Message() {}

    public Message(String text, String tag, User author) {
        this.author = author;
        this.text = text;
        this.tag = tag;
    }

    public String getAuthorName(){
        return MessageHelper.getAuthorName(author);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public User getAuthor() { return author; }

    public void setAuthor(User author) { this.author = author; }

    public String getFilename() { return filename; }

    public void setFilename(String filename) { this.filename = filename; }

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
    }
}
