package wooyun;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class GUI {
    public GUI(TableStruct t) {
        TabbedPaneFrame jf = new TabbedPaneFrame(t);    //创建一个JFrame对象
        jf.setTitle("WooYun 搜索结果");
        jf.setSize(650, 480);
        jf.setLocationRelativeTo(null);
//        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);    //设置窗口可见
    }
}

class TabbedPaneFrame extends JFrame {


    private JTabbedPane tabbedPane;

    private int count = 0;
    private TableStruct struct;


    public TabbedPaneFrame(TableStruct t) {
        this.struct = t;
        // 添加选项卡

        tabbedPane = new JTabbedPane();
        for (String s : struct.GetNameSets()) {
            tabbedPane.addTab(s, null);
        }
        // 添加选项卡面板
        add(tabbedPane, "Center");
        // 添加监听器
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override

            public void stateChanged(ChangeEvent e) {

                // TODO Auto-generated method stub

                int n = tabbedPane.getSelectedIndex();

                loadTab(n);

            }

        });

        loadTab(0);

    }

    private static String convertToMultiline(String orig) {
        return "<html>" + orig.replaceAll("\n", "<br>");
    }


    private void loadTab(int n) {

        String title = tabbedPane.getTitleAt(n);

        Object[][] tableDate = struct.GetRowDatasFromTabName(title);
        Object[] name = struct.GetColumnNamesFromTabName(title);
        JTable table = new JTable(tableDate, name);

        JPanel jp = new JPanel();    //创建一个JPanel对象

        JTableHeader head = table.getTableHeader(); // 创建表格标题对象
        head.setPreferredSize(new Dimension(head.getWidth(), 20));// 设置表头大小

        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        TableColumnModel tc = table.getColumnModel();
        tc.getColumn(0).setPreferredWidth(10);
        table.setRowHeight(22);

        String ret = struct.GetExtraInfo(title);
        JLabel jl = new JLabel(convertToMultiline(ret));    //创建一个标签

        jp.setLayout(new BorderLayout());
        jp.add(jl, "North");
        jp.add(new JScrollPane(table), "Center");

        tabbedPane.setComponentAt(n, jp);

    }

}
