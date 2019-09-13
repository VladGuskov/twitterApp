package vladeater.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import vladeater.domain.Message;
import vladeater.domain.User;
import vladeater.domain.dto.MessageDto;
import vladeater.service.MessageService;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Vlados Guskov
 */

@Controller
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting() { return "greeting"; }

    @GetMapping("/main")
    public String main(
            @RequestParam(required = false, defaultValue = "") String filter,
            Model model,
            @PageableDefault(sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal User user
    ){
        Page<MessageDto> page = messageService.getMessageList(pageable, filter, user);
        model.addAttribute("page",page);
        model.addAttribute("url","/main");
        model.addAttribute("filter",filter);
        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @Valid Message message,
            BindingResult bindingResult,
            Model model,
            @RequestParam("file") MultipartFile file,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) throws IOException {
        message.setAuthor(user);
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        } else {
            saveFile(message, file);
            model.addAttribute("message", null);
            messageService.saveMessage(message);
        }
        Page<MessageDto> page = messageService.findAll(pageable,user);
        model.addAttribute("url", "/main");
        model.addAttribute("page", page);
        return "main";
    }

    @GetMapping("/user-messages/{user}")
    public String userMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,
            Model model,
            @RequestParam(required = false) Message message,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        getUserContext(currentUser, user, model, message, pageable);
        return "userMessages";
    }

    @PostMapping("/user-messages/{user}")
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,
            @RequestParam("id") Message message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if(message.getAuthor().equals(currentUser)){
            if (!StringUtils.isEmpty(text)){
                message.setText(text);
            }
            if (!StringUtils.isEmpty(tag)){
                message.setTag(tag);
            }
            saveFile(message,file);
            messageService.saveMessage(message);
        }
        return "redirect:/user-messages/" + user.getId();
    }

    @GetMapping("/user-messages/delete/{user}")
    public String deleteMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,
            @RequestParam(value = "message", required = false) Message message,
            Model model,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        messageService.deleteMessage(currentUser, message);
        getUserContext(currentUser, user, model, message, pageable);
        if (message == null || (currentUser.isAdmin() && !message.getAuthor().isAdmin())) {
            return "redirect:/main";
        } else return "userMessages";
    }

    @GetMapping("messages/{message}/like")
    public String like(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Message message,
            RedirectAttributes redirectAttributes,
            @RequestHeader(required = false) String referer
    ){
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

    private void saveFile(
            @Valid Message message,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
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

    private void getUserContext(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User author,
            Model model,
            @RequestParam(required = false) Message message,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MessageDto> page = messageService.getMessageListByAuthor(pageable, author, currentUser);
        model.addAttribute("userChannel", author);
        model.addAttribute("message", message);
        model.addAttribute("subscriptionsCount", author.getSubscriptions().size());
        model.addAttribute("subscribersCount", author.getSubscribers().size());
        model.addAttribute("isSubscriber", author.getSubscribers().contains(currentUser));
        model.addAttribute("isCurrentUser", currentUser.equals(author));
        model.addAttribute("url", "/user-messages/" + author.getId());
        model.addAttribute("page", page);
    }
}