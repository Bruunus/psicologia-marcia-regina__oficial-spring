package br.com.psicologia.marcia.security;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@WebFilter("/*") //caminha obrigatório para todas as URLs
@Order(Ordered.HIGHEST_PRECEDENCE) //ordem de execução prioritária de todos os filtros
public class CorsFilter implements Filter{
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Filter.super.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		String originHeader = ((HttpServletRequest) request).getHeader("Origin");
		
		if (originHeader != null && (
	            originHeader.equals("http://psicologia-marcia.brunofernandes.net.br") ||
	            originHeader.equals("http://localhost:8080") ||		// fase de dev
	            originHeader.equals("http://localhost:4200")  // fase de dev		
	            // adicionar mais caso precise
	        )) {
			response.setHeader("Access-Control-Allow-Origin", originHeader);
	    }
		
//		response.setHeader("Access-Control-Allow-Origin",
//			    "http://psicologia-marcia.brunofernandes.net.br," + // fase de prod
//			    "http://localhost:8080," + // fase de dev
//			    "http://localhost:4200"
//			);
		
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
		response.setHeader("Access-Control-Max-Age", "3600");	//tempo de resposta do cabeçalho armaz em cache
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");// tipos de cabeçalhos permitidos
		
		if (HttpMethod.OPTIONS.name().equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, resp);
        }		
		
		
	}
	
	@Override
	public void destroy() {}
	

}
