package b4a.dylanmeng.networkservicediscovery;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.Version;

import android.net.nsd.NsdServiceInfo;
import android.content.Context;
import android.net.nsd.NsdManager;


@ShortName("NetworkServiceDiscovery")
@Version(1.01f)
public class NetworkServiceDiscovery {
	
	//Attributes
	private BA ba;
	private String eventName;
	
	NsdManager mNsdManager;
	NsdManager.ResolveListener mResolveListener;
	NsdManager.DiscoveryListener mDiscoveryListener;
	NsdManager.RegistrationListener mRegistrationListener;
	public NsdServiceInfo mService;
    
    public static final String tag = "NetworkServiceDiscovery";
    public String mServiceName = "NsdChat";
    public String mServiceType = "_http._tcp.";
    public int mServicePort = 5353;
        
       
	/**
	 * Initializes the NetworkServiceDiscovery class.
	 */
	public void Initialize(final BA ba, String EventName) {
		this.ba = ba;
		this.eventName = EventName.toLowerCase(BA.cul);
		mNsdManager = (NsdManager) BA.applicationContext.getSystemService(Context.NSD_SERVICE);
	}

	/**
	 * 
	 * @param aServiceName Name of the service (aName)
	 * @param aServiceType Type of the service ("_http._tcp.")
	 * @param aServicePort Port of the service (5353)
	 */
    public void setupService(String aServiceName, String aServiceType, int aServicePort) {
    	mServiceName = aServiceName;
    	mServiceType = aServiceType;
    	mServicePort = aServicePort;
    }
    
    /**
     * 
     */
    public void initializeNsd() {
        initializeResolveListener();
        //mNsdManager.init(mContext.getMainLooper(), this);
    }
    
    /**
     * 
     */
    public void initializeDiscoveryListener() {
		BA.LogInfo("NetworkServiceDiscovery - Initializing discovery listener"); 
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
           
        	@Override
            public void onDiscoveryStarted(String regType) {
        		if (ba.subExists(eventName + "_onDiscoveryStarted")==true) {
        			 ba.raiseEvent(this, eventName + "_onDiscoveryStarted", new Object[] { regType, "allo" } );
        		}
        		BA.LogInfo(tag + " - Service discovery started");
            }
        	
        	
            @Override
            public void onServiceFound(NsdServiceInfo service) {
            	BA.LogInfo(tag + " - Service Found");
            	BA.LogInfo(tag + service.toString());
            	BA.LogInfo(tag + service.getClass());
            	if (ba.subExists(eventName + "_onservicefound")) {
            		 ba.raiseEvent(service, eventName + "_onservicefound");
	            	BA.LogInfo(" - Service discovery success" + service);
	                if (!service.getServiceType().equals(mServiceType)) {
	                	BA.LogInfo(tag + " - Unknown Service Type: " + service.getServiceType());
	                } else if (service.getServiceName().equals(mServiceName)) {
	                	BA.LogInfo(tag + " - Same machine: " + mServiceName);
	                } else if (service.getServiceName().contains(mServiceName)){
	                    mNsdManager.resolveService(service, mResolveListener);
	                }
            	}
            }
            
            @Override
            public void onServiceLost(NsdServiceInfo service) {
            	BA.LogInfo(tag + " - Service lost" + service);
                if (mService == service) {
                    mService = null;
                }
            }
            
            @Override
            public void onDiscoveryStopped(String serviceType) {
            	BA.LogInfo(tag + " - Discovery stopped: " + serviceType);
            }
            
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            	BA.LogInfo(tag + " - Discovery failed: Error code:" + errorCode);
            	if (ba.subExists(eventName + "_onStartDiscoveryFailed")==true) {
            		 ba.raiseEvent(this, eventName + "_onStartDiscoveryFailed", new Object[] { mServiceName, "allo" } );
            	}
            }
            
            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            	BA.LogInfo(tag + " - Discovery failed: Error code:" + errorCode);
            }
        };
    }
    
    /**
     * Initialize the resolve listener
     */
    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {
           
        	@Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            	BA.LogInfo(tag + " - Resolve failed" + errorCode);
            }
        	
            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
            	BA.LogInfo(tag + " - Resolve Succeeded. " + serviceInfo);
                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    BA.LogInfo("Same IP.");
                    return;
                }
                mService = serviceInfo;
            }
            
        };
    }
    
    
    /**
     * Initialize the registration listener
     */
    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {
            
        	@Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
            	if (ba.subExists(eventName + "_onserviceregistered")) {
                    mServiceName = NsdServiceInfo.getServiceName();
            		ba.raiseEvent(this, eventName + "_onserviceregistered", mServiceName);
            	}   
            }
        	
            @Override
            public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
            	if (ba.subExists(eventName + "_onregistrationfailed")) {
            		ba.raiseEvent(this, eventName + "_onregistrationfailed", arg1);
            	}   
            }
            
            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
            	if (ba.subExists(eventName + "_onserviceunregistered")) {
            		ba.raiseEvent(this, eventName + "_onserviceunregistered", arg0.getServiceName());
            	}   
            }
            
            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            	if (ba.subExists(eventName + "_onunregistrationfailed")) {
            		ba.raiseEvent(this, eventName + "_onunregistrationfailed", errorCode);
            	}      
            }
            
        };
    }
    
	/**
	 * 
	 */
    public void registerService() {
        tearDown();  // Cancel any previous registration request
        initializeRegistrationListener();
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setPort(mServicePort);
        serviceInfo.setServiceName(mServiceName);
        serviceInfo.setServiceType(mServiceType);
        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }
    
    /**
     * Cancel any existing discovery request then initialize the discovery listener
     */
    public void discoverServices() {
        stopDiscovery();  // Cancel any existing discovery request
        initializeDiscoveryListener();
        mNsdManager.discoverServices(mServiceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);   
    }
    
    /**
     *  Stop discovery service
     */
    public void stopDiscovery() {
        if (mDiscoveryListener != null) {
            try {
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            } finally {
            }
            mDiscoveryListener = null;
        }
    }
      
    /**
     *  Tear down service
     */
    public void tearDown() {
        if (mRegistrationListener != null) {
            try {
                mNsdManager.unregisterService(mRegistrationListener);
            } finally {
            }
            mRegistrationListener = null;
        }
    }
}