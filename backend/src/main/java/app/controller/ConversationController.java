package app.controller;

import app.dto.MessageRequest;
import app.model.Conversation;
import app.model.Message;
import app.model.User;
import app.service.interfaces.ConversationService;
import app.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final UserService userService;

    @Autowired
    public ConversationController(
            ConversationService conversationService,
            UserService userService
    ) {
        this.conversationService = conversationService;
        this.userService = userService;
    }

    /**
     * Send a message.
     */
    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(
            @RequestBody MessageRequest request,
            Principal principal
    ) {
        try {

            User sender = userService.findByUsername(principal.getName());

            User receiver = userService.findById(request.getReceiverId());

            Message message = conversationService.sendMessage(
                    sender,
                    receiver,
                    request.getAdvertisementId(),
                    request.getContent()
            );

            return ResponseEntity.ok(message);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Get all conversations of current user.
     */
    @GetMapping
    public ResponseEntity<?> getUserConversations(
            Principal principal
    ) {
        try {

            User currentUser = userService.findByUsername(principal.getName());

            List<Conversation> conversations =
                    conversationService.getUserConversations(currentUser);

            return ResponseEntity.ok(conversations);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Get a conversation by id.
     */
    @GetMapping("/{conversationId}")
    public ResponseEntity<?> getConversation(
            @PathVariable int conversationId
    ) {
        try {

            Conversation conversation =
                    conversationService.getById(conversationId);

            return ResponseEntity.ok(conversation);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Get all messages in a conversation.
     */
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<?> getMessages(
            @PathVariable int conversationId
    ) {
        try {

            return ResponseEntity.ok(
                    conversationService.getMessages(conversationId)
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Close a conversation.
     */
    @PutMapping("/{conversationId}/close")
    public ResponseEntity<?> closeConversation(
            @PathVariable int conversationId,
            Principal principal
    ) {
        try {

            User currentUser =
                    userService.findByUsername(principal.getName());

            conversationService.closeConversation(
                    conversationId,
                    currentUser
            );

            return ResponseEntity.ok("Conversation closed successfully.");

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}