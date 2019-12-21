package burp;

import wooyun.GUI;
import wooyun.Menu;

import javax.swing.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class BurpExtender implements IBurpExtender, IContextMenuFactory {
    public PrintWriter stdout;
    public PrintWriter stderr;
    public IExtensionHelpers helpers;
    public IBurpExtenderCallbacks cbs;
    public IContextMenuInvocation context;


    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        String pluginName = "From wooyun search";

        callbacks.setExtensionName(pluginName);
        this.helpers = callbacks.getHelpers();
        this.cbs = callbacks;
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stderr = new PrintWriter(callbacks.getStderr(), true);
        this.stdout.println("hello burp!");

        callbacks.registerContextMenuFactory(this);// for menus
    }


    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        ArrayList<JMenuItem> menu_list = new ArrayList<JMenuItem>();
        this.context = invocation;
        menu_list.add(new Menu(this));

        return menu_list;
    }
}
