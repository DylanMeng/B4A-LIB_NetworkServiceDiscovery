Public Sub SearchForDevices
	
	Dim nsd As NetworkServiceDiscovery
	nsd.Initialize("nsd")
	nsd.setService("SMART-D-LED", "_sdledV1._tcp.", 5353)
	nsd.discoverServices()
	'nsd.registerService()
	
End Sub

'Resolve Listener
Sub nsd_onResolveFailed(aErrorCode As Int)
	Log("NetworkServiceDiscovery - Resolve failed - Error code:" & aErrorCode)
End Sub

Sub nsd_onServiceResolved(aService As String, aServiceName As String, aServiceHost As String, aServicePort As String)
	Log("NetworkServiceDiscovery - Resolve Succeeded")
	Log("Smart D-LED device discovered at" & aServiceHost & ":" & aServicePort)
End Sub

'Registration Listener
Sub nsd_onServiceRegistered(aServiceName As String)
	Log("NetworkServiceDiscovery - Service registered: " & aServiceName)
End Sub

Sub nsd_onRegistrationFailed(aErrorCode As Int)
	Log("NetworkServiceDiscovery - Service registration failed - Error code: " & aErrorCode)
End Sub

Sub nsd_onServiceUnregistered(aServiceName As String)
	Log("NetworkServiceDiscovery - Service unregistered: " & aServiceName)
End Sub

Sub nsd_onUnregistrationFailed(aErrorCode As Int)
	Log("NetworkServiceDiscovery - Service unregistration failed - Error code: " & aErrorCode)
End Sub

'Discovery Listener
Sub nsd_onDiscoveryStarted(aRegType As String)
	Log("NetworkServiceDiscovery - Service discovery started: " & aRegType)
End Sub

Sub nsd_onServiceFound(aService As String, aServiceName As String, aServiceType As String)
	Log("NetworkServiceDiscovery - Service discovery success: " & aService)
End Sub

Sub nsd_onServiceLost(aService As String)
	Log("NetworkServiceDiscovery - Service lost: " & aService)
End Sub

Sub nsd_onDiscoveryStopped(aServiceType As String)
	Log("NetworkServiceDiscovery - Discovery stopped: " & aServiceType)
End Sub

Sub nsd_onStartDiscoveryFailed(aErrorCode As Int)
	Log("NetworkServiceDiscovery - On start Discovery failed - Error code: " & aErrorCode)
End Sub

Sub nsd_onStopDiscoveryFailed(aErrorCode As Int)
	Log("NetworkServiceDiscovery - On Stop Discovery failed - Error code:" & aErrorCode)
End Sub
