package br.com.datawake.datadrivenserviceprocessingdata.domain.exception;

public class UploadsCsvNotReadException extends EntityNotFoundException {

    private static final long serialVersionUID = 1L;

    public UploadsCsvNotReadException(String msg) {
        super(msg);
    }

}
