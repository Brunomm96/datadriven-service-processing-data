package br.com.datawake.datadrivenserviceprocessingdata.domain.exception;

public class DomainException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable reason) {
        super(message, reason);
    }

}