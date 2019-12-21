package wooyun;

import java.util.HashMap;
import java.util.Map;

public class TableStruct {
    private Map<String, Object[]> ColumnNames;
    private Map<String, Object[][]> RowDatas;
    private Map<String, String> ExtraInfo;

    TableStruct() {
        ColumnNames = new HashMap<String, Object[]>();
        RowDatas = new HashMap<String, Object[][]>();
        ExtraInfo = new HashMap<String, String>();
    }

    public void SetExtraInfo(String name, String info) {
        this.ExtraInfo.put(name, info);
    }

    public String GetExtraInfo(String name) {
        return this.ExtraInfo.get(name);
    }

    public String[] GetNameSets() {
        return RowDatas.keySet().toArray(new String[0]);
    }

    public void AddColumnNames(String name, Object[] obj) {
        this.ColumnNames.put(name, obj);
    }

    public void AddRowDatas(String name, Object[][] obj) {
        this.RowDatas.put(name, obj);
    }

    public Object[] GetColumnNamesFromTabName(String name) {
        return this.ColumnNames.get(name);
    }

    public Object[][] GetRowDatasFromTabName(String name) {
        return this.RowDatas.get(name);
    }

}
