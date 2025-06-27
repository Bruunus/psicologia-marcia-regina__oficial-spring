package br.com.psicologia.marcia.service.analytics;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class SuporteAnalytics {

	

	public String capturarIpCliente(HttpServletRequest request) {
	        // Lista de cabeçalhos comuns usados por proxies para repassar o IP original
	        String[] headers = {
	            "X-Forwarded-For",           // padrão de proxies
	            "Proxy-Client-IP",           // Apache
	            "WL-Proxy-Client-IP",        // WebLogic
	            "HTTP_X_FORWARDED_FOR",      // proxies diversos
	            "HTTP_X_FORWARDED",          // variantes
	            "HTTP_X_CLUSTER_CLIENT_IP",  // AWS ELB
	            "HTTP_CLIENT_IP",
	            "HTTP_FORWARDED_FOR",
	            "HTTP_FORWARDED",
	            "HTTP_VIA",
	            "REMOTE_ADDR"
	        };

	        for (String header : headers) {
	            String ip = request.getHeader(header);
	            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
	                // Se o cabeçalho contiver vários IPs, pega o primeiro (cliente real)
	                return ip.split(",")[0].trim();
	            }
	        }

	        // Fallback: IP direto da requisição
	        String ip = request.getRemoteAddr();
	        

	        // Conversão de loopback IPv6 para IPv4 (localhost)
	        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
	            ip = "127.0.0.1";
	        }

	        return ip;
	    }
	
	
	
	public String mapearObjetivo(String endpoint) {
	    return switch (endpoint) {
	        case "/auth/login" -> "User login";
	        case "/auth/deslogar" -> "Logout user";
	        case "/edit/user/deletar" -> "Delete user";
	        case "/paciente/criar" -> "Create patient";
	        // você pode ir adicionando conforme for usando
	        default -> "Generic request";
	    };
	}

	
	
	
}
