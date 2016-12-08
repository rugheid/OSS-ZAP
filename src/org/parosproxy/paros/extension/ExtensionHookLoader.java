package org.parosproxy.paros.extension;

import org.apache.log4j.Logger;
import org.parosproxy.paros.CommandLine;
import org.parosproxy.paros.common.AbstractParam;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.core.proxy.ConnectRequestProxyListener;
import org.parosproxy.paros.core.proxy.OverrideMessageProxyListener;
import org.parosproxy.paros.core.proxy.ProxyListener;
import org.parosproxy.paros.core.scanner.Scanner;
import org.parosproxy.paros.core.scanner.ScannerHook;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.view.*;
import org.zaproxy.zap.PersistentConnectionListener;
import org.zaproxy.zap.extension.api.API;
import org.zaproxy.zap.extension.api.ApiImplementor;
import org.zaproxy.zap.model.ContextDataFactory;
import org.zaproxy.zap.view.ContextPanelFactory;
import org.zaproxy.zap.view.SiteMapListener;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ExtensionHookLoader {

    final Map<Extension, ExtensionHook> extensionHooks = new HashMap<Extension, ExtensionHook>();
    private Model model = null;
    private View view = null;

    private static final Logger logger = Logger.getLogger(ExtensionHookLoader.class);

    public ExtensionHookLoader(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    public void hookProxyListener(Proxy proxy) {
        for (ExtensionHook hook : extensionHooks.values()) {
            hookProxyListeners(proxy, hook.getProxyListenerList());
        }
    }

    static void hookProxyListeners(Proxy proxy, List<ProxyListener> listeners) {
        for (ProxyListener listener : listeners) {
            try {
                if (listener != null) {
                    proxy.addProxyListener(listener);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    void removeProxyListener(ExtensionHook hook) {
        Proxy proxy = Control.getSingleton().getProxy();
        List<ProxyListener> listenerList = hook.getProxyListenerList();
        for (ProxyListener listener : listenerList) {
            try {
                if (listener != null) {
                    proxy.removeProxyListener(listener);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void hookOverrideMessageProxyListener(Proxy proxy) {
        for (ExtensionHook hook : extensionHooks.values()) {
            List<OverrideMessageProxyListener> listenerList = hook.getOverrideMessageProxyListenerList();
            for (OverrideMessageProxyListener listener : listenerList) {
                try {
                    if (listener != null) {
                        proxy.addOverrideMessageProxyListener(listener);
                    }

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    void removeOverrideMessageProxyListener(ExtensionHook hook) {
        Proxy proxy = Control.getSingleton().getProxy();
        List<OverrideMessageProxyListener> listenerList = hook.getOverrideMessageProxyListenerList();
        for (OverrideMessageProxyListener listener : listenerList) {
            try {
                if (listener != null) {
                    proxy.removeOverrideMessageProxyListener(listener);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Hooks (adds) the {@code ConnectRequestProxyListener}s of the loaded extensions to the given {@code proxy}.
     * <p>
     * <strong>Note:</strong> even if public this method is expected to be called only by core classes (for example,
     * {@code Control}).
     *
     * @param proxy the local proxy
     * @since 2.5.0
     */
    public void hookConnectRequestProxyListeners(Proxy proxy) {
        for (ExtensionHook hook : extensionHooks.values()) {
            hookConnectRequestProxyListeners(proxy, hook.getConnectRequestProxyListeners());
        }
    }

    static void hookConnectRequestProxyListeners(Proxy proxy, List<ConnectRequestProxyListener> listeners) {
        for (ConnectRequestProxyListener listener : listeners) {
            proxy.addConnectRequestProxyListener(listener);
        }
    }

    void removeConnectRequestProxyListener(ExtensionHook hook) {
        Proxy proxy = Control.getSingleton().getProxy();
        for (ConnectRequestProxyListener listener : hook.getConnectRequestProxyListeners()) {
            proxy.removeConnectRequestProxyListener(listener);
        }
    }

    public void hookPersistentConnectionListener(Proxy proxy) {
        for (ExtensionHook hook : extensionHooks.values()) {
            hookPersistentConnectionListeners(proxy, hook.getPersistentConnectionListener());
        }
    }

    static void hookPersistentConnectionListeners(Proxy proxy, List<PersistentConnectionListener> listeners) {
        for (PersistentConnectionListener listener : listeners) {
            try {
                if (listener != null) {
                    proxy.addPersistentConnectionListener(listener);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    void removePersistentConnectionListener(ExtensionHook hook) {
        Proxy proxy = Control.getSingleton().getProxy();
        List<PersistentConnectionListener> listenerList = hook.getPersistentConnectionListener();
        for (PersistentConnectionListener listener : listenerList) {
            try {
                if (listener != null) {
                    proxy.removePersistentConnectionListener(listener);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }// ZAP: Added support for site map listeners

    public void hookSiteMapListener(SiteMapPanel siteMapPanel) {
        for (ExtensionHook hook : extensionHooks.values()) {
            hookSiteMapListeners(siteMapPanel, hook.getSiteMapListenerList());
        }
    }

    static void hookSiteMapListeners(SiteMapPanel siteMapPanel, List<SiteMapListener> listeners) {
        for (SiteMapListener listener : listeners) {
            try {
                if (listener != null) {
                    siteMapPanel.addSiteMapListener(listener);
                }
            } catch (Exception e) {
                // ZAP: Log the exception
                logger.error(e.getMessage(), e);
            }
        }
    }

    void removeSiteMapListener(ExtensionHook hook) {
        if (view != null) {
            SiteMapPanel siteMapPanel = view.getSiteTreePanel();
            List<SiteMapListener> listenerList = hook.getSiteMapListenerList();
            for (SiteMapListener listener : listenerList) {
                try {
                    if (listener != null) {
                        siteMapPanel.removeSiteMapListener(listener);
                    }

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }// ZAP: method called by the scanner to load all scanner hooks. 

    public void hookScannerHook(Scanner scan) {
        Iterator<ExtensionHook> iter = extensionHooks.values().iterator();
        while (iter.hasNext()) {
            ExtensionHook hook = iter.next();
            List<ScannerHook> scannerHookList = hook.getScannerHookList();

            for (ScannerHook scannerHook : scannerHookList) {
                try {
                    if (hook != null) {
                        scan.addScannerHook(scannerHook);
                    }

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }// ZAP: Added the type argument.

    void addParamPanel(List<AbstractParamPanel> panelList, AbstractParamDialog dialog) {
        String[] ROOT = {};
        for (AbstractParamPanel panel : panelList) {
            try {
                dialog.addParamPanel(ROOT, panel, true);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    void removeParamPanel(List<AbstractParamPanel> panelList, AbstractParamDialog dialog) {
        for (AbstractParamPanel panel : panelList) {
            try {
                dialog.removeParamPanel(panel);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        dialog.revalidate();
    }

    void hookAllExtension(double progressFactor, List<Extension> extensionList) {
        final double factorPerc = progressFactor / extensionList.size();

        for (int i = 0; i < extensionList.size(); i++) {
            try {
                final Extension ext = extensionList.get(i);
                logger.info("Initializing " + ext.getDescription());
                final ExtensionHook extHook = new ExtensionHook(model, view);
                ext.hook(extHook);
                extensionHooks.put(ext, extHook);

                hookContextDataFactories(ext, extHook);
                hookApiImplementors(ext, extHook);

                if (view != null) {
                    EventQueue.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            // no need to hook view if no GUI
                            hookView(ext, view, extHook);
                            hookMenu(view, extHook);
                            view.addSplashScreenLoadingCompletion(factorPerc);
                        }
                    });
                }

                hookOptions(extHook);
                ext.optionsLoaded();

            } catch (Throwable e) {
                // Catch Errors thrown by out of date extensions as well as Exceptions
                logger.error(e.getMessage(), e);
            }
        }
        // Call postInit for all extensions after they have all been initialized
        for (Extension extension: extensionList) {
            try {
                extension.postInit();
            } catch (Throwable e) {
                // Catch Errors thrown by out of date extensions as well as Exceptions
                logger.error(e.getMessage(), e);
            }
        }

        if (view != null) {
            view.getMainFrame().getMainMenuBar().validate();
            view.getMainFrame().validate();
        }
    }

    void hookContextDataFactories(Extension extension, ExtensionHook extHook) {
        for (ContextDataFactory contextDataFactory : extHook.getContextDataFactories()) {
            try {
                model.addContextDataFactory(contextDataFactory);
            } catch (Exception e) {
                logger.error("Error while adding a ContextDataFactory from " + extension.getClass().getCanonicalName(), e);
            }
        }
    }

    void hookApiImplementors(Extension extension, ExtensionHook extHook) {
        for (ApiImplementor apiImplementor : extHook.getApiImplementors()) {
            try {
                API.getInstance().registerApiImplementor(apiImplementor);
            } catch (Exception e) {
                logger.error("Error while adding an ApiImplementor from " + extension.getClass().getCanonicalName(), e);
            }
        }
    }

    /**
     * Hook command line listener with the command line processor
     *
     * @param cmdLine
     * @throws Exception
     */
    public void hookCommandLineListener(CommandLine cmdLine) throws Exception {
        List<CommandLineArgument[]> allCommandLineList = new ArrayList<CommandLineArgument[]>();
        Map<String, CommandLineListener> extMap = new HashMap<String, CommandLineListener>();
        for (Map.Entry<Extension, ExtensionHook> entry : extensionHooks.entrySet()) {
            ExtensionHook hook = entry.getValue();
            CommandLineArgument[] arg = hook.getCommandLineArgument();
            if (arg.length > 0) {
                allCommandLineList.add(arg);
            }

            Extension extension = entry.getKey();
            if (extension instanceof CommandLineListener) {
                CommandLineListener cli = (CommandLineListener) extension;
                List<String> exts = cli.getHandledExtensions();
                if (exts != null) {
                    for (String ext : exts) {
                        extMap.put(ext, cli);
                    }
                }
            }
        }

        cmdLine.parse(allCommandLineList, extMap);
    }

    void hookMenu(View view, ExtensionHook hook) {
        if (view == null) {
            return;
        }

        ExtensionHookMenu hookMenu = hook.getHookMenu();
        if (hookMenu == null) {
            return;
        }

        MainMenuBar menuBar = view.getMainFrame().getMainMenuBar();

        // 2 menus at the back (Tools/Help)
        addMenuHelper(menuBar, hookMenu.getNewMenus(), 2);

        addMenuHelper(menuBar.getMenuFile(), hookMenu.getFile(), 2);
        addMenuHelper(menuBar.getMenuTools(), hookMenu.getTools(), 2);
        addMenuHelper(menuBar.getMenuEdit(), hookMenu.getEdit());
        addMenuHelper(menuBar.getMenuView(), hookMenu.getView());
        addMenuHelper(menuBar.getMenuAnalyse(), hookMenu.getAnalyse());
        addMenuHelper(menuBar.getMenuHelp(), hookMenu.getHelpMenus());
        addMenuHelper(menuBar.getMenuReport(), hookMenu.getReportMenus());
        addMenuHelper(menuBar.getMenuOnline(), hookMenu.getOnlineMenus());

        addMenuHelper(view.getPopupList(), hookMenu.getPopupMenus());
    }

    void addMenuHelper(JMenu menu, List<JMenuItem> items) {
        addMenuHelper(menu, items, 0);
    }

    void addMenuHelper(JMenuBar menuBar, List<JMenuItem> items, int existingCount) {
        for (JMenuItem item : items) {
            if (item != null) {
                menuBar.add(item, menuBar.getMenuCount() - existingCount);
            }
        }
        menuBar.revalidate();
    }

    void addMenuHelper(JMenu menu, List<JMenuItem> items, int existingCount) {
        for (JMenuItem item : items) {
            if (item != null) {
                if (item == ExtensionHookMenu.MENU_SEPARATOR) {
                    menu.addSeparator();
                    continue;
                }

                menu.add(item, menu.getItemCount() - existingCount);
            }
        }

        menu.revalidate();
    }

    void addMenuHelper(List<JMenuItem> menuList, List<JMenuItem> items) {
        for (JMenuItem item : items) {
            if (item != null) {
                menuList.add(item);
            }
        }
    }

    void removeMenu(View view, ExtensionHook hook) {
        if (view == null) {
            return;
        }

        ExtensionHookMenu hookMenu = hook.getHookMenu();
        if (hookMenu == null) {
            return;
        }

        MainMenuBar menuBar = view.getMainFrame().getMainMenuBar();

        // clear up various menus
        removeMenuHelper(menuBar, hookMenu.getNewMenus());

        removeMenuHelper(menuBar.getMenuFile(), hookMenu.getFile());
        removeMenuHelper(menuBar.getMenuTools(), hookMenu.getTools());
        removeMenuHelper(menuBar.getMenuEdit(), hookMenu.getEdit());
        removeMenuHelper(menuBar.getMenuView(), hookMenu.getView());
        removeMenuHelper(menuBar.getMenuAnalyse(), hookMenu.getAnalyse());
        removeMenuHelper(menuBar.getMenuHelp(), hookMenu.getHelpMenus());
        removeMenuHelper(menuBar.getMenuReport(), hookMenu.getReportMenus());
        removeMenuHelper(menuBar.getMenuOnline(), hookMenu.getOnlineMenus());

        removeMenuHelper(view.getPopupList(), hookMenu.getPopupMenus());

        view.refreshTabViewMenus();
    }

    void removeMenuHelper(JMenuBar menuBar, List<JMenuItem> items) {
        for (JMenuItem item : items) {
            if (item != null) {
                menuBar.remove(item);
            }
        }
        menuBar.revalidate();
    }

    void removeMenuHelper(JMenu menu, List<JMenuItem> items) {
        for (JMenuItem item : items) {
            if (item != null) {
                menu.remove(item);
            }
        }
        menu.revalidate();
    }

    void removeMenuHelper(List<JMenuItem> menuList, List<JMenuItem> items) {
        for (JMenuItem item : items) {
            if (item != null) {
                menuList.remove(item);
            }
        }
    }

    void hookOptions(ExtensionHook hook) {
        List<AbstractParam> list = hook.getOptionsParamSetList();

        for (AbstractParam paramSet : list) {
            try {
                model.getOptionsParam().addParamSet(paramSet);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    void unloadOptions(ExtensionHook hook) {
        List<AbstractParam> list = hook.getOptionsParamSetList();

        for (AbstractParam paramSet : list) {
            try {
                model.getOptionsParam().removeParamSet(paramSet);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    void hookView(Extension extension, View view, ExtensionHook hook) {
        if (view == null) {
            return;
        }

        ExtensionHookView pv = hook.getHookView();
        if (pv == null) {
            return;
        }

        for (ContextPanelFactory contextPanelFactory : pv.getContextPanelFactories()) {
            try {
                view.addContextPanelFactory(contextPanelFactory);
            } catch (Exception e) {
                logger.error("Error while adding a ContextPanelFactory from " + extension.getClass().getCanonicalName(), e);
            }
        }

        view.getWorkbench().addPanels(pv.getSelectPanel(), WorkbenchPanel.PanelType.SELECT);
        view.getWorkbench().addPanels(pv.getWorkPanel(), WorkbenchPanel.PanelType.WORK);
        view.getWorkbench().addPanels(pv.getStatusPanel(), WorkbenchPanel.PanelType.STATUS);

        addParamPanel(pv.getSessionPanel(), view.getSessionDialog());
        addParamPanel(pv.getOptionsPanel(), view.getOptionsDialog(""));
    }

    void removeView(Extension extension, View view, ExtensionHook hook) {
        if (view == null) {
            return;
        }

        ExtensionHookView pv = hook.getHookView();
        if (pv == null) {
            return;
        }

        for (ContextPanelFactory contextPanelFactory : pv.getContextPanelFactories()) {
            try {
                view.removeContextPanelFactory(contextPanelFactory);
            } catch (Exception e) {
                logger.error("Error while removing a ContextPanelFactory from " + extension.getClass().getCanonicalName(), e);
            }
        }

        view.getWorkbench().removePanels(pv.getSelectPanel(), WorkbenchPanel.PanelType.SELECT);
        view.getWorkbench().removePanels(pv.getWorkPanel(), WorkbenchPanel.PanelType.WORK);
        view.getWorkbench().removePanels(pv.getStatusPanel(), WorkbenchPanel.PanelType.STATUS);

        removeParamPanel(pv.getSessionPanel(), view.getSessionDialog());
        removeParamPanel(pv.getOptionsPanel(), view.getOptionsDialog(""));
    }

    public void removeStatusPanel(AbstractPanel panel) {
        if (!View.isInitialised()) {
            return;
        }

        View.getSingleton().getWorkbench().removePanel(panel, WorkbenchPanel.PanelType.STATUS);
    }

    public void removeOptionsPanel(AbstractParamPanel panel) {
        if (!View.isInitialised()) {
            return;
        }

        View.getSingleton().getOptionsDialog("").removeParamPanel(panel);
    }

    public void removeOptionsParamSet(AbstractParam params) {
        model.getOptionsParam().removeParamSet(params);
    }

    public void removeWorkPanel(AbstractPanel panel) {
        if (!View.isInitialised()) {
            return;
        }

        View.getSingleton().getWorkbench().removePanel(panel, WorkbenchPanel.PanelType.WORK);
    }

    public void removePopupMenuItem(ExtensionPopupMenuItem popupMenuItem) {
        if (!View.isInitialised()) {
            return;
        }

        View.getSingleton().getPopupList().remove(popupMenuItem);
    }

    public void removeFileMenuItem(JMenuItem menuItem) {
        if (!View.isInitialised()) {
            return;
        }

        View.getSingleton().getMainFrame().getMainMenuBar().getMenuFile().remove(menuItem);
    }

    public void removeEditMenuItem(JMenuItem menuItem) {
        if (!View.isInitialised()) {
            return;
        }

        View.getSingleton().getMainFrame().getMainMenuBar().getMenuEdit().remove(menuItem);
    }

    public void removeViewMenuItem(JMenuItem menuItem) {
        if (!View.isInitialised()) {
            return;
        }

        View.getSingleton().getMainFrame().getMainMenuBar().getMenuView().remove(menuItem);
    }

    public void removeToolsMenuItem(JMenuItem menuItem) {
        if (!View.isInitialised()) {
            return;
        }

        View.getSingleton().getMainFrame().getMainMenuBar().getMenuTools().remove(menuItem);
    }

    public void removeHelpMenuItem(JMenuItem menuItem) {
        if (!View.isInitialised()) {
            return;
        }

        View.getSingleton().getMainFrame().getMainMenuBar().getMenuHelp().remove(menuItem);
    }

    public void removeReportMenuItem(JMenuItem menuItem) {
        if (!View.isInitialised()) {
            return;
        }

        View.getSingleton().getMainFrame().getMainMenuBar().getMenuReport().remove(menuItem);
    }
}