package b4a.dylanmeng.networkservicediscovery;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.Version;

import android.net.nsd.NsdServiceInfo;
import android.content.Context;
import android.net.nsd.NsdManager;


@ShortName("NetworkServiceDiscovery")
@Version(1.00f)
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
		initializeResolveListener();
	}

	/**
	 * 
	 * @param aServiceName Name of the service ("aName")
	 * @param aServiceType Type of the service ("_http._tcp.")
	 * @param aServicePort Port of the service (5353)
	 */
    public void setService(String aServiceName, String aServiceType, int aServicePort) {
    	mServiceName = aServiceName;
    	mServiceType = aServiceType;
    	mServicePort = aServicePort;
    }
    
    /**
     * 
     */
    public void initializeNsd() {
        initializeResolveListener();
    }
    
    /**
     * Initialize the discovery listener
     */
    public void initializeDiscoveryListener() {

        mDiscoveryListener = new NsdManager.DiscoveryListener() {
           
        	@Override
            public void onDiscoveryStarted(String regType) {
        		if (ba.subExists(eventName + "_ondiscoverystarted")) {
        			ba.raiseEvent(this, eventName + "_ondiscoverystarted", regType);
            	}  
            }
        	
        	
            @Override
            public void onServiceFound(NsdServiceInfo service) {
            	if (ba.subExists(eventName + "_onservicefound")) {
            		if (service.getServiceName().contains(mServiceName)) {
            			mNsdManager.resolveService(service, mResolveListener);
            		}        
        			ba.raiseEvent(this, eventName + "_onservicefound", service.toString(), 
        					service.getServiceName(), 
        					service.getHost());
            	}  
            }
            
            @Override
            public void onServiceLost(NsdServiceInfo service) {
            	if (ba.subExists(eventName + "_onservicelost")) {
        			ba.raiseEvent(this, eventName + "_onservicelost", service.toString());
            	}  
            }
            
            @Override
            public void onDiscoveryStopped(String serviceType) {
            	if (ba.subExists(eventName + "_ondiscoverystopped")) {
        			ba.raiseEvent(this, eventName + "_ondiscoverystopped", serviceType);
            	}  
            }
            
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            	if (ba.subExists(eventName + "_onstartdiscoveryfailed")) {
        			ba.raiseEvent(this, eventName + "_onstartdiscoveryfailed", errorCode);
            	}  
            }
            
            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            	if (ba.subExists(eventName + "_onstopdiscoveryfailed")) {
        			ba.raiseEvent(this, eventName + "_onstopdiscoveryfailed", errorCode);
            	}  
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
        	  	if (ba.subExists(eventName + "_onresolvefailed")) {
        			ba.raiseEvent(this, eventName + "_onresolvefailed", errorCode);
            	}  
            }
        	
            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
        	  	if (ba.subExists(eventName + "_onserviceresolved")) {
        			ba.raiseEvent(this, eventName + "_onserviceresolved", serviceInfo.toString(),
        					serviceInfo.getServiceName(), serviceInfo.getHost().toString(), serviceInfo.getPort());
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