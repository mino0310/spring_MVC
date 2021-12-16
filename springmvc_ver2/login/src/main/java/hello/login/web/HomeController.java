package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

//    @GetMapping("/")
    public String home() {
        return "home";
    }

//    @GetMapping("/")
    public String homeLogin(
            @CookieValue(name = "memberId", required = false) Long memberId, Model model
    ) {
        // 해당하는 쿠키가 없다는 이야기 - 처음 로그인이거나 쿠키 만료로 인해 다시 로그인 해야 함.
        if (memberId == null) {
            return "home";
        }

        // 쿠키가 있긴 하다는 이야기
        // 로그인
        Member loginMember = memberRepository.findById(memberId);

        // 해당 쿠키로 값을 찾았는데 널이라는 이야기. 쿠키가 유효하지 않다는 말인가?
        // 쿠키가 너무 옛날에 만들어진 경우에 이렇게 된다고 함.
        if (loginMember == null) {
            return "home";
        }

        // 쿠키가 있고, 쿠키로 서버에서 값을 찾을 수도 있다는 이야기 == 로그인 된 것 확인.
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {
        // 세션 관리자에 저장된 회원 정보 조회
        Member member = (Member) sessionManager.getSession(request);

        // 로그인
        if (member == null) {
            return "home";
        }

        // 쿠키가 있고, 쿠키로 서버에서 값을 찾을 수도 있다는 이야기 == 로그인 된 것 확인.
        model.addAttribute("member", member);
        return "loginHome";
    }

    @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {

        // 세션이 없으면 home
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "home";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // 세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        // 쿠키가 있고, 쿠키로 서버에서 값을 찾을 수도 있다는 이야기 == 로그인 된 것 확인.
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

}