package org.elyast.decorify.camel.internal;

import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.naming.InitialContext;
import javax.servlet.ServletException;

import org.apache.camel.Component;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Endpoint;
import org.apache.camel.NoFactoryAvailableException;
import org.apache.camel.Producer;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Route;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.ServiceStatus;
import org.apache.camel.ShutdownRoute;
import org.apache.camel.ShutdownRunningTask;
import org.apache.camel.StartupListener;
import org.apache.camel.TypeConverter;
import org.apache.camel.builder.ErrorHandlerBuilder;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spi.CamelContextNameStrategy;
import org.apache.camel.spi.ClassResolver;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.spi.DataFormatResolver;
import org.apache.camel.spi.Debugger;
import org.apache.camel.spi.EndpointStrategy;
import org.apache.camel.spi.ExecutorServiceStrategy;
import org.apache.camel.spi.FactoryFinder;
import org.apache.camel.spi.FactoryFinderResolver;
import org.apache.camel.spi.InflightRepository;
import org.apache.camel.spi.Injector;
import org.apache.camel.spi.InterceptStrategy;
import org.apache.camel.spi.Language;
import org.apache.camel.spi.LifecycleStrategy;
import org.apache.camel.spi.ManagementStrategy;
import org.apache.camel.spi.NodeIdFactory;
import org.apache.camel.spi.PackageScanClassResolver;
import org.apache.camel.spi.ProcessorFactory;
import org.apache.camel.spi.Registry;
import org.apache.camel.spi.ServicePool;
import org.apache.camel.spi.ShutdownStrategy;
import org.apache.camel.spi.TypeConverterRegistry;
import org.apache.camel.spi.UuidGenerator;
import org.elyast.decorify.camel.BindableCamelContext;
import org.elyast.decorify.camel.BindableRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrappedCamelContext implements BindableCamelContext {

	private static final String DEFAULT_SERVLET_NAME = "CamelServlet";
	private static final String DEFAULT_MATCH = "false";
	private static final String DEFAULT_ALIAS = "/services";
	private static final String SERVLET_NAME = "servlet-name";
	private static final String MATCH_ON_URI_PREFIX = "matchOnUriPrefix";
	private static final String ALIAS = "alias";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(WrappedCamelContext.class);
	private OsgiDefaultCamelContext camel;
	private HttpService httpService;
	private String alias;
	private BindableJndiRegistry registry;

	void activate(BundleContext context, Map<String, Object> properties)
			throws Exception {
		LOGGER.info("activating with properties {}", properties);
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("java.naming.factory.initial", "org.apache.camel.util.jndi.CamelInitialContextFactory");
		InitialContext jndiContext = new InitialContext(env);
		registry = new BindableJndiRegistry(jndiContext);
		camel = new OsgiDefaultCamelContext(context, registry);
		camel.start();
		alias = (String) properties.get(ALIAS);
		alias = alias == null ? DEFAULT_ALIAS : alias;
		String matchOn = (String) properties.get(MATCH_ON_URI_PREFIX);
		matchOn = matchOn == null ? DEFAULT_MATCH : matchOn;
		String servletName = (String) properties.get(SERVLET_NAME);
		servletName = servletName == null ? DEFAULT_SERVLET_NAME : servletName;
		LOGGER.info("activating with properties {}, {}, {}", new Object[] {
				alias, matchOn, servletName });
		registerServlet(alias, matchOn, servletName);
	}

	private void registerServlet(String alias, String matchOn,
			String servletName) throws ServletException, NamespaceException {
		final HttpContext httpContext = httpService.createDefaultHttpContext();
		final Dictionary<String, String> initParams = new Hashtable<String, String>();
		initParams.put(MATCH_ON_URI_PREFIX, matchOn);
		initParams.put(SERVLET_NAME, servletName);
		httpService.registerServlet(alias, new CamelHttpTransportServlet(),
				initParams, httpContext);
	}

	void deactivate() throws Exception {
		LOGGER.info("deactivting...");
		this.httpService.unregister(alias);
		camel.stop();
	}

	void bind(HttpService httpService) {
		LOGGER.info("binding service {}", httpService);
		this.httpService = httpService;
	}

	void unbind(HttpService httpService) {
		LOGGER.debug("unbinding service {}", httpService);
		this.httpService = null;

	}

	@Override
	public BindableRegistry getBindableRegistry() {
		return registry;
	}

	public void start() throws Exception {
		camel.start();
	}

	public void setStreamCaching(Boolean paramBoolean) {
		camel.setStreamCaching(paramBoolean);
	}

	public void suspend() throws Exception {
		camel.suspend();
	}

	public void stop() throws Exception {
		camel.stop();
	}

	public Boolean isStreamCaching() {
		return camel.isStreamCaching();
	}

	public void resume() throws Exception {
		camel.resume();
	}

	public void setTracing(Boolean paramBoolean) {
		camel.setTracing(paramBoolean);
	}

	public boolean isSuspended() {
		return camel.isSuspended();
	}

	public Boolean isTracing() {
		return camel.isTracing();
	}

	public void setHandleFault(Boolean paramBoolean) {
		camel.setHandleFault(paramBoolean);
	}

	public Boolean isHandleFault() {
		return camel.isHandleFault();
	}

	public void setDelayer(Long paramLong) {
		camel.setDelayer(paramLong);
	}

	public Long getDelayer() {
		return camel.getDelayer();
	}

	public void setAutoStartup(Boolean paramBoolean) {
		camel.setAutoStartup(paramBoolean);
	}

	public Boolean isAutoStartup() {
		return camel.isAutoStartup();
	}

	public void setShutdownRoute(ShutdownRoute paramShutdownRoute) {
		camel.setShutdownRoute(paramShutdownRoute);
	}

	public ShutdownRoute getShutdownRoute() {
		return camel.getShutdownRoute();
	}

	public void setShutdownRunningTask(
			ShutdownRunningTask paramShutdownRunningTask) {
		camel.setShutdownRunningTask(paramShutdownRunningTask);
	}

	public ShutdownRunningTask getShutdownRunningTask() {
		return camel.getShutdownRunningTask();
	}

	public String getName() {
		return camel.getName();
	}

	public CamelContextNameStrategy getNameStrategy() {
		return camel.getNameStrategy();
	}

	public void setNameStrategy(
			CamelContextNameStrategy paramCamelContextNameStrategy) {
		camel.setNameStrategy(paramCamelContextNameStrategy);
	}

	public String getManagementName() {
		return camel.getManagementName();
	}

	public void setManagementName(String paramString) {
		camel.setManagementName(paramString);
	}

	public String getVersion() {
		return camel.getVersion();
	}

	public ServiceStatus getStatus() {
		return camel.getStatus();
	}

	public String getUptime() {
		return camel.getUptime();
	}

	public void addService(Object paramObject) throws Exception {
		camel.addService(paramObject);
	}

	public boolean hasService(Object paramObject) {
		return camel.hasService(paramObject);
	}

	public void addStartupListener(StartupListener paramStartupListener)
			throws Exception {
		camel.addStartupListener(paramStartupListener);
	}

	public void addComponent(String paramString, Component paramComponent) {
		camel.addComponent(paramString, paramComponent);
	}

	public Component hasComponent(String paramString) {
		return camel.hasComponent(paramString);
	}

	public Component getComponent(String paramString) {
		return camel.getComponent(paramString);
	}

	public <T extends Component> T getComponent(String paramString,
			Class<T> paramClass) {
		return camel.getComponent(paramString, paramClass);
	}

	public List<String> getComponentNames() {
		return camel.getComponentNames();
	}

	public Component removeComponent(String paramString) {
		return camel.removeComponent(paramString);
	}

	public Endpoint getEndpoint(String paramString) {
		return camel.getEndpoint(paramString);
	}

	public <T extends Endpoint> T getEndpoint(String paramString,
			Class<T> paramClass) {
		return camel.getEndpoint(paramString, paramClass);
	}

	public Collection<Endpoint> getEndpoints() {
		return camel.getEndpoints();
	}

	public Map<String, Endpoint> getEndpointMap() {
		return camel.getEndpointMap();
	}

	public Endpoint hasEndpoint(String paramString) {
		return camel.hasEndpoint(paramString);
	}

	public Endpoint addEndpoint(String paramString, Endpoint paramEndpoint)
			throws Exception {
		return camel.addEndpoint(paramString, paramEndpoint);
	}

	public Collection<Endpoint> removeEndpoints(String paramString)
			throws Exception {
		return camel.removeEndpoints(paramString);
	}

	public void addRegisterEndpointCallback(
			EndpointStrategy paramEndpointStrategy) {
		camel.addRegisterEndpointCallback(paramEndpointStrategy);
	}

	public List<RouteDefinition> getRouteDefinitions() {
		return camel.getRouteDefinitions();
	}

	public RouteDefinition getRouteDefinition(String paramString) {
		return camel.getRouteDefinition(paramString);
	}

	public List<Route> getRoutes() {
		return camel.getRoutes();
	}

	public Route getRoute(String paramString) {
		return camel.getRoute(paramString);
	}

	public void addRoutes(RoutesBuilder paramRoutesBuilder) throws Exception {
		camel.addRoutes(paramRoutesBuilder);
	}

	public RoutesDefinition loadRoutesDefinition(InputStream paramInputStream)
			throws Exception {
		return camel.loadRoutesDefinition(paramInputStream);
	}

	public void addRouteDefinitions(Collection<RouteDefinition> paramCollection)
			throws Exception {
		camel.addRouteDefinitions(paramCollection);
	}

	public void addRouteDefinition(RouteDefinition paramRouteDefinition)
			throws Exception {
		camel.addRouteDefinition(paramRouteDefinition);
	}

	public void removeRouteDefinitions(
			Collection<RouteDefinition> paramCollection) throws Exception {
		camel.removeRouteDefinitions(paramCollection);
	}

	public void removeRouteDefinition(RouteDefinition paramRouteDefinition)
			throws Exception {
		camel.removeRouteDefinition(paramRouteDefinition);
	}

	public void startRoute(RouteDefinition paramRouteDefinition)
			throws Exception {
		camel.startRoute(paramRouteDefinition);
	}

	public void startRoute(String paramString) throws Exception {
		camel.startRoute(paramString);
	}

	public void stopRoute(RouteDefinition paramRouteDefinition)
			throws Exception {
		camel.stopRoute(paramRouteDefinition);
	}

	public void stopRoute(String paramString) throws Exception {
		camel.stopRoute(paramString);
	}

	public void stopRoute(String paramString, long paramLong,
			TimeUnit paramTimeUnit) throws Exception {
		camel.stopRoute(paramString, paramLong, paramTimeUnit);
	}

	public boolean stopRoute(String paramString, long paramLong,
			TimeUnit paramTimeUnit, boolean paramBoolean) throws Exception {
		return camel.stopRoute(paramString, paramLong, paramTimeUnit,
				paramBoolean);
	}

	@Deprecated
	public void shutdownRoute(String paramString) throws Exception {
		camel.shutdownRoute(paramString);
	}

	@Deprecated
	public void shutdownRoute(String paramString, long paramLong,
			TimeUnit paramTimeUnit) throws Exception {
		camel.shutdownRoute(paramString, paramLong, paramTimeUnit);
	}

	public boolean removeRoute(String paramString) throws Exception {
		return camel.removeRoute(paramString);
	}

	public void resumeRoute(String paramString) throws Exception {
		camel.resumeRoute(paramString);
	}

	public void suspendRoute(String paramString) throws Exception {
		camel.suspendRoute(paramString);
	}

	public void suspendRoute(String paramString, long paramLong,
			TimeUnit paramTimeUnit) throws Exception {
		camel.suspendRoute(paramString, paramLong, paramTimeUnit);
	}

	public ServiceStatus getRouteStatus(String paramString) {
		return camel.getRouteStatus(paramString);
	}

	public boolean isStartingRoutes() {
		return camel.isStartingRoutes();
	}

	public TypeConverter getTypeConverter() {
		return camel.getTypeConverter();
	}

	public TypeConverterRegistry getTypeConverterRegistry() {
		return camel.getTypeConverterRegistry();
	}

	public Registry getRegistry() {
		return camel.getRegistry();
	}

	public Injector getInjector() {
		return camel.getInjector();
	}

	public List<LifecycleStrategy> getLifecycleStrategies() {
		return camel.getLifecycleStrategies();
	}

	public void addLifecycleStrategy(LifecycleStrategy paramLifecycleStrategy) {
		camel.addLifecycleStrategy(paramLifecycleStrategy);
	}

	public Language resolveLanguage(String paramString) {
		return camel.resolveLanguage(paramString);
	}

	public String resolvePropertyPlaceholders(String paramString)
			throws Exception {
		return camel.resolvePropertyPlaceholders(paramString);
	}

	public List<String> getLanguageNames() {
		return camel.getLanguageNames();
	}

	public ProducerTemplate createProducerTemplate() {
		return camel.createProducerTemplate();
	}

	public ProducerTemplate createProducerTemplate(int paramInt) {
		return camel.createProducerTemplate(paramInt);
	}

	public ConsumerTemplate createConsumerTemplate() {
		return camel.createConsumerTemplate();
	}

	public ConsumerTemplate createConsumerTemplate(int paramInt) {
		return camel.createConsumerTemplate(paramInt);
	}

	public void addInterceptStrategy(InterceptStrategy paramInterceptStrategy) {
		camel.addInterceptStrategy(paramInterceptStrategy);
	}

	public List<InterceptStrategy> getInterceptStrategies() {
		return camel.getInterceptStrategies();
	}

	public ErrorHandlerBuilder getErrorHandlerBuilder() {
		return camel.getErrorHandlerBuilder();
	}

	public void setErrorHandlerBuilder(
			ErrorHandlerBuilder paramErrorHandlerBuilder) {
		camel.setErrorHandlerBuilder(paramErrorHandlerBuilder);
	}

	public void setDataFormats(Map<String, DataFormatDefinition> paramMap) {
		camel.setDataFormats(paramMap);
	}

	public Map<String, DataFormatDefinition> getDataFormats() {
		return camel.getDataFormats();
	}

	public DataFormat resolveDataFormat(String paramString) {
		return camel.resolveDataFormat(paramString);
	}

	public DataFormatDefinition resolveDataFormatDefinition(String paramString) {
		return camel.resolveDataFormatDefinition(paramString);
	}

	public DataFormatResolver getDataFormatResolver() {
		return camel.getDataFormatResolver();
	}

	public void setDataFormatResolver(DataFormatResolver paramDataFormatResolver) {
		camel.setDataFormatResolver(paramDataFormatResolver);
	}

	public void setProperties(Map<String, String> paramMap) {
		camel.setProperties(paramMap);
	}

	public Map<String, String> getProperties() {
		return camel.getProperties();
	}

	public FactoryFinder getDefaultFactoryFinder() {
		return camel.getDefaultFactoryFinder();
	}

	public void setFactoryFinderResolver(
			FactoryFinderResolver paramFactoryFinderResolver) {
		camel.setFactoryFinderResolver(paramFactoryFinderResolver);
	}

	public FactoryFinder getFactoryFinder(String paramString)
			throws NoFactoryAvailableException {
		return camel.getFactoryFinder(paramString);
	}

	public ClassResolver getClassResolver() {
		return camel.getClassResolver();
	}

	public PackageScanClassResolver getPackageScanClassResolver() {
		return camel.getPackageScanClassResolver();
	}

	public void setClassResolver(ClassResolver paramClassResolver) {
		camel.setClassResolver(paramClassResolver);
	}

	public void setPackageScanClassResolver(
			PackageScanClassResolver paramPackageScanClassResolver) {
		camel.setPackageScanClassResolver(paramPackageScanClassResolver);
	}

	public void setProducerServicePool(
			ServicePool<Endpoint, Producer> paramServicePool) {
		camel.setProducerServicePool(paramServicePool);
	}

	public ServicePool<Endpoint, Producer> getProducerServicePool() {
		return camel.getProducerServicePool();
	}

	public void setNodeIdFactory(NodeIdFactory paramNodeIdFactory) {
		camel.setNodeIdFactory(paramNodeIdFactory);
	}

	public NodeIdFactory getNodeIdFactory() {
		return camel.getNodeIdFactory();
	}

	public ManagementStrategy getManagementStrategy() {
		return camel.getManagementStrategy();
	}

	public void setManagementStrategy(ManagementStrategy paramManagementStrategy) {
		camel.setManagementStrategy(paramManagementStrategy);
	}

	public InterceptStrategy getDefaultTracer() {
		return camel.getDefaultTracer();
	}

	public void setDefaultTracer(InterceptStrategy paramInterceptStrategy) {
		camel.setDefaultTracer(paramInterceptStrategy);
	}

	public void disableJMX() {
		camel.disableJMX();
	}

	public InflightRepository getInflightRepository() {
		return camel.getInflightRepository();
	}

	public void setInflightRepository(InflightRepository paramInflightRepository) {
		camel.setInflightRepository(paramInflightRepository);
	}

	public ClassLoader getApplicationContextClassLoader() {
		return camel.getApplicationContextClassLoader();
	}

	public void setApplicationContextClassLoader(ClassLoader paramClassLoader) {
		camel.setApplicationContextClassLoader(paramClassLoader);
	}

	public ShutdownStrategy getShutdownStrategy() {
		return camel.getShutdownStrategy();
	}

	public void setShutdownStrategy(ShutdownStrategy paramShutdownStrategy) {
		camel.setShutdownStrategy(paramShutdownStrategy);
	}

	public ExecutorServiceStrategy getExecutorServiceStrategy() {
		return camel.getExecutorServiceStrategy();
	}

	public void setExecutorServiceStrategy(
			ExecutorServiceStrategy paramExecutorServiceStrategy) {
		camel.setExecutorServiceStrategy(paramExecutorServiceStrategy);
	}

	public ProcessorFactory getProcessorFactory() {
		return camel.getProcessorFactory();
	}

	public void setProcessorFactory(ProcessorFactory paramProcessorFactory) {
		camel.setProcessorFactory(paramProcessorFactory);
	}

	public Debugger getDebugger() {
		return camel.getDebugger();
	}

	public void setDebugger(Debugger paramDebugger) {
		camel.setDebugger(paramDebugger);
	}

	public UuidGenerator getUuidGenerator() {
		return camel.getUuidGenerator();
	}

	public void setUuidGenerator(UuidGenerator paramUuidGenerator) {
		camel.setUuidGenerator(paramUuidGenerator);
	}

	public Boolean isLazyLoadTypeConverters() {
		return camel.isLazyLoadTypeConverters();
	}

	public void setLazyLoadTypeConverters(Boolean paramBoolean) {
		camel.setLazyLoadTypeConverters(paramBoolean);
	}

	public Boolean isUseMDCLogging() {
		return camel.isUseMDCLogging();
	}

	public void setUseMDCLogging(Boolean paramBoolean) {
		camel.setUseMDCLogging(paramBoolean);
	}

}
