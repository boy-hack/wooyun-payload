package wooyun;

import burp.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Menu extends JMenuItem {//JMenuItem vs. JMenu

    public Menu(BurpExtender burp) {
        this.setText(" 从乌云搜索类似漏洞");
        this.addActionListener(new SearchFromWooyun(burp, burp.context));
    }
}

class SearchFromWooyun implements ActionListener {
    public IContextMenuInvocation invocation;
    public IExtensionHelpers helpers;
    public PrintWriter stdout;
    public PrintWriter stderr;
    public IBurpExtenderCallbacks callbacks;

    public SearchFromWooyun(BurpExtender burp, IContextMenuInvocation context) {
        this.invocation = context;
        this.helpers = burp.helpers;
        this.callbacks = burp.cbs;
        this.stdout = burp.stdout;
        this.stderr = burp.stderr;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        IHttpRequestResponse[] selectedItems = this.invocation.getSelectedMessages();

        byte[] selectedRequest = selectedItems[0].getRequest();

        IHttpService httpService = selectedItems[0].getHttpService();


        //**************获取参数 通过IRequestInfo对象*************************//
        IRequestInfo analyzedRequest = this.helpers.analyzeRequest(selectedRequest);//only get the first
//        String url = analyzedRequest.getUrl().toString();
        String pattern = "(GET|POST) ([^ ]*) HTTP/";
        String line0 = analyzedRequest.getHeaders().get(0);
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(line0);
        String url = "";
        if (m.find()) {
            url = httpService.getProtocol() + "://" + httpService.getHost() + m.group(2);
        } else {
            stderr.println("Host:" + httpService.getHost() + " url匹配失败");
            return;
        }
        List<String> gets = new ArrayList<String>();
        List<String> posts = new ArrayList<String>();
        List<String> cookies = new ArrayList<String>();

        List<IParameter> paraList = analyzedRequest.getParameters();
        for (IParameter para : paraList) {
            byte type = para.getType(); //获取参数的类型
            String key = para.getName(); //获取参数的名称
            String value = para.getValue(); //获取参数的值
            switch (type) {
                case 0:
                    gets.add(key);
                    break;
                case 1:
                    posts.add(key);
                    break;
                case 2:
                    cookies.add(key);
                    break;
            }
        }
//参数共有7种格式，0是URL参数，1是body参数，2是cookie参数，6是json格式参数
        Wooyun w = null;
        try {
            w = new Wooyun();
        } catch (Exception e1) {
            for (StackTraceElement
                    elem : e1.getStackTrace()) {
                this.stderr.println(elem);
            }
            JOptionPane.showMessageDialog(null, "初始化数据库失败", "提示", 1);
            return;
        }
        try {
            TableStruct ret = w.Search(new URL(url), gets.toArray(new String[gets.size()]), cookies.toArray(new String[cookies.size()]), posts.toArray(new String[posts.size()]));
            if (ret.GetNameSets().length == 0) {
                JOptionPane.showMessageDialog(null, "未发现可用参数", "提示", 1);
                return;
            }
            new GUI(ret);
        } catch (Exception e1) {
            for (StackTraceElement
                    elem : e1.getStackTrace()) {
                this.stderr.println(elem);
            }
            this.stderr.println(e1);

        }

    }

}