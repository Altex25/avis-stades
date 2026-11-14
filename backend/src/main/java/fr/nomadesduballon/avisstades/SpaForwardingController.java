package fr.nomadesduballon.avisstades;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the PWA entry page for its client-side routes, so that a deep link or a page refresh does not end in a 404.
 */
@Controller
class SpaForwardingController {

	@GetMapping({ "/{path:^(?!api$|actuator$)[^.]*}", "/{path:^(?!api$|actuator$)[^.]*}/{*rest}" })
	String forward() {
		return "forward:/index.html";
	}

}
