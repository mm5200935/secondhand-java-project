package app.controller;

import app.dto.request.SendMessageRequest;
import app.dto.response.ConversationResponse;
import app.dto.response.MessageResponse;
import app.model.Advertisement;
import app.model.Conversation;
import app.model.User;
import app.security.AuthenticatedUser;
import app.service.interfaces.AdvertisementService;
import app.service.interfaces.ConversationService;
import app.service.interfaces.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final AdvertisementService advertisementService;
    private final UserService userService;

    public ConversationController(ConversationService conversationService,
                                  AdvertisementService advertisementService,
                                  UserService userService) {
        this.conversationService = conversationService;
        this.advertisementService = advertisementService;
        this.userService = userService;
    }

    // شروع گفت‌وگو + ارسال اولین/بعدی پیام (AdDetailPage و ChatPage هر دو همین رو صدا می‌زنن)
    @PostMapping("/messages")
    public ConversationResponse sendMessage(@RequestBody SendMessageRequest request,
                                            @AuthenticationPrincipal AuthenticatedUser principal) {
        User sender = userService.findById(principal.id());
        Advertisement ad = advertisementService.getById(request.getAdId());

        Conversation conversation = conversationService.startConversation(sender, ad);
        conversationService.sendMessage(conversation.getId(), sender, request.getContent());

        return new ConversationResponse(conversationService.getById(conversation.getId()));
    }

    @GetMapping
    public List<ConversationResponse> getMyConversations(@AuthenticationPrincipal AuthenticatedUser principal) {
        return conversationService.getUserConversations(principal.id())
                .stream().map(ConversationResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ConversationResponse getById(@PathVariable int id) {
        return new ConversationResponse(conversationService.getById(id));
    }

    @GetMapping("/{id}/messages")
    public List<MessageResponse> getMessages(@PathVariable int id) {
        return conversationService.getMessages(id)
                .stream().map(MessageResponse::new).collect(Collectors.toList());
    }

    // حذف/بستن گفت‌وگو
    @DeleteMapping("/{id}")
    public void deleteConversation(@PathVariable int id,
                                   @AuthenticationPrincipal AuthenticatedUser principal) {
        User user = userService.findById(principal.id());
        conversationService.closeConversation(id, user); // یا متد delete که پایین‌تر گفتم
    }

    // داخل ConversationController.java

    @PostMapping("/start")
    public ConversationResponse startConversation(@RequestBody SendMessageRequest request,
                                                  @AuthenticationPrincipal AuthenticatedUser principal) {
        User buyer = userService.findById(principal.id());
        Advertisement ad = advertisementService.getById(request.getAdId());
        Conversation conversation = conversationService.startConversation(buyer, ad);
        return new ConversationResponse(conversation);
    }

    // پیام جدید به یک گفت‌وگوی از قبل موجود (بر اساس conversationId، نه adId)
    @PostMapping("/{id}/messages")
    public MessageResponse addMessage(@PathVariable int id,
                                      @RequestBody SendMessageRequest request,
                                      @AuthenticationPrincipal AuthenticatedUser principal) {
        User sender = userService.findById(principal.id());
        return new MessageResponse(conversationService.sendMessage(id, sender, request.getContent()));
    }
}