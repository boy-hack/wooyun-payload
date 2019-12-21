import wooyun.GUI;
import wooyun.TableStruct;
import wooyun.Wooyun;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class TestWooyun {
    public static void main(String[] args) throws IOException {
        Wooyun w = new Wooyun();
        TableStruct ret = w.Search(new URL("https://zone.wooyun.org/xxxa/admin.php?id=1"), new String[]{"s", "port", "username", "data"}, new String[]{"get", "id"}, new String[]{"get", "id"});
//        System.out.println(Arrays.deepToString(ret.GetRowDatasFromTabName("Post Params")));
        GUI g = new GUI(ret);
//        System.out.println(Arrays.toString(ret.GetNameSets()));
    }
}
