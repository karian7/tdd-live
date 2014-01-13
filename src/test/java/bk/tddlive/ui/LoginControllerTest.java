package bk.tddlive.ui;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import bk.tddlive.security.AuthService;
import bk.tddlive.security.Authentication;

public class LoginControllerTest {

    public static final String NO_USER_ID = "nouserid";
    public static final String PASSWORD = "password";
    public static final String USER_ID = "userid";
    public static final String WRONG_PASSWORD = "wrongPassword";

    private LoginController loginController;

    private AuthService mockAuthService;
    private MockHttpServletResponse mockResponse;

    @Before
    public void setUp() throws Exception {
        mockAuthService = mock(AuthService.class);
        loginController = new LoginController(mockAuthService);
        mockResponse = new MockHttpServletResponse();
    }

    //    ■ 폼 요청 처리 (쉬움)
    @Test
    public void whenRequestIsForm_returnFormView() {
        String viewName = loginController.form();
        assertFormView(viewName);
    }

    //    ■ 폼 전송 시, LoginCommand 값 이상, 폼 뷰 리턴 (비정상)
    @Test
    public void whenInvalidLoginCommand_returnFormView() throws Exception {
        assertFormViewWhenInvalidLoginCommand(null, PASSWORD);
        assertFormViewWhenInvalidLoginCommand("", PASSWORD);
        assertFormViewWhenInvalidLoginCommand(USER_ID, null);
        assertFormViewWhenInvalidLoginCommand(USER_ID, "");
    }

    //    ■ 폼 전송 시, ID/PW 불일치(사용자 없음), 폼 뷰 리턴 (비정상)
    @Test
    public void whenNonExistingUser_returnFormView() throws Exception {
        when(mockAuthService.authenticate(NO_USER_ID, PASSWORD)).thenThrow(new AuthService.NonExistingUserException());
        assertFormViewWhenIdOrPwNotMatch(NO_USER_ID, PASSWORD);
    }

    //    ■ 폼 전송 시, ID/PW 불일치(잘못된 암호), 폼 뷰 리턴 (비정상)
    @Test
    public void whenWrongPassword_returnFormView() throws Exception {
        when(mockAuthService.authenticate(USER_ID, WRONG_PASSWORD)).thenThrow(new AuthService.WrongPasswordException());
        assertFormViewWhenIdOrPwNotMatch(USER_ID, WRONG_PASSWORD);
    }

    //    ■ 폼 전송 시, ID/PW 일치, 성공 뷰 리턴 (정상) ● 쿠키 생성 확인
    @Test
    public void whenIdPwMatching_returnSuccessView() throws Exception {
        when(mockAuthService.authenticate(USER_ID, PASSWORD)).thenReturn(new Authentication(USER_ID));
        String viewName = runSubmit(USER_ID, PASSWORD);
        assertThat(viewName, equalTo(LoginController.SUCCESS_VIEW));

        assertThat(mockResponse.getCookie("AUTH").getValue(), equalTo(USER_ID));
    }

    private void assertFormView(String viewName) {
        assertThat(viewName, equalTo(LoginController.FORM_VIEW));
    }

    private void assertFormViewWhenInvalidLoginCommand(String id, String password) {
        String viewName = runSubmit(id, password);
        assertFormView(viewName);
    }

    private String runSubmit(String userId, String password) {
        LoginCommand loginCommand = createSpiedLoginCommand(userId, password);
        String viewName = loginController.submit(loginCommand, mockResponse);
        verify(loginCommand).validate();
        return viewName;
    }

    private LoginCommand createSpiedLoginCommand(String id, String password) {
        LoginCommand loginCommand = new LoginCommand();
        loginCommand.setId(id);
        loginCommand.setPassword(password);
        return spy(loginCommand);
    }

    private void assertFormViewWhenIdOrPwNotMatch(String userId, String password) {
        String viewName = runSubmit(userId, password);
        assertFormView(viewName);
        verify(mockAuthService).authenticate(userId, password);
    }
}
