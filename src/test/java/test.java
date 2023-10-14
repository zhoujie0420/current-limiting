import org.jiezhou.limit.limiter.Limit;

public class test {
    @Limit(limit = 2,time = 1,key = "1",msg = "error")
    public void test(){
        return;
    }
}
