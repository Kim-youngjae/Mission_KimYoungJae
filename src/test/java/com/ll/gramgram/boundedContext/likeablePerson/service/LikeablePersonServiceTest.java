package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class LikeablePersonServiceTest {
    @Autowired
    LikeablePersonService likeablePersonService;
    @Autowired
    LikeablePersonRepository likeablePersonRepository;

    @Test
    @DisplayName("_ 활용해서 검색하기")
    void t001() throws Exception {
        // 좋아하는 대상의 ID가
        List<LikeablePerson> likeablePeople = likeablePersonRepository.findByToInstaMemberId_username("insta_user3");

        System.out.println("likeablePeople = " + likeablePeople);

//        LikeablePerson likeablePerson = likeablePersonRepository.findByFromInstaMemberIdAndToInstaMember_username(2L, "insta_user4");
//        System.out.println("likeablePerson = " + likeablePerson);
//
//        Long id = likeablePerson.getToInstaMember().getId();
//        System.out.println("id = " + id);

    }
}