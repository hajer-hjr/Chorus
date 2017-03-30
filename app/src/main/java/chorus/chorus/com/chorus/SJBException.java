package chorus.chorus.com.chorus;


public class SJBException extends Exception {
    private String fieldName;
    private String errorCode;

    public SJBException(String message) {
        super(message);
    }

    public SJBException(String errorCode, String fieldName) {
        super("FIELD_ERROR");
        this.errorCode = errorCode;
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
