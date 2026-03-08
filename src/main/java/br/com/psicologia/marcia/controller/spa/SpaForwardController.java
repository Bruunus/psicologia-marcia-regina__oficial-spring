package br.com.psicologia.marcia.controller.spa;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller responsável por encaminhar as rotas da SPA Angular
 * para o arquivo index.html.
 */
@Controller
public class SpaForwardController {

    /**
     * Encaminha rotas da SPA para o Angular.
     *
     * Não intercepta:
     * - /api/**
     * - arquivos estáticos com extensão
     */
    @RequestMapping(value = {
            "/",
            "/{x:[\\w\\-]+}",
            "/{x:^(?!api$).*$}/{y:[\\w\\-]+}",
            "/{x:^(?!api$).*$}/{y:[\\w\\-]+}/{z:[\\w\\-]+}"
    })
    public String forward() {
        return "forward:/index.html";
    }
}