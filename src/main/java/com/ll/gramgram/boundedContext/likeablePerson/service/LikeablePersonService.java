package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if (member.hasConnectedInstaMember() == false) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        // 동일한 인스타아이디, 동일한 호감코드로 등록하려고 하면 등록거부
        RsData<LikeablePerson> addRs = canAdd(member, username, attractiveTypeCode);
        if (addRs != null) {
            return addRs;
        }
        // TODO: null이 반환될 때 수행될 로직 구현

        InstaMember fromInstaMember = member.getInstaMember();
        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장

        // 내가 누구를 좋아한다는 호감표시를 추가
        fromInstaMember.addFromLikeablePerson(likeablePerson);
        // 누군가가 나를 좋아하는 호감표시를 추가
        toInstaMember.addToLikeablePerson(likeablePerson);

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    public RsData canAdd(Member member, String username, int attractiveTypeCode) {
        List<LikeablePerson> fromLikeablePeople = member.getInstaMember().getFromLikeablePeople();

        if (fromLikeablePeople.size() == 10) {
            return RsData.of("F-3", "더 이상 등록이 되지 않습니다. <br>목록에서 제거 후 등록해주세요");
        }

        for (LikeablePerson likeablePerson : fromLikeablePeople) {
            if (likeablePerson.getToInstaMemberUsername().equals(username)) {
                if (likeablePerson.getAttractiveTypeCode() == attractiveTypeCode) {
                    return RsData.of("F-4", "이미 호감상대가 등록되어 있습니다 <br>(동일한 호감코드)");
                } else {
                    // 인스타 아이디는 같은데 다른 호감코드로 등록했을 때의 경우
                    String originalAttractiveTypeDisplayName = likeablePerson.getAttractiveTypeDisplayName();
                    String convertedAttractiveTypeDisplayName = convertAttractiveTypeCode(attractiveTypeCode);

                    modifyAttractiveTypeCode(likeablePerson, attractiveTypeCode);

                    return RsData.of("S-2", "%s 에 대한 호감사유를 %s에서 %s으로 변경합니다.".formatted(username, originalAttractiveTypeDisplayName, convertedAttractiveTypeDisplayName));
                }
            }
        }
        return null;
    }

    //실제 호감 코드가 수정되는 부분
    @Transactional
    public void modifyAttractiveTypeCode(LikeablePerson likeablePerson, int attractiveTypeCode) {
        LikeablePerson modifiedPersonData = likeablePerson.toBuilder()
                .attractiveTypeCode(attractiveTypeCode)
                .build();
        this.likeablePersonRepository.save(modifiedPersonData);
    }

    private static String convertAttractiveTypeCode(int attractiveTypeCode) {
        return switch (attractiveTypeCode) {
            case 1 -> "외모";
            case 2 -> "성격";
            default -> "능력";
        };
    }

    public Optional<LikeablePerson> getLikeablePerson(Long id) {
        return this.likeablePersonRepository.findById(id);
    }

    @Transactional
    public RsData delete(LikeablePerson likeablePerson) {
        this.likeablePersonRepository.delete(likeablePerson);

        return RsData.of("S-1", "선택하신 인스타유저(%s)을/를 호감목록에서 삭제 되었습니다.".formatted(likeablePerson.getToInstaMember().getUsername()));
    }

    public RsData canActorDelete(Member actor, LikeablePerson likeablePerson) {
        if (likeablePerson == null) return RsData.of("F-1", "이미 삭제되었습니다.");

        // 수행자의 인스타계정 번호
        long actorInstaMemberId = actor.getInstaMember().getId();
        // 삭제 대상의 작성자(호감표시한 사람)의 인스타계정 번호
        long fromInstaMemberId = likeablePerson.getFromInstaMember().getId();

        if (actorInstaMemberId != fromInstaMemberId)
            return RsData.of("F-2", "권한이 없습니다.");

        return RsData.of("S-1", "삭제가능합니다.");
    }
}
