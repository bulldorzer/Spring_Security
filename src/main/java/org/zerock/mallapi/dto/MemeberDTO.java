package org.zerock.mallapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Getter @Setter
@ToString
public class MemeberDTO extends User {
    /*
        Spring Security에서 제공하는 기본 사용자 객체
        사용자 인증과 권한을 관리하는데 사용
        User클래스 역할 사용자의 로그인 정보와 권한을 저장하는 역할
        UserDetail 인터페이스를 상속받아 구현한 클래스임 
    */
    private String email;
    private String pw;
    private String nickname;
    private boolean social;

    private List<String> roleNames = new ArrayList<>();

    public MemeberDTO(String email,String pw, String nickname, boolean social,
                      List<String> roleNames){

        // 부모 생성자 호출
        // SimpleGrantedAuthority : 스프링 시큐리티에서 사용자의 권한을 표현하는 클래스
        // 각 권한 앞에 "ROLE_" 글자 붙여서 객체 생성 -> List 형태로 변환
        super(
                email, pw,
                roleNames.stream()
                        .map( str -> new SimpleGrantedAuthority("ROLE_"+str))
                        .collect(Collectors.toList())
        );

        this.email=email;
        this.pw=pw;
        this.nickname=nickname;
        this.social=social;
        this.roleNames=roleNames;




    }

    /*
        사용자의 정보를 Mqp형태로 변환하여
        JWT 토큰 생성 등에 사용
    */
    public Map<String,Object> getClaims(){

        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("email",email);
        dataMap.put("pw",pw);
        dataMap.put("nickname",nickname);
        dataMap.put("social",social);
        dataMap.put("roleNames",roleNames);

        return dataMap;

    }



}
