package com.ll.gramgram.boundedContext.home.controller;

import com.ll.gramgram.base.rq.Rq;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final Rq rq;

    @GetMapping("/")
    public String showMain() {
        return "usr/home/main";
    }

    @GetMapping("/debugSession")
    @ResponseBody
    public String showDebugSession(HttpSession session) {
        StringBuilder sb = new StringBuilder("Session content:\n");

        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = session.getAttribute(attributeName);
            sb.append(String.format("%s: %s\n", attributeName, attributeValue));
        }

        return sb.toString().replaceAll("\n", "<br>");
    }

    @GetMapping("/historyBackTest")
    @PreAuthorize("hasAuthority('admin')")
    public String showHistoryBackTest(HttpSession session) {
        return rq.historyBack("여기는 당신같은 사람이 오면 안돼요.");
    }


    @GetMapping("/toStringTest")
    @ResponseBody
    public String showToStringTest() {
        Teacher teacher = new Teacher();
        teacher.age = 45;
        teacher.name = "David";

        Student student = new Student();
        student.age = 17;
        student.name = "폴";
        student.teacher = teacher; // 이 학생의 담당 선생님 세팅

//        System.out.println(student);

        // 이 선생의 학급 학생 등록
        // 아래 클래스에 @ToString.Exclude 가 없으면 무한참조에 의한 오류발생
        teacher.addStudent(student);

        System.out.println(student);

        return "toStringTest";
    }
}

@ToString
class Teacher {
    @ToString.Exclude // 여기에만 있어도 되고
    public List<Student> students = new ArrayList<>();
    public int age;
    public String name;

    public void addStudent(Student student) {
        students.add(student);
    }
}

@ToString
class Student {
    @ToString.Exclude // 여기에만 있어도 된다.
    public Teacher teacher;
    public int age;
    public String name;
}
