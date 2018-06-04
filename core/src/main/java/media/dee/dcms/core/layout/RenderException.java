package media.dee.dcms.core.layout;

public abstract class RenderException extends Throwable{
    public RenderException(String message){
        super(message);
    }

    public RenderException(String message, Throwable throwable){
        super(message, throwable);
    }

    public RenderException(Throwable throwable){
        super(throwable);
    }
}
