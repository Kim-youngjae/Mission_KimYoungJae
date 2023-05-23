package com.ll.gramgram.boundedContext.notification.eventListener;

import com.ll.gramgram.base.event.EventAfterLike;
import com.ll.gramgram.base.event.EventAfterModifyAttractiveType;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationEventListener {
    private final NotificationService notificationService;

    @EventListener
    public void listen(EventAfterLike event) {
        // 누군가가 호감 표시를 했을 때
        LikeablePerson likeablePerson = event.getLikeablePerson(); // 좋아요가 발생하면 publisher 로 이벤트 발생 및 likeablePerson 을 매개변수로 넘겨줌
        notificationService.makeLike(likeablePerson); // 가져온 likeablePerson 을 notificationService 에서 처리
    }

    @EventListener
    public void listen(EventAfterModifyAttractiveType event) {
        // 누군가의 호감 사유가 변경 되었을 때
        LikeablePerson likeablePerson = event.getLikeablePerson();
        notificationService.makeModifyAttractive(likeablePerson, event.getOldAttractiveTypeCode());
    }
}
