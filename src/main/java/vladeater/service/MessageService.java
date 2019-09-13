package vladeater.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import vladeater.domain.Message;
import vladeater.domain.User;
import vladeater.domain.dto.MessageDto;
import vladeater.repos.MessageRepo;
import vladeater.util.ControllerUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


/**
 * @author Vlados Guskov
 */

@Service
public class MessageService {

    private final MessageRepo messageRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public MessageService(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    public String main(String filter, Model model, Pageable pageable, User user) {
        Page<MessageDto> page;
        if (filter != null && !filter.isEmpty()){
            page = messageRepo.findByTag(filter, pageable, user);
        } else {
            page = messageRepo.findAll(pageable,user);
        }
        model.addAttribute("page",page);
        model.addAttribute("url","/main");
        model.addAttribute("filter",filter);
        return "main";
    }

    public String add(
            User user,
            Message message,
            BindingResult bindingResult,
            Model model,
            MultipartFile file,
            Pageable pageable
    ) throws IOException {
        message.setAuthor(user);
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        } else {
            saveFile(message, file);
            model.addAttribute("message", null);
            messageRepo.save(message);
        }
        Page<MessageDto> page = messageRepo.findAll(pageable,user);
        model.addAttribute("url", "/main");
        model.addAttribute("page", page);
        return "main";
    }

    public String getUserMessages(User currentUser, User user, Model model, Message message, Pageable pageable) {
        getUserContext(currentUser, user, model, message,pageable);
        return "userMessages";
    }

    public String updateMessage(
            User currentUser,
            User user,
            Message message,
            String text,
            String tag,
            MultipartFile file
    ) throws IOException {
        if(message.getAuthor().equals(currentUser)){
            if (!StringUtils.isEmpty(text)){
                message.setText(text);
            }
            if (!StringUtils.isEmpty(tag)){
                message.setTag(tag);
            }
            saveFile(message,file);
            messageRepo.save(message);
        }
        return "redirect:/user-messages/" + user.getId();
    }

    public String delete(
            User currentUser,
            User user,
            Message message,
            Model model,
            Pageable pageable
    ) {
        if(message != null && (message.getAuthor().equals(currentUser) || currentUser.isAdmin())){
            messageRepo.delete(message);
        }
        getUserContext(currentUser, user, model, message, pageable);
        if (message == null || (currentUser.isAdmin() && !message.getAuthor().isAdmin())) {
            return "redirect:/main";
        } else return "userMessages";
    }

    public String like(
            User currentUser,
            Message message,
            RedirectAttributes redirectAttributes,
            String referer
    ) {
        Set<User> likes = message.getLikes();
        if (likes.contains(currentUser)){
            likes.remove(currentUser);
        } else {
            likes.add(currentUser);
        }
        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();
        components.getQueryParams()
                .forEach(redirectAttributes::addAttribute);
        return "redirect:" + components.getPath();
    }

    private void getUserContext(
            User currentUser,
            User author,
            Model model,
            Message message,
            Pageable pageable
    ) {
        Page<MessageDto> page = messageRepo.findByUser(pageable, author, currentUser);
        model.addAttribute("userChannel", author);
        model.addAttribute("message", message);
        model.addAttribute("subscriptionsCount", author.getSubscriptions().size());
        model.addAttribute("subscribersCount", author.getSubscribers().size());
        model.addAttribute("isSubscriber", author.getSubscribers().contains(currentUser));
        model.addAttribute("isCurrentUser", currentUser.equals(author));
        model.addAttribute("url", "/user-messages/" + author.getId());
        model.addAttribute("page", page);
    }

    private void saveFile(Message message, MultipartFile file) throws IOException {
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFilename));
            message.setFilename(resultFilename);
        }
    }
}
