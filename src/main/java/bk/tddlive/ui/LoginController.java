package bk.tddlive.ui;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import bk.tddlive.security.AuthService;
import bk.tddlive.security.Authentication;

@Controller
public class LoginController {

	public static final String FORM_VIEW = "formview";
	public static final String SUCCESS_VIEW = "successview";
	private AuthService authService;

	public LoginController(AuthService authService) {
		this.authService = authService;
	}
	
	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String form() {
		return FORM_VIEW;
	}

	@RequestMapping(value="/login", method = RequestMethod.POST)
	public String submit(LoginCommand loginCommand, HttpServletResponse response) {
		if (!loginCommand.validate())
			return FORM_VIEW;
		try {
			Authentication auth = authenticate(loginCommand);
			sendAuthCookie(response, auth);
			return SUCCESS_VIEW;
		} catch (AuthService.NonExistingUserException  e) {
			return FORM_VIEW;
		} catch (AuthService.WrongPasswordException  e) {
			return FORM_VIEW;
		}
	}

	private Authentication authenticate(LoginCommand loginCommand) {
		return authService.authenticate(loginCommand.getId(), loginCommand.getPassword());
	}

	private void sendAuthCookie(HttpServletResponse response, Authentication auth) {
		response.addCookie(createAuthCookie(auth));
	}

	private Cookie createAuthCookie(Authentication auth) {
		return new Cookie("AUTH", auth.getId());
	}
}
