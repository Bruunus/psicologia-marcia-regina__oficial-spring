package br.com.psicologia.marcia.service.analytics;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class StatusCapturingResponseWrapper extends HttpServletResponseWrapper {
	
	 private int httpStatus = SC_OK;

	    public StatusCapturingResponseWrapper(HttpServletResponse response) {
	        super(response);
	    }

	    @Override
	    public void sendError(int sc) throws IOException {
	        this.httpStatus = sc;
	        super.sendError(sc);
	    }

	    @Override
	    public void sendError(int sc, String msg) throws IOException {
	        this.httpStatus = sc;
	        super.sendError(sc, msg);
	    }

	    @Override
	    public void setStatus(int sc) {
	        this.httpStatus = sc;
	        super.setStatus(sc);
	    }

	    @Override
	    public int getStatus() {
	        return this.httpStatus;
	    }

}
