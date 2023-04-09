package com.ll.gramgram.boundedContext.likeablePerson.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/likeablePerson")
@RequiredArgsConstructor
public class LikeablePersonController {
    private final Rq rq;
    private final LikeablePersonService likeablePersonService;

    @GetMapping("/add")
    public String showAdd() {
        return "usr/likeablePerson/add";
    }

    @AllArgsConstructor
    @Getter
    public static class AddForm {
        private final String username;
        private final int attractiveTypeCode;
    }

    @PostMapping("/add")
    public String add(@Valid AddForm addForm) {
        RsData<LikeablePerson> createRsData = likeablePersonService.like(rq.getMember(), addForm.getUsername(), addForm.getAttractiveTypeCode());

        if (createRsData.isFail()) {
            return rq.historyBack(createRsData);
        }

        return rq.redirectWithMsg("/likeablePerson/list", createRsData);
    }

    @GetMapping("/list")
    public String showList(Model model) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            // 해당 instaMember 가 좋아한다고 한 사람들의 목록
            List<LikeablePerson> likeablePeople = instaMember.getFromLikeablePeople();
            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/list";
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        // TODO: 삭제를 처리하기 전에 해당 항목에 대한 소유권이 본인(로그인한 사람)에게 있는지 체크해야 한다.
        LikeablePerson likeablePerson = likeablePersonService.getLikeablePerson(id).orElse(null); // likeablePerson 테이블에 삭제 가능한 행 정보가 있는지 조회

        RsData canActorDeleteRsData = likeablePersonService.canActorDelete(rq.getMember(), likeablePerson); // 삭제가 가능한지 서비스로 요청 없으면 null값이 들어감

        if (canActorDeleteRsData.isFail()) return rq.historyBack(canActorDeleteRsData); // 서비스에서의 삭제 판단 메서드 반환값이 isFail()이면 뒤로 돌아감

        RsData deleteRsData = likeablePersonService.delete(likeablePerson); // 가능해서 삭제가능 하면 서비스에 삭제 요청

        if (deleteRsData.isFail()) {
            return rq.historyBack(deleteRsData); // id가 없어서 삭제가 실패할 경우 뒤로 원복
        }

        return rq.redirectWithMsg("/likeablePerson/list", deleteRsData); // 모든 과정이 성공적으로 이루어지면 redirect
    }
}
