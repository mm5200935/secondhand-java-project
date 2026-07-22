package app.service;

import app.dto.request.MessageRequest;
import app.dto.response.Response;
import app.model.Ad;
import app.model.Conversation;
import app.model.Message;
import app.model.User;
import app.repository.ConversationRepository;
import app.repository.MessageRepository;
import app.repository.UserRepository;

import java.util.Date;
import java.util.List;

public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AdService adService;

    public ConversationService(ConversationRepository conversationRepository,
                               MessageRepository messageRepository,
                               UserRepository userRepository,
                               AdService adService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.adService = adService;
    }

    public Response<Conversation> sendMessage(MessageRequest request) {
        // Validation
        if (request.getAdId() == null) {
            return Response.error("شناسه آگهی الزامی است");
        }
        if (request.getSenderId() == null) {
            return Response.error("شناسه فرستنده الزامی است");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return Response.error("متن پیام نمی‌تواند خالی باشد");
        }

        // Check sender
        var senderOpt = userRepository.findById(request.getSenderId());
        if (senderOpt.isEmpty()) {
            return Response.error("فرستنده یافت نشد");
        }
        User sender = senderOpt.get();
        if (sender.isBlocked()) {
            return Response.error("کاربر مسدود شده است");
        }

        // Get ad details
        var adResponse = adService.getAdDetails(request.getAdId());
        if (!adResponse.isSuccess() || adResponse.getData() == null) {
            return Response.error("آگهی مورد نظر یافت نشد");
        }
        Ad ad = adResponse.getData();

        // Check if sender is the owner
        if (ad.getOwnerId().equals(request.getSenderId())) {
            return Response.error("شما نمی‌توانید برای آگهی خودتان پیام ارسال کنید");
        }

        // Check if seller is blocked
        var sellerOpt = userRepository.findById(ad.getOwnerId());
        if (sellerOpt.isEmpty() || sellerOpt.get().isBlocked()) {
            return Response.error("فروشنده مسدود شده است");
        }

        // Find or create conversation
        Conversation conversation = conversationRepository
                .findByAdIdAndBuyerId(request.getAdId(), request.getSenderId())
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    newConv.setAdId(request.getAdId());
                    newConv.setBuyerId(request.getSenderId());
                    newConv.setSellerId(ad.getOwnerId());
                    return conversationRepository.save(newConv);
                });

        // Create message
        Message message = new Message();
        message.setConversationId(conversation.getId());
        message.setSenderId(request.getSenderId());
        message.setContent(request.getContent().trim());
        message = messageRepository.save(message);

        // Update conversation
        conversation.getMessageIds().add(message.getId());
        conversation.setLastMessageTime(new Date());
        conversationRepository.save(conversation);

        // Update user conversations list
        sender.getConversationIds().add(conversation.getId());
        userRepository.save(sender);

        if (sellerOpt.isPresent() && !sellerOpt.get().getConversationIds().contains(conversation.getId())) {
            User seller = sellerOpt.get();
            seller.getConversationIds().add(conversation.getId());
            userRepository.save(seller);
        }

        return Response.success("پیام با موفقیت ارسال شد", conversation);
    }

    public Response<List<Conversation>> getUserConversations(Long userId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Response.error("کاربر یافت نشد");
        }
        return Response.success("لیست گفت‌وگوها", conversationRepository.findByUserId(userId));
    }

    public Response<List<Message>> getConversationMessages(Long conversationId, Long userId) {
        var convOpt = conversationRepository.findById(conversationId);
        if (convOpt.isEmpty()) {
            return Response.error("گفت‌وگو یافت نشد");
        }

        Conversation conv = convOpt.get();
        // Check if user is part of conversation
        if (!conv.getBuyerId().equals(userId) && !conv.getSellerId().equals(userId)) {
            return Response.error("شما دسترسی به این گفت‌وگو ندارید");
        }

        return Response.success("پیام‌های گفت‌وگو", messageRepository.findByConversationId(conversationId));
    }

    public Response<Conversation> getConversationById(Long conversationId, Long userId) {
        var convOpt = conversationRepository.findById(conversationId);
        if (convOpt.isEmpty()) {
            return Response.error("گفت‌وگو یافت نشد");
        }

        Conversation conv = convOpt.get();
        if (!conv.getBuyerId().equals(userId) && !conv.getSellerId().equals(userId)) {
            return Response.error("شما دسترسی به این گفت‌وگو ندارید");
        }

        return Response.success("گفت‌وگو یافت شد", conv);
    }
}