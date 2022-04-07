package tabby;

import org.junit.jupiter.api.Test;
import soot.SootMethod;
import tabby.util.SemanticHelper;

/**
 * @Author: r00t4dm
 * @Date: 2022/4/7 7:28 下午
 */
public class SemanticHelperTest {


    @Test
    public void getFieldNameByMethodNameTest () {

        String result = SemanticHelper.getFieldNameByMethodName("getS", null);
        String result2 = SemanticHelper.getFieldNameByMethodName("setS", null);
        assert result != null;
        assert result2 != null;

    }
}
