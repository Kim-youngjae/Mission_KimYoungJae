package com.ll.gramgram.boundedContext.instaMember.entity;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString
@Entity
@Getter
public class InstaMember {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @CreatedDate
    private LocalDateTime createDate;
    @LastModifiedDate
    private LocalDateTime modifyDate;
    @Column(unique = true)
    private String username;
    @Setter
    private String gender;

    // 하나의 멤버가 여러개의 likeablePerson을 가질 수 있음
    @OneToMany(mappedBy = "fromInstaMember", cascade = {CascadeType.ALL})
    @OrderBy("id desc") // 정렬; 데이터를 가져올 때에 id 내림차순 정렬로 가져올 수 있도록 id는 fromLikeablePerson의 id
    @LazyCollection(LazyCollectionOption.EXTRA) // fromLikeablePerson의 사이즈를 구할 때에 select count가 실행되도록 해줌
    @Builder.Default // @Builder가 있으면 = new ArrayList<>();가 동작하지 않는다. 이걸 붙여줘야 함
    private List<LikeablePerson> fromLikeablePeople = new ArrayList<>(); // from(나로부터) 좋아한다고 한 사람들

    @OneToMany(mappedBy = "toInstaMember", cascade = {CascadeType.ALL})
    @OrderBy("id desc") // 정렬; 데이터를 가져올 때에 id 내림차순 정렬로 가져올 수 있도록 id는 fromLikeablePerson의 id
    @LazyCollection(LazyCollectionOption.EXTRA) // fromLikeablePerson의 사이즈를 구할 때에 select count가 실행되도록 해줌
    @Builder.Default // @Builder가 있으면 = new ArrayList<>();가 동작하지 않는다. 이걸 붙여줘야 함
    private List<LikeablePerson> toLikeablePeople = new ArrayList<>(); // from(나로부터) 좋아한다고 한 사람들

    public void addFromLikeablePerson(LikeablePerson likeablePerson) {
        fromLikeablePeople.add(0, likeablePerson); // 역순 정렬이라 최신이 앞으로 와야함
    }

    public void addToLikeablePerson(LikeablePerson likeablePerson) {
        toLikeablePeople.add(0, likeablePerson);
    }
}
