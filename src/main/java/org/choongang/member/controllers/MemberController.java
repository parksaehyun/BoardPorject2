package org.choongang.member.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.choongang.board.repositories.BoardRepository;
import org.choongang.global.exceptions.ExceptionProcessor;
import org.choongang.member.MemberUtil;
import org.choongang.member.services.MemberSaveService;
import org.choongang.member.validators.JoinValidator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@SessionAttributes("requestLogin")
public class MemberController implements ExceptionProcessor {

    private final JoinValidator joinValidator;
    private final MemberSaveService memberSaveService;
    private final MemberUtil memberUtil;
    private final BoardRepository boardRepository;

    @ModelAttribute
    public RequestLogin getRequestLogin() {
        return new RequestLogin();
    }

    @GetMapping("/join")
    public String join(@ModelAttribute RequestJoin form) {
        return "front/member/join"; // pc뷰
    }

    @PostMapping("/join")
    public String joinPs(@Valid RequestJoin form, Errors errors) {

        joinValidator.validate(form, errors);

        if (errors.hasErrors()) {
            return "front/member/join";
        }

        memberSaveService.save(form); // 회원 가입 처리

        return "redirect:/member/login";
    }

    // 로그인은 뷰만 구성하면됨(겟매핑) // 처리는 시큐리티가 해줌(겟포스트 안써도 됨)
    @GetMapping("/login")
    public String login(@Valid @ModelAttribute RequestLogin form, Errors errors) {
        String code = form.getCode();
        if (StringUtils.hasText(code)) {
            errors.reject(code, form.getDefaultMessage());

            // 비번 만료인 경우 비번 재설정 페이지 이동
            if (code.equals("CredentialsExpired.Login")) {
                return "redirect:/member/password/reset";
            }
        }

        return "front/member/login"; // pc뷰
    }

    @ResponseBody
    @GetMapping("/test1")
    @PreAuthorize("isAuthenticated()")
    public void test1() {
        log.info("test1 - 회원만 접근 가능");
    }

    @ResponseBody
    @GetMapping("/test2")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void test2() {
        log.info("test2 - 관리자만 접근 가능");
    }

    /*
    // 로그인한 회원의 정보 가져오기

    @ResponseBody
    @GetMapping("/test")
    public void test(Principal principal) {
        log.info("로그인 아이디: {}", principal.getName());
    }

    @ResponseBody
    @GetMapping("/test2")
    public void test2(@AuthenticationPrincipal MemberInfo memberInfo) {
        log.info("로그인 회원: {}", memberInfo.toString());
    }

    @ResponseBody
    @GetMapping("/test3")
    public void test3() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // getContext() : SecurityContextHolder객체를 가져오기
        // getAuthentication() : LoginSuccessHandler의 3번째 매개변수, 로그인 정보

        log.info("로그인 상태: {}", authentication.isAuthenticated());
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof MemberInfo) { // 로그인 상태 - UserDetails 구현체(getPrincipal())
            ;
            MemberInfo memberInfo = (MemberInfo) authentication.getPrincipal();
            log.info("로그인 회원: {}", memberInfo.toString());
        } else { // 미로그인 상태 - String / anonymousUser (getPrincipal())
            log.info("getPrincipal(): {}", authentication.getPrincipal());
        }
    }
     */
    /*
    @ResponseBody
    @GetMapping("/test4")
    public void test4() {
        log.info("로그인 여부 : {}", memberUtil.isLogin());
        log.info("로그인 회원: {}", memberUtil.getMember());
    }

     */
//    /*
//    @ResponseBody
//    @GetMapping("/test5")
//    public void test5() {
//
//        Board board = Board.builder()
//                .bId("freetalk")
//                .bName("자유게시판")
//                .build();
//
//        boardRepository.saveAndFlush(board);
//
//        /*
//        Board board = boardRepository.findById("freetalk").orElse(null); // 가져온 상태 = 영속성안에 있는 상태, 조회 = 영속성콘텍스트안에 있는것을 조회
//        board.setBName("(수정)자유게시판"); // 영속성 콘텍스트안에 있는 엔티티를 수정하면 -> save시 insert가 아닌 update쿼리를 수행
//        boardRepository.saveAndFlush(board); // 값 저장이 아님!!!, 값 수정
//         */
//
//    */
}
