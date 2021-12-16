package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            if (isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = httpRequest.getSession(false);
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("미 인증 사용자 요청 {}", requestURI);
                    // 미 인증 사용자이므로 다시 로그인 페이지로 redirect
                    // sendRedirect 메서드는 응답 요청에 얘가 다음에 어디로 리다이렉트해야하는지를 넣어주는 것.
                    // 따라서 이 뒤에 나오는 명령도 실행되니 주의해야 함.
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);

                    return;
                }
            }

            // 인증이 필요없는 사용자는 다음 필터를 적용하거나 서블릿을 호출
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e; // 예외를 로깅하거나 처리하는 것도 가능하지만, 톰캣의 서블릿 컨테이너까지 올려줘야 함. 그래서 그냥 보냄
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }
    /**
     * 화이트 리스트의 경우엔 인증을 검사하지 않음. 이 여부를 판별해줄 함수
     * 즉, 매개변수로 받는 URI를 로그인 인증을 해야 하냐 말아야 하냐를 결정해줘야 함
     * whitelist(로그인 인증 필요없는 URI) 에 들어 있으면 체크하지 않아도 되고 들어있지 않으면 체크해줘야 함.
     * 그러니 들어있지 않으면 isLoginPath의 값은 참이 되어야 함.
     */
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
