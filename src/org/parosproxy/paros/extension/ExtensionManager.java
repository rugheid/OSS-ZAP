/*
 *
 * Paros and its related class files.
 * 
 * Paros is an HTTP/HTTPS proxy for assessing web application security.
 * Copyright (C) 2003-2004 Chinotec Technologies Company
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Clarified Artistic License
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Clarified Artistic License for more details.
 * 
 * You should have received a copy of the Clarified Artistic License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
// ZAP: 2011/12/14 Support for extension dependencies
// ZAP: 2012/02/18 Rationalised session handling
// ZAP: 2012/03/15 Reflected the change in the name of the method optionsChanged of
// the class OptionsChangedListener. Changed the method destroyAllExtension() to
// save the configurations of the main http panels and save the configuration file.
// ZAP: 2012/04/23 Reverted the changes of the method destroyAllExtension(),
// now the configurations of the main http panels and the configuration file
// are saved in the method Control.shutdown(boolean).
// ZAP: 2012/04/24 Changed the method destroyAllExtension to catch exceptions.
// ZAP: 2012/04/25 Added the type argument and removed unnecessary cast.
// ZAP: 2012/07/23 Removed parameter from View.getSessionDialog call.
// ZAP: 2012/07/29 Issue 43: added sessionScopeChanged event
// ZAP: 2012/08/01 Issue 332: added support for Modes
// ZAP: 2012/11/30 Issue 425: Added tab index to support quick start tab 
// ZAP: 2012/12/27 Added hookPersistentConnectionListener() method.
// ZAP: 2013/01/16 Issue 453: Dynamic loading and unloading of add-ons
// ZAP: 2013/01/25 Added removeExtension(...) method and further helper methods
// to remove listeners, menu items, etc.
// ZAP: 2013/01/25 Refactored hookMenu(). Resolved some Checkstyle issues.
// ZAP: 2013/01/29 Catch Errors thrown by out of date extensions as well as Exceptions
// ZAP: 2013/07/23 Issue 738: Options to hide tabs
// ZAP: 2013/11/16 Issue 807: Error while loading ZAP when Quick Start Tab is closed
// ZAP: 2013/11/16 Issue 845: AbstractPanel added twice to TabbedPanel2 in ExtensionManager#addTabPanel
// ZAP: 2013/12/03 Issue 934: Handle files on the command line via extension
// ZAP: 2013/12/13 Added support for Full Layout DISPLAY_OPTION_TOP_FULL in the hookView function.
// ZAP: 2014/03/23 Issue 1022: Proxy - Allow to override a proxied message
// ZAP: 2014/03/23 Issue 1090: Do not add pop up menus if target extension is not enabled
// ZAP: 2014/05/20 Issue 1202: Issue with loading addons that did not initialize correctly
// ZAP: 2014/08/14 Catch Exceptions thrown by extensions when stopping them
// ZAP: 2014/08/14 Issue 1309: NullPointerExceptions during a failed uninstallation of an add-on
// ZAP: 2014/10/07 Issue 1357: Hide unused tabs
// ZAP: 2014/10/09 Issue 1359: Added info logging for splash screen
// ZAP: 2014/10/25 Issue 1062: Added scannerhook to be loaded by an active scanner.
// ZAP: 2014/11/11 Issue 1406: Move online menu items to an add-on
// ZAP: 2014/11/21 Reviewed foreach loops and commented startup process for splash screen progress bar
// ZAP: 2015/01/04 Issue 1379: Not all extension's listeners are hooked during add-on installation
// ZAP: 2015/01/19 Remove online menus when removeMenu(View, ExtensionHook) is called.
// ZAP: 2015/01/19 Issue 1510: New Extension.postInit() method to be called once all extensions loaded
// ZAP: 2015/02/09 Issue 1525: Introduce a database interface layer to allow for alternative implementations
// ZAP: 2015/02/10 Issue 1208: Search classes/resources in add-ons declared as dependencies
// ZAP: 2015/04/09 Generify Extension.getExtension(Class) to avoid unnecessary casts
// ZAP: 2015/09/07 Start GUI on EDT
// ZAP: 2016/04/06 Fix layouts' issues
// ZAP: 2016/04/08 Hook ContextDataFactory/ContextPanelFactory 
// ZAP: 2016/05/30 Notification of installation status of the add-ons
// ZAP: 2016/05/30 Issue 2494: ZAP Proxy is not showing the HTTP CONNECT Request in history tab
// ZAP: 2016/08/18 Hook ApiImplementor

package org.parosproxy.paros.extension;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;

import org.apache.log4j.Logger;
import org.parosproxy.paros.CommandLine;
import org.parosproxy.paros.common.AbstractParam;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.control.Control.Mode;
import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.core.scanner.Scanner;
import org.parosproxy.paros.db.Database;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.DatabaseUnsupportedException;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.model.OptionsParam;
import org.parosproxy.paros.model.Session;
import org.parosproxy.paros.view.AbstractParamPanel;
import org.parosproxy.paros.view.SiteMapPanel;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.control.AddOn;
import org.zaproxy.zap.extension.AddonFilesChangedListener;
import org.zaproxy.zap.extension.api.API;
import org.zaproxy.zap.extension.api.ApiImplementor;
import org.zaproxy.zap.extension.AddOnInstallationStatusListener;
import org.zaproxy.zap.model.ContextDataFactory;

public class ExtensionManager {

    private final List<Extension> extensionList = new ArrayList<>();
    private final Map<Class<? extends Extension>, Extension> extensionsMap = new HashMap<>();
    private final ExtensionHookLoader extensionHookLoader;
    private Model model = null;

    private View view = null;
    private static final Logger logger = Logger.getLogger(ExtensionManager.class);

    public ExtensionManager(Model model, View view) {
        this.model = model;
        this.view = view;
        this.extensionHookLoader = new ExtensionHookLoader(model, view);
    }

    public void addExtension(Extension extension) {
        extensionList.add(extension);
        extensionsMap.put(extension.getClass(), extension);
    }

    public void destroyAllExtension() {
        for (int i = 0; i < getExtensionCount(); i++) {
            try {
                getExtension(i).destroy();
                
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    public Extension getExtension(int i) {
        return extensionList.get(i);
    }

    public Extension getExtension(String name) {
        if (name != null) {
            for (int i = 0; i < extensionList.size(); i++) {
                Extension p = getExtension(i);
                if (p.getName().equalsIgnoreCase(name)) {
                    return p;
                }
            }
        }
        
        return null;
    }

    public Extension getExtensionByClassName(String name) {
        if (name != null) {
            for (int i = 0; i < extensionList.size(); i++) {
                Extension p = getExtension(i);
                if (p.getClass().getName().equals(name)) {
                    return p;
                }
            }
        }
        
        return null;
    }

    /**
     * Gets the {@code Extension} with the given class.
     *
     * @param clazz the class of the {@code Extension}
     * @return the {@code Extension} or {@code null} if not found.
     */
    public <T extends Extension> T getExtension(Class<T> clazz) {
        if (clazz != null) {
            Extension extension = extensionsMap.get(clazz);
            if (extension != null) {
                return clazz.cast(extension);
            }
        }
        return null;
    }

    /**
     * Tells whether or not an {@code Extension} with the given
     * {@code extensionName} is enabled.
     *
     * @param extensionName the name of the extension
     * @return {@code true} if the extension is enabled, {@code false}
     * otherwise.
     * @throws IllegalArgumentException if the {@code extensionName} is
     * {@code null}.
     * @see #getExtension(String)
     * @see Extension
     */
    public boolean isExtensionEnabled(String extensionName) {
        if (extensionName == null) {
            throw new IllegalArgumentException("Parameter extensionName must not be null.");
        }

        Extension extension = getExtension(extensionName);
        if (extension == null) {
            return false;
        }
        
        return extension.isEnabled();
    }

    public int getExtensionCount() {
        return extensionList.size();
    }

    public void hookProxyListener(Proxy proxy) {
        extensionHookLoader.hookProxyListener(proxy);
    }

    public void hookOverrideMessageProxyListener(Proxy proxy) {
        extensionHookLoader.hookOverrideMessageProxyListener(proxy);
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
        extensionHookLoader.hookConnectRequestProxyListeners(proxy);
    }

    public void hookPersistentConnectionListener(Proxy proxy) {
        extensionHookLoader.hookPersistentConnectionListener(proxy);
    }

    // ZAP: Added support for site map listeners
    public void hookSiteMapListener(SiteMapPanel siteMapPanel) {
        extensionHookLoader.hookSiteMapListener(siteMapPanel);
    }

    // ZAP: method called by the scanner to load all scanner hooks. 
    public void hookScannerHook(Scanner scan) {
        extensionHookLoader.hookScannerHook(scan);
    }

    public void optionsChangedAllPlugin(OptionsParam options) {
        for (ExtensionHook hook : extensionHookLoader.extensionHooks.values()) {
            List<OptionsChangedListener> listenerList = hook.getOptionsChangedListenerList();
            for (OptionsChangedListener listener : listenerList) {
                try {
                    if (listener != null) {
                        listener.optionsChanged(options);
                    }
                    
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void runCommandLine() {
        Extension ext;
        for (int i = 0; i < getExtensionCount(); i++) {
            ext = getExtension(i);
            if (ext instanceof CommandLineListener) {
                CommandLineListener listener = (CommandLineListener) ext;
                listener.execute(extensionHookLoader.extensionHooks.get(ext).getCommandLineArgument());
            }
        }
    }

    public void sessionChangedAllPlugin(Session session) {
        logger.debug("sessionChangedAllPlugin");
        for (ExtensionHook hook : extensionHookLoader.extensionHooks.values()) {
            List<SessionChangedListener> listenerList = hook.getSessionListenerList();
            for (SessionChangedListener listener : listenerList) {
                try {
                    if (listener != null) {
                        listener.sessionChanged(session);
                    }

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void databaseOpen(Database db) {
        Extension ext;
        for (int i = 0; i < getExtensionCount(); i++) {
            ext = getExtension(i);
            try {
				ext.databaseOpen(db);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
			}
        }
    }

    public void sessionAboutToChangeAllPlugin(Session session) {
        logger.debug("sessionAboutToChangeAllPlugin");
        for (ExtensionHook hook : extensionHookLoader.extensionHooks.values()) {
            List<SessionChangedListener> listenerList = hook.getSessionListenerList();
            for (SessionChangedListener listener : listenerList) {
                try {
                    if (listener != null) {
                        listener.sessionAboutToChange(session);
                    }

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void sessionScopeChangedAllPlugin(Session session) {
        logger.debug("sessionScopeChangedAllPlugin");
        for (ExtensionHook hook : extensionHookLoader.extensionHooks.values()) {
            List<SessionChangedListener> listenerList = hook.getSessionListenerList();
            for (SessionChangedListener listener : listenerList) {
                try {
                    if (listener != null) {
                        listener.sessionScopeChanged(session);
                    }

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void sessionModeChangedAllPlugin(Mode mode) {
        logger.debug("sessionModeChangedAllPlugin");
        for (ExtensionHook hook : extensionHookLoader.extensionHooks.values()) {
            List<SessionChangedListener> listenerList = hook.getSessionListenerList();
            for (SessionChangedListener listener : listenerList) {
                try {
                    if (listener != null) {
                        listener.sessionModeChanged(mode);
                    }
                    
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void addonFilesAdded() {
        for (ExtensionHook hook : extensionHookLoader.extensionHooks.values()) {
            List<AddonFilesChangedListener> listenerList = hook.getAddonFilesChangedListener();
            for (AddonFilesChangedListener listener : listenerList) {
                try {
                    listener.filesAdded();
                    
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void addonFilesRemoved() {
        for (ExtensionHook hook : extensionHookLoader.extensionHooks.values()) {
            List<AddonFilesChangedListener> listenerList = hook.getAddonFilesChangedListener();
            for (AddonFilesChangedListener listener : listenerList) {
                try {
                    listener.filesRemoved();
                    
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
    
    /**
     * Notifies {@code Extension}s' {@code AddOnInstallationStatusListener}s that the given add-on was installed.
     *
     * @param addOn the add-on that was installed, must not be {@code null}
     * @since 2.5.0
     */
    public void addOnInstalled(AddOn addOn) {
        for (ExtensionHook hook : extensionHookLoader.extensionHooks.values()) {
            for (AddOnInstallationStatusListener listener : hook.getAddOnInstallationStatusListeners()) {
                try {
                    listener.addOnInstalled(addOn);
                } catch (Exception e) {
                    logger.error("An error occurred while notifying: " + listener.getClass().getCanonicalName(), e);
                }
            }
        }
    }

    /**
     * Notifies {@code Extension}s' {@code AddOnInstallationStatusListener}s that the given add-on was soft uninstalled.
     *
     * @param addOn the add-on that was soft uninstalled, must not be {@code null}
     * @param successfully if the soft uninstallation was successful, that is, no errors occurred while uninstalling it
     * @since 2.5.0
     */
    public void addOnSoftUninstalled(AddOn addOn, boolean successfully) {
        for (ExtensionHook hook : extensionHookLoader.extensionHooks.values()) {
            for (AddOnInstallationStatusListener listener : hook.getAddOnInstallationStatusListeners()) {
                try {
                    listener.addOnSoftUninstalled(addOn, successfully);
                } catch (Exception e) {
                    logger.error("An error occurred while notifying: " + listener.getClass().getCanonicalName(), e);
                }
            }
        }
    }

    /**
     * Notifies {@code Extension}s' {@code AddOnInstallationStatusListener}s that the given add-on was uninstalled.
     *
     * @param addOn the add-on that was uninstalled, must not be {@code null}
     * @param successfully if the uninstallation was successful, that is, no errors occurred while uninstalling it
     * @since 2.5.0
     */
    public void addOnUninstalled(AddOn addOn, boolean successfully) {
        for (ExtensionHook hook : extensionHookLoader.extensionHooks.values()) {
            for (AddOnInstallationStatusListener listener : hook.getAddOnInstallationStatusListeners()) {
                try {
                    listener.addOnUninstalled(addOn, successfully);
                } catch (Exception e) {
                    logger.error("An error occurred while notifying: " + listener.getClass().getCanonicalName(), e);
                }
            }
        }
    }

    public void startAllExtension(double progressFactor) {
        double factorPerc = progressFactor / getExtensionCount();
        
        for (int i = 0; i < getExtensionCount(); i++) {
            try {
                getExtension(i).start();
                if (view != null) {
                    view.addSplashScreenLoadingCompletion(factorPerc);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Initialize and start all Extensions
     * This function loops for all getExtensionCount() exts
     * launching each specific initialization element (model, xml, view, hook, etc.)
     */
    public void startLifeCycle() {
        
        // Percentages are passed into the calls as doubles
    	if (view != null) {
    		view.setSplashScreenLoadingCompletion(0.0);
    	}

        // Step 3: initialize all (slow)
        initAllExtension(5.0);
        // Step 4: initialize models (quick)
        initModelAllExtension(model, 0.0);
        // Step 5: initialize xmls (quick)
        initXMLAllExtension(model.getSession(), model.getOptionsParam(), 0.0);
        // Step 6: initialize viewes (slow)
        initViewAllExtension(view, 10.0);
        // Step 7: initialize hooks (slowest)
        extensionHookLoader.hookAllExtension(75.0, extensionList);
        // Step 8: start all extensions(quick)
        startAllExtension(10.0);
    }

    /**
     * Initialize a specific Extension
     * @param ext the Extension that need to be initialized
     * @throws DatabaseUnsupportedException 
     * @throws DatabaseException 
     */
    public void startLifeCycle(Extension ext) throws DatabaseException, DatabaseUnsupportedException {
        ext.init();
        ext.databaseOpen(model.getDb());
        ext.initModel(model);
        ext.initXML(model.getSession(), model.getOptionsParam());
        ext.initView(view);
        
        ExtensionHook extHook = new ExtensionHook(model, view);
        try {
            ext.hook(extHook);
            extensionHookLoader.extensionHooks.put(ext, extHook);

            extensionHookLoader.hookContextDataFactories(ext, extHook);
            extensionHookLoader.hookApiImplementors(ext, extHook);

            if (view != null) {
                // no need to hook view if no GUI
                extensionHookLoader.hookView(ext, view, extHook);
                extensionHookLoader.hookMenu(view, extHook);
            }

            extensionHookLoader.hookOptions(extHook);
            ext.optionsLoaded();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        
        ext.start();

        Proxy proxy = Control.getSingleton().getProxy();
        ExtensionHookLoader.hookProxyListeners(proxy, extHook.getProxyListenerList());

        ExtensionHookLoader.hookPersistentConnectionListeners(proxy, extHook.getPersistentConnectionListener());
        ExtensionHookLoader.hookConnectRequestProxyListeners(proxy, extHook.getConnectRequestProxyListeners());

        if (view != null) {
            ExtensionHookLoader.hookSiteMapListeners(view.getSiteTreePanel(), extHook.getSiteMapListenerList());
        }
    }

    public void stopAllExtension() {
        for (int i = 0; i < getExtensionCount(); i++) {
            try {
                getExtension(i).stop();
                
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    /**
     * Hook command line listener with the command line processor
     *
     * @param cmdLine
     * @throws java.lang.Exception
     */
    public void hookCommandLineListener(CommandLine cmdLine) throws Exception {

        extensionHookLoader.hookCommandLineListener(cmdLine);
    }

    public void removeStatusPanel(AbstractPanel panel) {

        extensionHookLoader.removeStatusPanel(panel);
    }

    public void removeOptionsPanel(AbstractParamPanel panel) {

        extensionHookLoader.removeOptionsPanel(panel);
    }

    public void removeOptionsParamSet(AbstractParam params) {
        extensionHookLoader.removeOptionsParamSet(params);
    }

    public void removeWorkPanel(AbstractPanel panel) {

        extensionHookLoader.removeWorkPanel(panel);
    }

    public void removePopupMenuItem(ExtensionPopupMenuItem popupMenuItem) {

        extensionHookLoader.removePopupMenuItem(popupMenuItem);
    }

    public void removeFileMenuItem(JMenuItem menuItem) {

        extensionHookLoader.removeFileMenuItem(menuItem);
    }

    public void removeEditMenuItem(JMenuItem menuItem) {

        extensionHookLoader.removeEditMenuItem(menuItem);
    }

    public void removeViewMenuItem(JMenuItem menuItem) {

        extensionHookLoader.removeViewMenuItem(menuItem);
    }

    public void removeToolsMenuItem(JMenuItem menuItem) {

        extensionHookLoader.removeToolsMenuItem(menuItem);
    }

    public void removeHelpMenuItem(JMenuItem menuItem) {

        extensionHookLoader.removeHelpMenuItem(menuItem);
    }

    public void removeReportMenuItem(JMenuItem menuItem) {

        extensionHookLoader.removeReportMenuItem(menuItem);
    }

    /**
     * Init all extensions
     */
    private void initAllExtension(double progressFactor) {        
        double factorPerc = progressFactor / getExtensionCount();
        
        for (int i = 0; i < getExtensionCount(); i++) {
            try {
                getExtension(i).init();
                getExtension(i).databaseOpen(Model.getSingleton().getDb());
                if (view != null) {
                	view.addSplashScreenLoadingCompletion(factorPerc);
                }
                
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Init all extensions with the same Model
     * @param model the model to apply to all extensions
     */
    private void initModelAllExtension(Model model, double progressFactor) {
        double factorPerc = progressFactor / getExtensionCount();
        
        for (int i = 0; i < getExtensionCount(); i++) {
            try {
                getExtension(i).initModel(model);
                if (view != null) {
                    view.addSplashScreenLoadingCompletion(factorPerc);
                }
                
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Init all extensions with the same View
     * @param view the View that need to be applied
     */
    private void initViewAllExtension(final View view, double progressFactor) {
        if (view == null) {
            return;
        }

        final double factorPerc = progressFactor / getExtensionCount();
        
        for (int i = 0; i < getExtensionCount(); i++) {
            try {
                final Extension extension = getExtension(i);
                EventQueue.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        extension.initView(view);
                        view.addSplashScreenLoadingCompletion(factorPerc);
                    }
                });
                
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void initXMLAllExtension(Session session, OptionsParam options, double progressFactor) {
        double factorPerc = progressFactor / getExtensionCount();
        
        for (int i = 0; i < getExtensionCount(); i++) {
            try {
                getExtension(i).initXML(session, options);
                if (view != null) {
                    view.addSplashScreenLoadingCompletion(factorPerc);
                }
                
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Removes an extension from internal list. As a result listeners added via
     * the {@link ExtensionHook} object are unregistered.
     *
     * @param extension
     * @param hook
     */
    public void removeExtension(Extension extension, ExtensionHook hook) {
        extensionList.remove(extension);
        extensionsMap.remove(extension.getClass());

        if (hook == null) {
            logger.info("ExtensionHook is null for \"" + extension.getClass().getCanonicalName()
                    + "\" the hooked objects will not be automatically removed.");
            return;
        }

        // by removing the ExtensionHook object,
        // the following listeners are no longer informed:
        // 		* SessionListeners
        // 		* OptionsChangedListeners
        extensionHookLoader.extensionHooks.values().remove(hook);
        extensionHookLoader.unloadOptions(hook);
        extensionHookLoader.removePersistentConnectionListener(hook);
        extensionHookLoader.removeProxyListener(hook);
        extensionHookLoader.removeOverrideMessageProxyListener(hook);
        extensionHookLoader.removeConnectRequestProxyListener(hook);
        extensionHookLoader.removeSiteMapListener(hook);

        for (ContextDataFactory contextDataFactory : hook.getContextDataFactories()) {
            try {
                model.removeContextDataFactory(contextDataFactory);
            } catch (Exception e) {
                logger.error("Error while removing a ContextDataFactory from " + extension.getClass().getCanonicalName(), e);
            }
        }

        for (ApiImplementor apiImplementor : hook.getApiImplementors()) {
            try {
                API.getInstance().removeApiImplementor(apiImplementor);
            } catch (Exception e) {
                logger.error("Error while removing an ApiImplementor from " + extension.getClass().getCanonicalName(), e);
            }
        }

        removeViewInEDT(extension, hook);
    }

    private void removeViewInEDT(final Extension extension, final ExtensionHook hook) {
        if (view == null) {
            return;
        }

        if (EventQueue.isDispatchThread()) {
            extensionHookLoader.removeView(extension, view, hook);
            extensionHookLoader.removeMenu(view, hook);
        } else {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    removeViewInEDT(extension, hook);
                }
            });
        }
    }

    /**
     * Gets the names of all unsaved resources of all the extensions.
     *
     * @return a {@code List} containing all the unsaved resources of all add-ons, never {@code null}
     * @see Extension#getActiveActions()
     */
    public List<String> getUnsavedResources() {
        List<String> list = new ArrayList<>();
        List<String> l;

        for (int i = 0; i < getExtensionCount(); i++) {
            l = getExtension(i).getUnsavedResources();
            if (l != null) {
                list.addAll(l);
            }
        }
        
        return list;
    }

    /**
     * Gets the names of all active actions of all the extensions.
     *
     * @return a {@code List} containing all the active actions of all add-ons, never {@code null}
     * @since 2.4.0
     * @see Extension#getActiveActions()
     */
    public List<String> getActiveActions() {
        List<String> list = new ArrayList<>();
        List<String> l;

        for (int i = 0; i < getExtensionCount(); i++) {
            l = getExtension(i).getActiveActions();
            if (l != null) {
                list.addAll(l);
            }
        }
        
        return list;
    }

}
