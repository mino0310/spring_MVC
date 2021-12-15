package hello.login.web.session;

import hello.login.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.*;

class SessionManagerTest {

    SessionManager sessionManager = new SessionManager();

    // 서버와 클라이언트의 통신과정을 가상화해서 시뮬레이션 해보는 것.
    @Test
    void sessionTest() {

        // 세션 생성 (서버 -> 클라이언트) 값으로 세션id생성하고 쿠키까지 넣어주는 과정.
        MockHttpServletResponse response = new MockHttpServletResponse();
        Member member = new Member();
        sessionManager.createSession(member, response);

        // 요청에 응답 쿠키 저장 (클라이언트 -> 서버)
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(response.getCookies()); // 여기서 response는 서버로부터 받은 쿠키값들이겠지?

        // 세션 조회 (서버 -> 클라이언트) 다시 클라이언트에서 서버로 조회 요청이 온 상황임.
        Object result = sessionManager.getSession(request);
        assertThat(result).isEqualTo(member);

        // 세션 만료
        sessionManager.expire(request);
        Object expired = sessionManager.getSession(request);
        assertThat(expired).isNull();
    }
}