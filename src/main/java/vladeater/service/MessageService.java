package vladeater.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vladeater.domain.Message;
import vladeater.domain.User;
import vladeater.domain.dto.MessageDto;
import vladeater.repos.MessageRepo;


/**
 * @author Vlados Guskov
 */

@Service
public class MessageService {

    private final MessageRepo messageRepo;

    @Autowired
    public MessageService(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    public Page<MessageDto> getMessageList(Pageable pageable, String filter, User user){
        if (filter != null && !filter.isEmpty()){
            return messageRepo.findByTag(filter, pageable, user);
        } else {
            return findAll(pageable,user);
        }
    }

    public Page<MessageDto> getMessageListByAuthor(Pageable pageable, User author, User currentUser) {
        return messageRepo.findByUser(pageable,author,currentUser);
    }

    public void deleteMessage(User currentUser, Message message){
        if(message != null && (message.getAuthor().equals(currentUser) || currentUser.isAdmin())){
            messageRepo.delete(message);
        }
    }

    public void saveMessage(Message message) {
        messageRepo.save(message);
    }

    public Page<MessageDto> findAll(Pageable pageable,User user) {
        return messageRepo.findAll(pageable,user);
    }
}
