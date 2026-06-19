package bluegreen.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static bluegreen.model.Constant.BASE_API_METHOD;

public abstract class AbstractBlueGreenController {

    @Value("${display_color}")
    protected String color;

    @Value("${git.commit.id.abbrev}")
    protected String commitId;

    @Value("${git.commit.time}")
    protected String commitTime;

    @GetMapping(BASE_API_METHOD)
    public String page(final Model model) throws UnknownHostException {
        model.addAttribute("color", this.color);
        model.addAttribute("commitId", this.commitId);
        model.addAttribute("commitTime", this.commitTime);
        final String hostAddress = InetAddress.getLocalHost().getHostAddress();
        model.addAttribute("serverAddress", hostAddress);
        return this.getView();
    }

    protected String getView() {
        return "page";
    }
}
