import com.hmall.cart.CartApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CartApplication.class)
public class test {
    @Value("${hm.db.database}")
    private String database;
    @Test
    public void test1(){
        // 测试代码
        System.out.println(database);
    }
}
