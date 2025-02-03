package org.zerock.mallapi.domain;


import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "memberRoleList")
public class Member {

    @Id
    private String email;
    private String pw;
    private String nickname;
    private boolean social;

    // 계정 권한 저장되어 있는 enum
    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<>();

    // 역할 추가
    public void addRole(MemberRole memberRole){
        memberRoleList.add(memberRole);
    }
    
    // 역할 전부 회수
    public void ClearRole(){
        memberRoleList.clear();
    }

    // 데이터 수정
    public void changeNickname(String nickname){
        this.nickname = nickname;
    }
    public void changePw(String pw){
        this.pw = pw;
    }
    public void changeSocial(boolean social){
        this.social = social;
    }
}
